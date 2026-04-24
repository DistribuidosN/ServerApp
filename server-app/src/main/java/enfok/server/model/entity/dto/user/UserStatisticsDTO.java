package enfok.server.model.entity.dto.user;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserStatisticsDTO")
public class UserStatisticsDTO {

    @XmlElement(name = "totalBatches")
    private int totalBatches;

    @XmlElement(name = "totalImages")
    private int totalImages;

    @XmlElement(name = "imagesSuccess")
    private int imagesSuccess;

    @XmlElement(name = "imagesFailed")
    private int imagesFailed;

    @XmlElement(name = "topTransformations")
    private List<TransformationStatDTO> topTransformations;

    // Getters and Setters
    public int getTotalBatches() { return totalBatches; }
    public void setTotalBatches(int totalBatches) { this.totalBatches = totalBatches; }

    public int getTotalImages() { return totalImages; }
    public void setTotalImages(int totalImages) { this.totalImages = totalImages; }

    public int getImagesSuccess() { return imagesSuccess; }
    public void setImagesSuccess(int imagesSuccess) { this.imagesSuccess = imagesSuccess; }

    public int getImagesFailed() { return imagesFailed; }
    public void setImagesFailed(int imagesFailed) { this.imagesFailed = imagesFailed; }

    public List<TransformationStatDTO> getTopTransformations() { return topTransformations; }
    public void setTopTransformations(List<TransformationStatDTO> topTransformations) { this.topTransformations = topTransformations; }
}
