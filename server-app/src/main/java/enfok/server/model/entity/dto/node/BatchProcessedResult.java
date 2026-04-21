package enfok.server.model.entity.dto.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;

/**
 * Respuesta con la lista de imágenes procesadas de un lote completado.
 * Renombrado para evitar colisión con wrappers de JAX-WS.
 */
public class BatchProcessedResult implements Serializable {
    
    @JsonProperty("batchId")
    private String batchId;
    
    @JsonProperty("images")
    private List<ImageItemBatch> images;

    public BatchProcessedResult() {
    }

    public BatchProcessedResult(String batchId, List<ImageItemBatch> images) {
        this.batchId = batchId;
        this.images = images;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public List<ImageItemBatch> getImages() {
        return images;
    }

    public void setImages(List<ImageItemBatch> images) {
        this.images = images;
    }
}
