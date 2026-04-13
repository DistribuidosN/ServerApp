package enfok.server.model.entity.bd;

import java.sql.Timestamp;

public class Node {
    private int id;
    private String nodeId;
    private String host;
    private int port;
    private int statusId;
    private Timestamp lastSignal;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }
    public Timestamp getLastSignal() { return lastSignal; }
    public void setLastSignal(Timestamp lastSignal) { this.lastSignal = lastSignal; }
}
