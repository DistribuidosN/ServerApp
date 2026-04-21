package enfok.server.model.entity.dto.node;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

/**
 * Resultado de la consulta de estado de un lote.
 * Renombrado para evitar colisión JAXB.
 */
public class BatchStatusResult implements Serializable {
    
    @JsonProperty("batchId")
    private String batchId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("progressPercentage")
    private double progressPercentage;
    
    @JsonProperty("totalImages")
    private int totalImages;
    
    @JsonProperty("processedImages")
    private int processedImages;

    public BatchStatusResult() {}

    public BatchStatusResult(String batchId, String status, double progressPercentage, int totalImages, int processedImages) {
        this.batchId = batchId;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.totalImages = totalImages;
        this.processedImages = processedImages;
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

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public int getTotalImages() {
        return totalImages;
    }

    public void setTotalImages(int totalImages) {
        this.totalImages = totalImages;
    }

    public int getProcessedImages() {
        return processedImages;
    }

    public void setProcessedImages(int processedImages) {
        this.processedImages = processedImages;
    }
}
