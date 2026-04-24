package enfok.server.model.entity;

import java.time.LocalDateTime;

/**
 * Representa el estado volátil de un nodo en el orquestador.
 */
public class NodeState {
    private String nodeId;
    private String ipAddress;
    private int port;
    private LocalDateTime lastSeen;

    public NodeState(String nodeId, String ipAddress, int port) {
        this.nodeId = nodeId;
        this.ipAddress = ipAddress;
        this.port = port;
        this.lastSeen = LocalDateTime.now();
    }

    public String getNodeId() { return nodeId; }
    public String getIpAddress() { return ipAddress; }
    public int getPort() { return port; }
    public LocalDateTime getLastSeen() { return lastSeen; }

    public void update(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.lastSeen = LocalDateTime.now();
    }

    public void updateHeartbeat() {
        this.lastSeen = LocalDateTime.now();
    }
}

