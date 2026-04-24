package enfok.server.utility;

import enfok.server.model.entity.bd.Transformation;
import enfok.server.model.entity.bd.TransformationType;
import enfok.server.model.entity.dto.node.TransformationItem;
import jakarta.enterprise.context.ApplicationScoped;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clase de utilidad para procesar tareas de imágenes, extraer metadatos y analizar el peso
 * computacional de las transformaciones solicitadas.
 */
@ApplicationScoped
public class ImageTaskHelper {

    /**
     * Record para encapsular metadatos de dimensiones y formato.
     */
    public record ImageMetadata(int width, int height, String format) {}

    /**
     * Resultado del análisis de un pipeline de transformaciones.
     */
    public record TransformationAnalysis(List<TransformationItem> filters, double totalWeight) {}

    /**
     * Extrae el formato, ancho y alto de una imagen a partir de sus bytes sin decodificarla por completo.
     * 
     * @param imageData Los bytes crudos de la imagen.
     * @return Objeto ImageMetadata con dimensiones y formato detectado.
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
            }
        }
        return new ImageMetadata(width, height, format);
    }

    /**
     * Convierte una lista de transformaciones SOAP legacy a objetos dinámicos TransformationItem
     * y calcula su peso computacional total.
     * 
     * @param transformations Lista de transformaciones recibidas por SOAP.
     * @return TransformationAnalysis con el pipeline mapeado y su peso.
     */
    public TransformationAnalysis analyzeTransformations(List<Transformation> transformations) {
        double totalWeight = 0.0;
        List<TransformationItem> items = new ArrayList<>();

        if (transformations != null && !transformations.isEmpty()) {
            for (Transformation trans : transformations) {
                // Mapeamos al nuevo modelo dinámico preservando los parámetros JSON
                TransformationItem item = new TransformationItem(trans.getName(), trans.getParams());
                items.add(item);
                totalWeight += TransformationType.getWeightByName(trans.getName());
            }
        } else {
            totalWeight = 1.0; 
        }

        return new TransformationAnalysis(items, totalWeight);
    }

    /**
     * Calcula los pesos para el DTO TransformationItem (usado en REST y por el Orquestador).
     */
    public TransformationAnalysis analyzeTransformationItems(List<TransformationItem> transformations) {
        double totalWeight = 0.0;

        if (transformations != null && !transformations.isEmpty()) {
            for (TransformationItem item : transformations) {
                totalWeight += TransformationType.getWeightByName(item.getName());
            }
        } else {
            totalWeight = 1.0;
        }

        return new TransformationAnalysis(transformations, totalWeight);
    }
}
