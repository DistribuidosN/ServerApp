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
import enfok.worker.proto.RegisterNodeRequest;
import enfok.worker.proto.RegisterNodeResponse;
import enfok.worker.proto.LogRequest;
import enfok.worker.proto.LogResponse;
import enfok.server.utility.NodeMemoryRegistry;
import enfok.server.model.entity.bd.LogRecord;
import enfok.server.model.entity.bd.NodeMetricsBd;
import enfok.server.ports.adapter.BdRepositoryInterface;
import enfok.server.model.entity.bd.Node;

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
 * Implementa los 8 RPCs que los nodos llaman:
 *
 * 1. PullTasks         — A2WS PULL: el worker pide slots_free tareas
 * 2. SubmitResult      — el worker entrega el resultado procesado
 * 3. StealTasks        — árbitro de work-stealing entre nodos
 * 4. UpdateTaskProgress— progreso en tiempo real (0–100 %)
 * 5. RegisterNode      — registro inicial idempotente (REGISTERED | UPDATED)
 * 6. SendHeartbeat     — ping periódico "sigo vivo"
 * 7. GetQueueStatus    — snapshot de la cola central y todos los nodos
 * 8. WatchQueue        — stream reactivo del estado (cada 2 s)
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

    @Inject
    NodeMemoryRegistry nodeMemoryRegistry;

    @Inject
    BdRepositoryInterface bdRepository;

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

            // 1. Construir el Pipeline dinámico (Nuevo Contrato)
            enfok.worker.proto.TransformationPipeline.Builder pipelineBuilder = enfok.worker.proto.TransformationPipeline.newBuilder();
            if (item.transformations != null) {
                for (enfok.server.model.entity.dto.node.TransformationItem trans : item.transformations) {
                    pipelineBuilder.addItems(enfok.worker.proto.TransformationItem.newBuilder()
                            .setType(trans.getName())
                            .setParamsJson(trans.getParams())
                            .build());
                }
            }

            // 2. Construir mensaje gRPC
            dispatched.add(ImageTask.newBuilder()
                    .setTaskId(item.id)
                    .setImageData(com.google.protobuf.ByteString.copyFrom(item.imageBytes))
                    .setFilename(item.filename)
                    .setTransformationPipeline(pipelineBuilder.build())
                    .setEnqueueTs(System.currentTimeMillis())
                    .setTargetWidth(item.width)
                    .setTargetHeight(item.height)
                    .setImageFormat(item.imageFormat)
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

            // 2. Persistencia síncrona en BD
            try {
                // [SENIOR FIX]: Enviamos los bytes puros y el nodeId. 
                // Go se encargará de subir a MinIO y guardar el path + trazabilidad.
                bdRepository.updateImageResult(taskId, imageBytes, nodeId);
                
                // Registramos las métricas de rendimiento recibidas del nodo
                if (result.hasMetrics()) {
                    log.info("[METRICS] Vinculando métricas de rendimiento a la imagen: " + taskId);
                    bdRepository.createMetrics(mapMetrics(nodeId, taskId, result.getMetrics()));
                }

                log.info("[SUBMIT] Resultados y métricas persistidos con éxito.");
            } catch (Exception e) {
                log.severe("[SUBMIT] Error persistiendo resultados en BD: " + e.getMessage());
            }


        } else {
            taskQueue.setStatus(taskId, "FAILED");
            log.warning(String.format("[SUBMIT] ✘ task=%s  nodo=%s  error=%s",
                    taskId, nodeId, result.getErrorMsg()));
            
            // Persistencia en BD
            try {
                bdRepository.updateImageStatus(taskId, "FAILED");
            } catch (Exception e) {
                log.warning("[SUBMIT] Error al actualizar status FAILED en BD para task " + taskId);
            }
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
    // 5. REGISTER NODE → Registro inicial idempotente del nodo Go
    // El cliente llama esto al arrancar Y periódicamente como heartbeat de
    // registro. El servidor responde REGISTERED (primera vez) o UPDATED
    // (ya existía) para que el nodo sepa si fue reconocido como nuevo.
    // =========================================================================

    @Override
    public Uni<RegisterNodeResponse> registerNode(RegisterNodeRequest request) {
        String nodeId = request.getNodeId();
        String ip = request.getIpAddress();
        int port = request.getPort();
        NodeMetrics m = request.getMetrics();

        // 1. Memoria (Orquestador Central)
        String regStatus = nodeMemoryRegistry.registerOrUpdate(nodeId, ip, port);
        boolean isNew = "REGISTERED".equals(regStatus);

        // 2. Memoria (TaskQueue - Planificación)
        boolean hasCapacity = m.getQueueSize() < m.getQueueCapacity();
        taskQueue.registerNode(nodeId, ip, port, hasCapacity);

        // 3. Persistencia SÍNCRONA (Consistencia Garantizada)
        try {
            Node nodeBd = new Node();
            nodeBd.setNodeId(nodeId);
            nodeBd.setHost(ip);
            nodeBd.setPort(port);
            nodeBd.setStatusId(isNew ? 1 : 2); // 1: ACTIVO
            
            bdRepository.registerNode(nodeBd);
            bdRepository.createMetrics(mapMetrics(nodeId, null, m));
            
            log.fine("Persistencia de registro exitosa para " + nodeId);
        } catch (Exception e) {
            log.severe("Fallo crítico persistiendo nodo " + nodeId + ": " + e.getMessage());
            return Uni.createFrom().item(RegisterNodeResponse.newBuilder()
                    .setOk(false)
                    .setStatus("ERROR")
                    .setMsg("Error de persistencia: " + e.getMessage())
                    .build());
        }


        if (isNew) {
            log.info(String.format("[REG] ✚ NUEVO NODO: %s en %s:%d", nodeId, ip, port));
        } else {
            log.fine(String.format("[REG] ↺ ACTUALIZADO: %s en %s:%d", nodeId, ip, port));
        }

        return Uni.createFrom().item(
                RegisterNodeResponse.newBuilder()
                        .setOk(true)
                        .setStatus(regStatus)
                        .setMsg(isNew ? "Nodo registrado correctamente en el sistema A2WS" : "Configuración de red actualizada")
                        .build());
    }


    @Override
    public Uni<Ack> sendHeartbeat(HeartbeatRequest request) {
        String nodeId = request.getNodeId();
        String ip = request.getIpAddress();
        NodeMetrics m = request.getMetrics();

        // 1. Memoria (Rápido para Work Stealing & Watchdog)
        boolean nodeHasCapacity = m.getQueueSize() < m.getQueueCapacity();
        taskQueue.heartbeat(nodeId, ip, nodeHasCapacity);
        nodeMemoryRegistry.updateHeartbeat(nodeId);



        log.fine(String.format("[HB] ♥ nodo=%-10s  ip=%-15s  CPU=%.1f%%  cola=%d/%d",
                nodeId, ip, m.getCpuPercent(), m.getQueueSize(), m.getQueueCapacity()));

        return Uni.createFrom().item(ok("OK"));
    }


    private NodeMetricsBd mapMetrics(String nodeId, String imageUuid, NodeMetrics m) {
        NodeMetricsBd bd = new NodeMetricsBd();
        bd.setNodeId(nodeId);
        bd.setImageUuid(imageUuid);
        
        bd.setCpuPercent(m.getCpuPercent());
        bd.setRamUsedMb((double) m.getRamUsedMb());
        bd.setRamTotalMb((double) m.getRamTotalMb());
        bd.setWorkersBusy(m.getWorkersBusy());
        bd.setWorkersTotal(m.getWorkersTotal());
        bd.setQueueSize(m.getQueueSize());
        bd.setQueueCapacity(m.getQueueCapacity());
        bd.setTasksDone(m.getTasksDone());
        bd.setUptimeSeconds(m.getUptimeSeconds());
        bd.setStatus(m.getStatus());
        bd.setReportedAt(new java.sql.Timestamp(System.currentTimeMillis()));
        return bd;
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

    // 7. LOG EVENT → Centralización de logs de los nodos trabajadores
    // =========================================================================
    @Override
    public Uni<LogResponse> logEvent(LogRequest request) {
        String level = request.getLevel().toUpperCase();
        String message = request.getMessage();
        String nodeId = request.getNodeId();

        // 1. Filtrado: Solo almacenar logs importantes (Regla 4: NO DEBUG)
        if ("DEBUG".equals(level) || level.isEmpty()) {
            return Uni.createFrom().item(LogResponse.newBuilder().setOk(true).build());
        }

        // 2. Mapeo de Nivel a ID (Regla 3)
        int levelId = mapLogLevel(level);

        // 3. Persistencia Asíncrona (Regla 2 & 5)
        enfok.server.utility.Infrastructure.getDefaultWorkerPool().execute(() -> {
            try {
                // Validación básica de nodo
                if (nodeMemoryRegistry.getActiveNodes().containsKey(nodeId)) {
                    LogRecord logRecord = new LogRecord();
                    logRecord.setNodeId(nodeId);
                    logRecord.setImageUuid(request.getImageUuid());
                    logRecord.setLevelId(levelId);
                    logRecord.setLevelName(level);
                    logRecord.setMessage(message);
                    logRecord.setTransformationId(request.getTransformationId());
                    
                    bdRepository.createLog(logRecord);
                }
            } catch (Exception e) {
                // Regla 5: Loggear error en servidor sin afectar al nodo
                log.severe("[LOG-COLLECTOR] Error persistiendo log de " + nodeId + ": " + e.getMessage());
            }
        });

        return Uni.createFrom().item(LogResponse.newBuilder().setOk(true).build());
    }

    private int mapLogLevel(String level) {
        switch (level) {
            case "INFO": return 1;
            case "WARNING": return 2;
            case "ERROR": return 3;
            case "CRITICAL": return 4;
            default: return 1; // Fallback a INFO
        }
    }

    /** Ack genérico positivo. */
    private Ack ok(String msg) {
        return Ack.newBuilder().setOk(true).setMsg(msg).build();
    }
}

