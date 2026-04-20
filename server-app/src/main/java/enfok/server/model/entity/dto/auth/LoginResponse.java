package enfok.server.model.entity.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginResponse {
    private String token;
    
    @JsonProperty("role_id")
    private int roleId;

    public LoginResponse() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
