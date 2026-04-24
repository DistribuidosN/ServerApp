package enfok.server.utility;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import enfok.server.model.grpc.ImageQueue;

/**
 * Cola central de tareas con prioridad (Shortest Job First).
 * Maneja el estado de las tareas y el monitoreo (Heartbeats) de los workers
 * activos.
 */
@ApplicationScoped
public class TaskQueue {

    // ── Cola principal ────────────────────────────────────────────────────────
    private final PriorityBlockingQueue<ImageQueue> queue = new PriorityBlockingQueue<>();

    // ── Estado por tarea: taskId → "QUEUED" | "RUNNING" | "DONE" | "FAILED" ─
    private final Map<String, String> statusMap = new ConcurrentHashMap<>();

    // ── Heartbeats: nodeId → época del último ping ────────────────────────────
    private final Map<String, Long> lastSeen = new ConcurrentHashMap<>();
    private final Map<String, Boolean> nodeAccepts = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────────────────────────────────────
    // Encolar
    // ─────────────────────────────────────────────────────────────────────────
    public void addNewImageTask(String id, int width, int height, String imageFormat, double costMultiplier,
            byte[] imageBytes, List<enfok.server.model.entity.dto.node.TransformationItem> filters, String filename) {
        queue.offer(new ImageQueue(id, width, height, imageFormat, costMultiplier, imageBytes, filters, filename));
        statusMap.put(id, "QUEUED");
    }


    // ─────────────────────────────────────────────────────────────────────────
    // Desencolar
    // ─────────────────────────────────────────────────────────────────────────
    /** Saca la tarea de menor costo estimado. Devuelve null si vacía. */
    public ImageQueue poll() {
        ImageQueue t = queue.poll();
        if (t != null) {
            statusMap.put(t.id, "RUNNING");
        }
        return t;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Estado de Tareas
    // ─────────────────────────────────────────────────────────────────────────
    public void setStatus(String id, String status) {
        statusMap.put(id, status);
    }

    public String getStatus(String id) {
        return statusMap.getOrDefault(id, "UNKNOWN");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Heartbeats y Monitoreo de Nodos
    // ─────────────────────────────────────────────────────────────────────────
    /** 
     * Registra o actualiza un nodo en memoria. 
     * Retorna "REGISTERED" si es la primera vez que lo vemos, o "UPDATED" si ya existía.
     */
    public String registerNode(String nodeId, String ip, int port, boolean accepting) {
        boolean exists = lastSeen.containsKey(nodeId);
        lastSeen.put(nodeId, System.currentTimeMillis());
        nodeAccepts.put(nodeId, accepting);
        return exists ? "UPDATED" : "REGISTERED";
    }

    public void heartbeat(String nodeId, String ip, boolean accepting) {
        lastSeen.put(nodeId, System.currentTimeMillis());
        nodeAccepts.put(nodeId, accepting);
    }


    public boolean isAlive(String nodeId, long maxAgeMs) {
        Long t = lastSeen.get(nodeId);
        return t != null && (System.currentTimeMillis() - t) < maxAgeMs;
    }

    public boolean accepts(String nodeId) {
        return Boolean.TRUE.equals(nodeAccepts.get(nodeId));
    }

    public Set<String> nodes() {
        return lastSeen.keySet();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Info general
    // ─────────────────────────────────────────────────────────────────────────
    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}