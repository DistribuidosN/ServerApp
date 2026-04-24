package enfok.server.model.entity.dto.node;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchProgressDTO {
    
    @JsonProperty("batch_uuid")
    private String batchUuid;
    
    @JsonProperty("total_images")
    private int totalImages;
    
    @JsonProperty("processed_images")
    private int processedImages;
    
    @JsonProperty("progress_percentage")
    private double progressPercentage;

    // Getters and Setters
    public String getBatchUuid() { return batchUuid; }
    public void setBatchUuid(String batchUuid) { this.batchUuid = batchUuid; }
    public int getTotalImages() { return totalImages; }
    public void setTotalImages(int totalImages) { this.totalImages = totalImages; }
    public int getProcessedImages() { return processedImages; }
    public void setProcessedImages(int processedImages) { this.processedImages = processedImages; }
    public double getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(double progressPercentage) { this.progressPercentage = progressPercentage; }
}
