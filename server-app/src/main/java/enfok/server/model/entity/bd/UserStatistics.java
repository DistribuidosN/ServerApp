package enfok.server.model.entity.bd;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserStatistics {
    @JsonProperty("user_uuid")
    private String userUuid;

    @JsonProperty("total_batches")
    private int totalBatches;

    @JsonProperty("total_images")
    private int totalImages;

    @JsonProperty("images_success")
    private int imagesSuccess;

    @JsonProperty("images_failed")
    private int imagesFailed;

    // Getters and Setters
    public String getUserUuid() { return userUuid; }
    public void setUserUuid(String userUuid) { this.userUuid = userUuid; }
    public int getTotalBatches() { return totalBatches; }
    public void setTotalBatches(int totalBatches) { this.totalBatches = totalBatches; }
    public int getTotalImages() { return totalImages; }
    public void setTotalImages(int totalImages) { this.totalImages = totalImages; }
    public int getImagesSuccess() { return imagesSuccess; }
    public void setImagesSuccess(int imagesSuccess) { this.imagesSuccess = imagesSuccess; }
    public int getImagesFailed() { return imagesFailed; }
    public void setImagesFailed(int imagesFailed) { this.imagesFailed = imagesFailed; }
}
