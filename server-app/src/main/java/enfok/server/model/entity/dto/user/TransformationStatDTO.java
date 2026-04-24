package enfok.server.model.entity.dto.user;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformationStatDTO")
public class TransformationStatDTO {

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "count")
    private int count;

    public TransformationStatDTO() {}

    public TransformationStatDTO(String name, int count) {
        this.name = name;
        this.count = count;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
