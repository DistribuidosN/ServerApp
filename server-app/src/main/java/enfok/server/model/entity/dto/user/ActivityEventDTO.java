package enfok.server.model.entity.dto.user;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActivityEventDTO")
public class ActivityEventDTO {

    @XmlElement(name = "eventType")
    private String eventType;

    @XmlElement(name = "refUuid")
    private String refUuid;

    @XmlElement(name = "parentUuid")
    private String parentUuid;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "occurredAt")
    private String occurredAt;

    // Getters and Setters
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getRefUuid() { return refUuid; }
    public void setRefUuid(String refUuid) { this.refUuid = refUuid; }

    public String getParentUuid() { return parentUuid; }
    public void setParentUuid(String parentUuid) { this.parentUuid = parentUuid; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getOccurredAt() { return occurredAt; }
    public void setOccurredAt(String occurredAt) { this.occurredAt = occurredAt; }
}
