package enfok.server.model.entity.bd;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public class Image {
    @JsonProperty("image_uuid")
    private String imageUuid;

    @JsonProperty("batch_uuid")
    private String batchUuid;

    @JsonProperty("original_name")
    private String originalName;

    @JsonProperty("result_path")
    private String resultPath;

    @JsonProperty("status")
    private String status;

    @JsonProperty("node_id")
    private String nodeId;

    @JsonProperty("reception_time")
    private OffsetDateTime receptionTime;

    @JsonProperty("conversion_time")
    private OffsetDateTime conversionTime;

    private byte[] data;

    public String getImageUuid() { return imageUuid; }
    public void setImageUuid(String imageUuid) { this.imageUuid = imageUuid; }
    public String getBatchUuid() { return batchUuid; }
    public void setBatchUuid(String batchUuid) { this.batchUuid = batchUuid; }
    public String getOriginalName() { return originalName; }
    public void setOriginalName(String originalName) { this.originalName = originalName; }
    public String getResultPath() { return resultPath; }
    public void setResultPath(String resultPath) { this.resultPath = resultPath; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public OffsetDateTime getReceptionTime() { return receptionTime; }
    public void setReceptionTime(OffsetDateTime receptionTime) { this.receptionTime = receptionTime; }
    public OffsetDateTime getConversionTime() { return conversionTime; }
    public void setConversionTime(OffsetDateTime conversionTime) { this.conversionTime = conversionTime; }
    public byte[] getData() { return data; }
    public void setData(byte[] data) { this.data = data; }
}
