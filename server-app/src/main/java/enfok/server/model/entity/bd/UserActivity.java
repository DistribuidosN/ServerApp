package enfok.server.model.entity.bd;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class UserActivity {
    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("ref_uuid")
    private String refUuid;

    @JsonProperty("occurred_at")
    private LocalDateTime occurredAt;

    // Getters and Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getRefUuid() { return refUuid; }
    public void setRefUuid(String refUuid) { this.refUuid = refUuid; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
    public void setOccurredAt(LocalDateTime occurredAt) { this.occurredAt = occurredAt; }
}
