package enfok.server.model.entity.dto.node;

import java.util.List;

/**
 * DTO para la petición de carga de lotes de imágenes desde el microservicio en Go.
 */
public class UploadBatchRequest {
    private String id;
    private List<TransformationItem> filters;
    private List<ImageItemBatch> images;

    public UploadBatchRequest() {
    }

    public UploadBatchRequest(String id, List<TransformationItem> filters, List<ImageItemBatch> images) {
        this.id = id;
        this.filters = filters;
        this.images = images;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TransformationItem> getFilters() {
        return filters;
    }

    public void setFilters(List<TransformationItem> filters) {
        this.filters = filters;
    }


    public List<ImageItemBatch> getImages() {
        return images;
    }

    public void setImages(List<ImageItemBatch> images) {
        this.images = images;
    }
}
