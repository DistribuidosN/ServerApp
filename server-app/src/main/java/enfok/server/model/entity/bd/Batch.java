package enfok.server.model.entity.bd;

import java.sql.Timestamp;

public class Batch {
    private int id;
    private String userUuid;
    private Timestamp requestTime;
    private int statusId;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUserUuid() { return userUuid; }
    public void setUserUuid(String userUuid) { this.userUuid = userUuid; }
    public Timestamp getRequestTime() { return requestTime; }
    public void setRequestTime(Timestamp requestTime) { this.requestTime = requestTime; }
    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }
}
