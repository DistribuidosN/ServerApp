package enfok.server.model.entity.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserProfileResponse {
    private int id;
    
    @JsonProperty("user_uuid")
    private String userUuid;
    
    private String username;
    private String email;
    
    @JsonProperty("role_id")
    private int roleId;
    
    private int status;
    
    @JsonProperty("created_at")
    private String createdAt;

    public UserProfileResponse() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserUuid() { return userUuid; }
    public void setUserUuid(String userUuid) { this.userUuid = userUuid; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
