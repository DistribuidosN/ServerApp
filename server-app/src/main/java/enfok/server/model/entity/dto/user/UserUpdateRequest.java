package enfok.server.model.entity.dto.user;

public class UserUpdateRequest {
    private String username;

    public UserUpdateRequest() {}

    public UserUpdateRequest(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
