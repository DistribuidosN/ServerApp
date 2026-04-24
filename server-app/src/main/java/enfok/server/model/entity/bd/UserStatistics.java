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

    @JsonProperty("top_transformations")
    private java.util.List<TransformationStat> topTransformations;

    public static class TransformationStat {
        private String name;
        private int count;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
    }

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
    public java.util.List<TransformationStat> getTopTransformations() { return topTransformations; }
    public void setTopTransformations(java.util.List<TransformationStat> topTransformations) { this.topTransformations = topTransformations; }
}
