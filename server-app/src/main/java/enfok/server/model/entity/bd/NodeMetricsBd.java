package enfok.server.model.entity.bd;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public class NodeMetricsBd {
    private long id;
    
    @JsonProperty("node_id")
    private String nodeId;
    
    @JsonProperty("image_uuid")
    private String imageUuid;
    
    @JsonProperty("ram_used_mb")
    private double ramUsedMb;
    
    @JsonProperty("ram_total_mb")
    private double ramTotalMb;
    
    @JsonProperty("cpu_percent")
    private double cpuPercent;
    
    @JsonProperty("workers_busy")
    private int workersBusy;
    
    @JsonProperty("workers_total")
    private int workersTotal;
    
    @JsonProperty("queue_size")
    private int queueSize;
    
    @JsonProperty("queue_capacity")
    private int queueCapacity;
    
    @JsonProperty("tasks_done")
    private int tasksDone;
    
    @JsonProperty("steals_performed")
    private int stealsPerformed;
    
    @JsonProperty("avg_latency_ms")
    private Double avgLatencyMs;
    
    @JsonProperty("p95_latency_ms")
    private Double p95LatencyMs;
    
    @JsonProperty("uptime_seconds")
    private long uptimeSeconds;
    
    private String status;
    
    @JsonProperty("reported_at")
    private OffsetDateTime reportedAt;

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getImageUuid() { return imageUuid; }
    public void setImageUuid(String imageUuid) { this.imageUuid = imageUuid; }
    public double getRamUsedMb() { return ramUsedMb; }
    public void setRamUsedMb(double ramUsedMb) { this.ramUsedMb = ramUsedMb; }
    public double getRamTotalMb() { return ramTotalMb; }
    public void setRamTotalMb(double ramTotalMb) { this.ramTotalMb = ramTotalMb; }
    public double getCpuPercent() { return cpuPercent; }
    public void setCpuPercent(double cpuPercent) { this.cpuPercent = cpuPercent; }
    public int getWorkersBusy() { return workersBusy; }
    public void setWorkersBusy(int workersBusy) { this.workersBusy = workersBusy; }
    public int getWorkersTotal() { return workersTotal; }
    public void setWorkersTotal(int workersTotal) { this.workersTotal = workersTotal; }
    public int getQueueSize() { return queueSize; }
    public void setQueueSize(int queueSize) { this.queueSize = queueSize; }
    public int getQueueCapacity() { return queueCapacity; }
    public void setQueueCapacity(int queueCapacity) { this.queueCapacity = queueCapacity; }
    public int getTasksDone() { return tasksDone; }
    public void setTasksDone(int tasksDone) { this.tasksDone = tasksDone; }
    public int getStealsPerformed() { return stealsPerformed; }
    public void setStealsPerformed(int stealsPerformed) { this.stealsPerformed = stealsPerformed; }
    public Double getAvgLatencyMs() { return avgLatencyMs; }
    public void setAvgLatencyMs(Double avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }
    public Double getP95LatencyMs() { return p95LatencyMs; }
    public void setP95LatencyMs(Double p95LatencyMs) { this.p95LatencyMs = p95LatencyMs; }
    public long getUptimeSeconds() { return uptimeSeconds; }
    public void setUptimeSeconds(long uptimeSeconds) { this.uptimeSeconds = uptimeSeconds; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public OffsetDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(OffsetDateTime reportedAt) { this.reportedAt = reportedAt; }
}
