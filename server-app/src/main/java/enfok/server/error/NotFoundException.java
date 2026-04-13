package enfok.server.error;

import jakarta.xml.ws.WebFault;

@WebFault(name = "NotFoundFault", targetNamespace = "http://error.server.enfok/")
public class NotFoundException extends Exception {
    private final String codigoError;

    // Constructor genérico
    public NotFoundException(String mensaje) {
        super(mensaje);
        this.codigoError = "ERR_DATA_404";
    }
    
    // Constructor personalizado para distintos recursos
    public NotFoundException(String tipoRecurso, String identificador) {
        super(String.format("No se encontró el recurso solicitado de tipo [%s] con identificador: %s", tipoRecurso, identificador));
        // Genera un código dinámico como ERR_DATA_404_USER o ERR_DATA_404_URL
        this.codigoError = "ERR_DATA_402" + tipoRecurso.toUpperCase();
    }

    public String getCodigoError() { return codigoError; }

    // --- MÉTODOS DE FÁBRICA (Atajos útiles para usar en tu código) ---
    
    public static NotFoundException forUser(String id) {
        return new NotFoundException("USER", id);
    }

    public static NotFoundException forUrl(String url) {
        return new NotFoundException("URL", url);
    }
    
    public static NotFoundException forBatch(String batchId) {
        return new NotFoundException("BATCH", batchId);
    }

    public static NotFoundException forImage(String imageId) {
        return new NotFoundException("IMAGE", imageId);
    }
}