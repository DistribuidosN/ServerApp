package enfok.server.model.soap;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * [CAPA MODEL / CONTRATOS DTO]:
 * Este DTO (Data Transfer Object) es exclusivo de CXF.
 * @XmlRootElement permite que Quarkus mapee el XML entrante de la petición a este objeto Java.
 */
@XmlRootElement
public class BatchRequestDto {
    private String batchId;
    private String data;

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
