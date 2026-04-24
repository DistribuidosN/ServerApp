package enfok.server.model.entity.dto.node;

/**
 * Representa una transformación individual dentro de un pipeline de procesamiento.
 */
public class TransformationItem {
    private String name;
    private String params; // JSON string con los parámetros de la transformación

    public TransformationItem() {
    }

    public TransformationItem(String name, String params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParams() {
        return params == null ? "{}" : params;
    }

    public void setParams(String params) {
        this.params = params;
    }
}
