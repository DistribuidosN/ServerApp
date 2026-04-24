package enfok.server.model.entity.bd;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class BatchWithCover implements Serializable {
    
    @JsonProperty("batch")
    private Batches batch;

    @JsonProperty("cover_image_url")
    private String coverImageUrl;

    @JsonProperty("cover_image_uuid")
    private String coverImageUuid;

    public BatchWithCover() {}

    public Batches getBatch() {
        return batch;
    }

    public void setBatch(Batches batch) {
        this.batch = batch;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getCoverImageUuid() {
        return coverImageUuid;
    }

    public void setCoverImageUuid(String coverImageUuid) {
        this.coverImageUuid = coverImageUuid;
    }
}
