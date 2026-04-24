package enfok.server.model.entity.bd;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Representa una instrucción de procesamiento de imagen en el pipeline.
 * Se utiliza en las peticiones SOAP para definir qué filtros aplicar y con qué parámetros.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Transformation", propOrder = {
    "name",
    "params"
})
public class Transformation {

    /**
     * El nombre técnico de la transformación.
     * Ejemplos: "rotate", "resize", "grayscale", "watermark_text".
     */
    @XmlElement(required = true)
    private String name;

    /**
     * Parámetros de la transformación en formato JSON.
     * Ejemplo para rotate: "{\"angle\": 45}"
     * Ejemplo para watermark: "{\"text\": \"Enfok App\", \"opacity\": 0.5}"
     * Si se deja vacío, se usarán los valores por defecto del nodo.
     */
    @XmlElement(required = false)
    private String params = "{}";

    public Transformation() {}

    public Transformation(String name, String params) {
        this.name = name;
        this.params = params;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getParams() { return params; }
    public void setParams(String params) { this.params = params; }
}
