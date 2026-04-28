package enfok.server.model.soap;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class DownloadBatchResponseDto {
    private String downloadUrl;
    private String status;
    private String message;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
