package enfok.server.utility;

import enfok.server.model.entity.bd.Transformation;
import enfok.server.model.entity.bd.TransformationType;
import jakarta.enterprise.context.ApplicationScoped;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class to process image-related tasks and extract metadata.
 * Extracted from NodeRepository to keep the repository logic clean.
 */
@ApplicationScoped
public class ImageTaskHelper {

    /**
     * Record to hold image metadata.
     */
    public record ImageMetadata(int width, int height, String format) {}

    /**
     * Record to hold the result of transformation analysis.
     */
    public record TransformationAnalysis(List<String> filterNames, double totalWeight) {}

    /**
     * Extracts format, width, and height from image bytes without fully decoding the pixel data.
     * 
     * @param imageData The raw image bytes.
     * @return An ImageMetadata object with dimensions and format.
     */
    public ImageMetadata extractMetadata(byte[] imageData) {
        int width = 1024;
        int height = 768;
        String format = "unknown";

        if (imageData != null && imageData.length > 0) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
                 ImageInputStream iis = ImageIO.createImageInputStream(bais)) {

                Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
                if (readers.hasNext()) {
                    ImageReader reader = readers.next();
                    format = reader.getFormatName().toLowerCase();
                    reader.setInput(iis, true, true);
                    width = reader.getWidth(0);
                    height = reader.getHeight(0);
                    reader.dispose();
                }
            } catch (IOException ignored) {
                // In case of error, we return the default values
            }
        }
        return new ImageMetadata(width, height, format);
    }

    /**
     * Extracts filter names and calculates the total cost weight of a list of transformations.
     * 
     * @param transformations The list of transformations to analyze.
     * @return A TransformationAnalysis object with filter names and total weight.
     */
    public TransformationAnalysis analyzeTransformations(List<Transformation> transformations) {
        double totalWeight = 0.0;
        List<String> filterNames = new ArrayList<>();

        if (transformations != null && !transformations.isEmpty()) {
            for (Transformation trans : transformations) {
                String filterName = trans.getName();
                filterNames.add(filterName);
                totalWeight += TransformationType.getWeightByName(filterName);
            }
        } else {
            totalWeight = 1.0; // Default minimum weight if no transformations are provided
        }

        return new TransformationAnalysis(filterNames, totalWeight);
    }
}
