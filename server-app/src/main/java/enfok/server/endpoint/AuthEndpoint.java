package enfok.server.endpoint;

import jakarta.jws.WebService;

import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.auth.Activity;
import enfok.server.model.soap.auth.apiSoapAuthDb;
import enfok.server.model.soap.user.apiSoapUser;

/**
 * Endpoint de Autenticación.
 * 
 * Agrupa la lógica de login, registro (apiSoapAuthDb) 
 * y gestión de usuarios (apiSoapUser).
 */
@WebService(serviceName = "AuthService")
public class AuthEndpoint implements apiSoapAuthDb, apiSoapUser {

    // ==========================================
    // Implementación de apiSoapAuthDb
    // ==========================================

    @Override
    public String logIn(String email, String pwd) {
        System.out.println("Processing login for: " + email);
        return "mock-token-123";
    }

    @Override
    public boolean register(String email, String password, String name, String lastName) {
        System.out.println("Registering user: " + email);
        return true;
    }

    @Override
    public boolean logOut(String token) {
        return true;
    }

    @Override
    public boolean forgetPwd(String email, String newPassword) {
        return true;
    }

    // ==========================================
    // Implementación de apiSoapUser
    // ==========================================

    @Override
    public User profile(String token) {
        User u = new User();
        u.setUsername("mock_user");
        u.setStatus(1);
        return u;
    }

    @Override
    public boolean updateProfile(String token, User data) {
        return true;
    }

    @Override
    public boolean deleteAccount(String token) {
        return true;
    }

    @Override
    public Activity getUserActivity(String token) {
        Activity act = new Activity();
        act.setTotalBatches(5);
        act.setTotalImagesProcessed(10);
        return act;
    }

    @Override
    public String getUserStatistics(String token) {
        return "Estadísticas mockeadas";
    }

    @Override
    public User searchUser(String uid) {
        User u = new User();
        u.setUserUuid(uid);
        return u;
    }
}
