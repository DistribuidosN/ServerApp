package enfok.server.model.grpc;

import java.util.List;
import enfok.server.model.entity.dto.node.TransformationItem;

public class ImageQueue implements Comparable<ImageQueue> {
    public String id;
    public double predictedCost;
    public String status = "EN_COLA";
    public byte[] imageBytes;
    public List<TransformationItem> transformations;
    public String filename;
    public String imageFormat;
    public int width;
    public int height;

    public ImageQueue(String id, int width, int height, String imageFormat, double filterMultiplier, byte[] imageBytes, List<TransformationItem> transformations, String filename) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.imageBytes = imageBytes;
        this.transformations = transformations;

        this.filename = filename;
        this.imageFormat = imageFormat;
        
        // Calculamos la complejidad estimada
        double megapixels = (width * height) / 1000000.0;
        this.predictedCost = megapixels * filterMultiplier;
    }

    @Override
    public int compareTo(ImageQueue otra) {
        return Double.compare(this.predictedCost, otra.predictedCost);
    }
}