package enfok.server.model.entity.dto.node;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representa una imagen individual dentro de un lote.
 */
public class ImageItemBatch {
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("base64")
    private String base64;
    
    public ImageItemBatch() {
    }

    public ImageItemBatch(String id, String name, String base64) {
        this.id = id;
        this.name = name;
        this.base64 = base64;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
