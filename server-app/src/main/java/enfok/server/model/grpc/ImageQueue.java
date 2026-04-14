package enfok.server.model.grpc;

public class ImageQueue implements Comparable<ImageQueue> {
    public String id;
    public double predictedCost; // El "peso" calculado
    public String status = "EN_COLA";
    public String url; // Almacenamiento

    public ImageQueue(String id, int width, int height, double filterMultiplier) {
        this.id = id;
        // Calculamos la complejidad estimada
        double megapixels = (width * height) / 1000000.0;
        this.predictedCost = megapixels * filterMultiplier;
    }

    // Aquí ocurre la magia de la Cola de Prioridad.
    // Ordenamos de Menor costo a Mayor costo (Shortest Job First)
    // para despachar rápido las tareas fáciles y no bloquear el sistema.
    @Override
    public int compareTo(ImageQueue otra) {
        return Double.compare(this.predictedCost, otra.predictedCost);
    }
}