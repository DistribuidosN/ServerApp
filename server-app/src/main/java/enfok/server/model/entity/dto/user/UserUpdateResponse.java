package enfok.server.model.entity.dto.user;

public class UserUpdateResponse {
    private String message;
    private String username;
    private boolean valid;

    public UserUpdateResponse() {}

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
