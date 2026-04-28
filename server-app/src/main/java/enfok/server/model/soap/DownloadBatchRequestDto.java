package enfok.server.model.soap;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DownloadBatchRequestDto {
    private String batchId;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }
}
