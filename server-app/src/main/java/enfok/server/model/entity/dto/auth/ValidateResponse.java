package enfok.server.model.entity.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValidateResponse {
    private boolean valid;
    private String role;
    private String username;
    
    @JsonProperty("user_uuid")
    private String userUuid;

    @JsonProperty("role_id")
    private int roleId;

    public ValidateResponse() {}

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
