package enfok.server.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;

// Clases generadas desde OrquestatorGrpsc.proto (java_package = "enfok.worker.proto")
import enfok.worker.proto.MutinyOrchestratorGrpc;
import enfok.worker.proto.ImageTask;
import enfok.worker.proto.NodeMetrics;
import enfok.worker.proto.NodeQueueInfo;
import enfok.worker.proto.PullRequest;
import enfok.worker.proto.PullResponse;
import enfok.worker.proto.QueueStatusRequest;
import enfok.worker.proto.QueueStatusResponse;
import enfok.worker.proto.StealRequest;
import enfok.worker.proto.StealResponse;
import enfok.worker.proto.TaskProgress;
import enfok.worker.proto.TaskResult;
import enfok.worker.proto.HeartbeatRequest;
import enfok.worker.proto.Ack;

import enfok.server.model.grpc.ImageQueue;
import enfok.server.utility.TaskQueue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;

/**
 * [ADAPTER - IN / gRPC SERVER]: Servicio {@code Orchestrator} expuesto en el
 * puerto 9000.
 *
 * Implementa los 7 RPCs que los nodos Python llaman:
 *
 * 1. PullTasks — A2WS PULL: el worker pide slots_free tareas
 * 2. SubmitResult — el worker entrega el resultado procesado
 * 3. StealTasks — árbitro de work-stealing entre nodos
 * 4. UpdateTaskProgress — progreso en tiempo real (0–100 %)
 * 5. SendHeartbeat — ping periódico "sigo vivo"
 * 6. GetQueueStatus — snapshot de la cola central y todos los nodos
 * 7. WatchQueue — stream reactivo del estado (cada 2 s)
 */
@GrpcService
public class OrquestatorNode extends MutinyOrchestratorGrpc.OrchestratorImplBase {

    private static final Logger log = Logger.getLogger(OrquestatorNode.class.getName());

    /** Nodo considerado muerto si no llega heartbeat en 30 s */
    private static final long DEAD_MS = 30_000;

    /** Capacidad máxima de la cola central (para reportar en status) */
    private static final int QUEUE_CAP = 1_000;

    @Inject
    TaskQueue taskQueue;

    // =========================================================================
    // 1. PULL TASKS → El worker Python llama esto para pedir trabajo
    // Es el método más importante del sistema A2WS.
    // El nodo dice "tengo N slots libres" → le devolvemos hasta N tareas.
    // =========================================================================

    @Override
    public Uni<PullResponse> pullTasks(PullRequest request) {
        String nodeId = request.getNodeId();
        int slotsFree = Math.max(1, request.getSlotsFree());

        log.info(String.format("[PULL] nodo=%-10s  pide=%d slot(s)  cola=%d",
                nodeId, slotsFree, taskQueue.size()));

        // ── Sacar hasta slotsFree tareas (Shortest Job First) ─────────────────
        List<ImageTask> dispatched = new ArrayList<>();

        for (int i = 0; i < slotsFree; i++) {
            ImageQueue item = taskQueue.poll(); // SJF: el menor costo sale primero
            if (item == null)
                break; // cola vacía → dejamos de sacar

            // 1. Unimos todos los filtros separados por comas (ej: "blur,ocr,grayscale")
            String filtrosAplicar = (item.transformations != null && !item.transformations.isEmpty())
                    ? String.join(",", item.transformations)
                    : "thumbnail"; // default de seguridad

            // 2. Extraemos los bytes a una variable nueva de tipo ByteString de gRPC
            com.google.protobuf.ByteString grpcImageData = com.google.protobuf.ByteString.copyFrom(item.imageBytes);

            // 3. Empaquetamos la tarea para enviarla por gRPC
            dispatched.add(
                    ImageTask.newBuilder()
                            .setTaskId(item.id)
                            .setFilename(item.filename)
                            .setFilterType(filtrosAplicar) // Asignamos el filtro real
                            .setImageData(grpcImageData) // <-- ¡Inyectamos la nueva variable limpia!
                            .setTargetWidth(item.width)
                            .setTargetHeight(item.height)
                            .setEnqueueTs(System.currentTimeMillis())
                            .setPriority(0)
                            .build());
        }

        boolean queueDry = taskQueue.isEmpty();

        if (dispatched.isEmpty()) {
            log.info(String.format("[PULL] nodo=%-10s  → cola SECA", nodeId));
        } else {
            log.info(String.format("[PULL] nodo=%-10s  → despachadas %d  |  restante=%d",
                    nodeId, dispatched.size(), taskQueue.size()));
        }

        return Uni.createFrom().item(
                PullResponse.newBuilder()
                        .addAllTasks(dispatched)
                        .setQueueDry(queueDry)
                        .build());
    }

    // =========================================================================
    // 2. SUBMIT RESULT → El worker entrega la imagen procesada (o error)
    // =========================================================================

    @Override
    public Uni<Ack> submitResult(TaskResult result) {
        String taskId = result.getTaskId();
        String nodeId = result.getNodeId();
        boolean success = result.getSuccess();
        int ms = result.getProcessingMs();
        Map<String, String> metadata = result.getMetadataMap();

        if (success) {
            taskQueue.setStatus(taskId, "DONE");

            // 1. Extraemos los bytes puros que nos mandó Python
            byte[] imageBytes = result.getResultData().toByteArray();

            log.info(String.format("[SUBMIT] ✔ task=%s  nodo=%s  tiempo=%dms  bytes=%d",
                    taskId, nodeId, ms, imageBytes.length));

            // 2. Lógica para guardar la imagen en disco
            try {
                // 1. Obtenemos la ruta absoluta de la raíz del proyecto
                String projectRoot = System.getProperty("user.dir");

                // 2. Anclamos la carpeta "imagenes" a esa raíz
                Path dirPath = Paths.get(projectRoot, "imagenes");

                // 3. Creamos la carpeta si no existe
                if (!Files.exists(dirPath)) {
                    Files.createDirectories(dirPath);
                }

                // 4. Resolvemos el nombre del archivo final
                Path filePath = dirPath.resolve(taskId + "_procesada.png");

                // 5. Guardamos
                Files.write(filePath, imageBytes);

                log.info("[SUBMIT] Imagen guardada en: " + filePath.toAbsolutePath());
                log.info("[SUBMIT] Metadatos recibidos: " + metadata);

            } catch (Exception e) {
                log.severe("[SUBMIT] ¡Error crítico al guardar la imagen en disco!: " + e.getMessage());
            }

        } else {
            taskQueue.setStatus(taskId, "FAILED");
            log.warning(String.format("[SUBMIT] ✘ task=%s  nodo=%s  error=%s",
                    taskId, nodeId, result.getErrorMsg()));
            // (Futuro) re-encolar si attempt < maxRetries
        }

        return Uni.createFrom().item(Ack.newBuilder().setOk(true).setMsg("Resultado guardado").build());
    }

    // =========================================================================
    // 3. STEAL TASKS → Árbitro: autoriza o deniega el work-stealing
    // El ladrón nos pide permiso antes de contactar al nodo víctima.
    // Si aprueba, el ladrón llama YieldTasks directamente a la víctima.
    // =========================================================================

    @Override
    public Uni<StealResponse> stealTasks(StealRequest request) {
        String thief = request.getThiefNodeId();
        String victim = request.getVictimNodeId();
        int count = request.getStealCount();

        // ── Política A2WS: autorizar si la víctima está viva y acepta ─────────
        boolean victimAlive = taskQueue.isAlive(victim, DEAD_MS);
        boolean victimAccepts = taskQueue.accepts(victim);
        boolean allowed = victimAlive && victimAccepts && count > 0;

        String reason = !victimAlive ? "Nodo víctima no responde (muerto)"
                : !victimAccepts ? "Nodo víctima no acepta robo ahora"
                        : allowed ? "Robo autorizado — contacta al nodo víctima"
                                : "No se puede autorizar";

        log.info(String.format("[STEAL] ladrón=%-10s  víctima=%-10s  cantidad=%d  autorizado=%b",
                thief, victim, count, allowed));

        return Uni.createFrom().item(
                StealResponse.newBuilder()
                        .setAllowed(allowed)
                        .setReason(reason)
                        .build());
    }

    // =========================================================================
    // 4. UPDATE TASK PROGRESS → Progreso 0–100 % para la UI móvil
    // =========================================================================

    @Override
    public Uni<Ack> updateTaskProgress(TaskProgress progress) {
        String taskId = progress.getTaskId();
        int pct = progress.getProgressPercentage();
        String msg = progress.getStatusMessage();

        taskQueue.setStatus(taskId, "RUNNING");

        log.info(String.format("[PROGRESS] task=%s  worker=%s  %3d%%  → %s",
                taskId, progress.getWorkerId(), pct, msg));

        // (Futuro) eventBus.send("task.progress", ...) → WebSocket → app móvil

        return Uni.createFrom().item(ok("Progreso registrado"));
    }

    // =========================================================================
    // 5. SEND HEARTBEAT → Ping periódico del nodo Python
    // Si no llega en 30 s → nodo DOWN → re-encolar sus tareas (futuro)
    // =========================================================================

    @Override
    public Uni<Ack> sendHeartbeat(HeartbeatRequest request) {
        String nodeId = request.getNodeId();
        String ip = request.getIpAddress();
        NodeMetrics m = request.getMetrics();

        // Registramos el nodo como vivo y si tiene espacio en su cola local
        boolean nodeHasCapacity = m.getQueueSize() < m.getQueueCapacity();
        taskQueue.heartbeat(nodeId, ip, nodeHasCapacity);

        log.fine(String.format("[HB] ♥ nodo=%-10s  ip=%-15s  CPU=%.1f%%  cola=%d/%d  %s",
                nodeId, ip,
                m.getCpuPercent(),
                m.getQueueSize(), m.getQueueCapacity(),
                m.getStatus()));

        return Uni.createFrom().item(ok("OK"));
    }

    // =========================================================================
    // 6. GET QUEUE STATUS → Snapshot completo para dashboard / A2WS
    // =========================================================================

    @Override
    public Uni<QueueStatusResponse> getQueueStatus(QueueStatusRequest request) {
        log.fine(String.format("[STATUS] solicitante=%s", request.getRequesterId()));
        return Uni.createFrom().item(buildSnapshot());
    }

    // =========================================================================
    // 7. WATCH QUEUE → Stream reactivo: emite snapshot cada 2 s
    // Los nodos se suscriben y reciben actualizaciones sin hacer polling.
    // =========================================================================

    @Override
    public Multi<QueueStatusResponse> watchQueue(QueueStatusRequest request) {
        String requester = request.getRequesterId();
        log.info(String.format("[WATCH] suscripción de '%s'", requester));

        return Multi.createFrom().ticks().every(Duration.ofSeconds(2))
                .map(tick -> buildSnapshot())
                .onCancellation().invoke(() -> log.info(String.format("[WATCH] '%s' desconectado", requester)));
    }

    // =========================================================================
    // Helpers privados
    // =========================================================================

    /** Construye un snapshot de la cola central + estado de todos los nodos. */
    private QueueStatusResponse buildSnapshot() {
        QueueStatusResponse.Builder snap = QueueStatusResponse.newBuilder()
                .setCentralQueueSize(taskQueue.size())
                .setCentralQueueCap(QUEUE_CAP)
                .setTimestamp(System.currentTimeMillis());

        for (String nodeId : taskQueue.nodes()) {
            boolean alive = taskQueue.isAlive(nodeId, DEAD_MS);
            boolean accepts = taskQueue.accepts(nodeId);
            snap.addNodes(
                    NodeQueueInfo.newBuilder()
                            .setNodeId(nodeId)
                            .setAvailable(alive && accepts)
                            .setStatus(alive ? (accepts ? "IDLE" : "DRAINING") : "DOWN")
                            .build());
        }
        return snap.build();
    }

    /** Ack genérico positivo. */
    private Ack ok(String msg) {
        return Ack.newBuilder().setOk(true).setMsg(msg).build();
    }
}
