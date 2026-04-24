package enfok.server.model.entity.bd;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public class UserActivity {
    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("ref_uuid")
    private String refUuid;

    @JsonProperty("parent_uuid")
    private String parentUuid;

    @JsonProperty("description")
    private String description;

    @JsonProperty("occurred_at")
    private OffsetDateTime occurredAt;

    // Getters and Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getRefUuid() { return refUuid; }
    public void setRefUuid(String refUuid) { this.refUuid = refUuid; }
    public String getParentUuid() { return parentUuid; }
    public void setParentUuid(String parentUuid) { this.parentUuid = parentUuid; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(OffsetDateTime occurredAt) { this.occurredAt = occurredAt; }
}
