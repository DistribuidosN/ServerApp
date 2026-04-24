package enfok.server.model.entity.bd;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public class Batches {
    @JsonProperty("batch_uuid")
    private String batchUuid;

    @JsonProperty("user_uuid")
    private String userUuid;

    @JsonProperty("request_time")
    private LocalDateTime requestTime;

    @JsonProperty("status")
    private String status;

    public String getBatchUuid() { return batchUuid; }
    public void setBatchUuid(String batchUuid) { this.batchUuid = batchUuid; }
    public String getUserUuid() { return userUuid; }
    public void setUserUuid(String userUuid) { this.userUuid = userUuid; }
    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
