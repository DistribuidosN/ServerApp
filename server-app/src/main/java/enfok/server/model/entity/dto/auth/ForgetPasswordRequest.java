package enfok.server.model.entity.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ForgetPasswordRequest {
    private String email;
    
    @JsonProperty("new_password")
    private String newPassword;

    public ForgetPasswordRequest() {}

    public ForgetPasswordRequest(String email, String newPassword) {
        this.email = email;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
