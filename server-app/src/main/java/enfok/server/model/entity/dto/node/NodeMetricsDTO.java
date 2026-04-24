package enfok.server.model.entity.dto.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.sql.Timestamp;

/**
 * DTO para la trazabilidad de consumo de recursos específica para cada imagen procesada.
 */
public class NodeMetricsDTO {
    
    @JsonProperty("node_id")
    private String nodeId;
    
    @JsonProperty("image_uuid")
    private String imageUuid;
    
    @JsonProperty("ram_used_mb")
    private double ramUsedMb;
    
    @JsonProperty("cpu_percent")
    private double cpuPercent;
    
    @JsonProperty("avg_latency_ms")
    private Double avgLatencyMs;
    
    @JsonProperty("uptime_seconds")
    private long uptimeSeconds;
    
    @JsonProperty("reported_at")
    private Timestamp reportedAt;

    // Getters and Setters
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getImageUuid() { return imageUuid; }
    public void setImageUuid(String imageUuid) { this.imageUuid = imageUuid; }
    public double getRamUsedMb() { return ramUsedMb; }
    public void setRamUsedMb(double ramUsedMb) { this.ramUsedMb = ramUsedMb; }
    public double getCpuPercent() { return cpuPercent; }
    public void setCpuPercent(double cpuPercent) { this.cpuPercent = cpuPercent; }
    public Double getAvgLatencyMs() { return avgLatencyMs; }
    public void setAvgLatencyMs(Double avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }
    public long getUptimeSeconds() { return uptimeSeconds; }
    public void setUptimeSeconds(long uptimeSeconds) { this.uptimeSeconds = uptimeSeconds; }
    public Timestamp getReportedAt() { return reportedAt; }
    public void setReportedAt(Timestamp reportedAt) { this.reportedAt = reportedAt; }
}
