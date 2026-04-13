package enfok.server.model.soap;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * [CAPA MODEL / CONTRATOS DTO]:
 * Representa la respuesta de nuestro WebService (lo que Quarkus convertirá a XML).
 */
@XmlRootElement
public class BatchResponseDto {
    private String status;
    private String message;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
