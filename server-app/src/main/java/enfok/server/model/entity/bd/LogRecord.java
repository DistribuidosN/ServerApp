package enfok.server.model.entity.bd;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public class LogRecord {
    private int id;
    
    @JsonProperty("node_id")
    private String nodeId;
    
    @JsonProperty("image_uuid")
    private String imageUuid;
    
    @JsonProperty("level_id")
    private int levelId;
    
    @JsonProperty("level_name")
    private String levelName;
    
    private String message;
    
    @JsonProperty("transformation_id")
    private int transformationId;
    
    @JsonProperty("log_time")
    private OffsetDateTime createdAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }

    public String getImageUuid() { return imageUuid; }
    public void setImageUuid(String imageUuid) { this.imageUuid = imageUuid; }

    public int getLevelId() { return levelId; }
    public void setLevelId(int levelId) { this.levelId = levelId; }

    public String getLevelName() { return levelName; }
    public void setLevelName(String levelName) { this.levelName = levelName; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getTransformationId() { return transformationId; }
    public void setTransformationId(int transformationId) { this.transformationId = transformationId; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
