package enfok.server.model.entity.dto.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Respuesta inmediata tras la recepción de un lote.
 * Renombrado para evitar colisión JAXB.
 */
public class UploadBatchResult implements Serializable {
    
    @JsonProperty("batchId")
    private String batchId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("message")
    private String message;

    public UploadBatchResult() {
    }

    public UploadBatchResult(String batchId, String status, String message) {
        this.batchId = batchId;
        this.status = status;
        this.message = message;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
