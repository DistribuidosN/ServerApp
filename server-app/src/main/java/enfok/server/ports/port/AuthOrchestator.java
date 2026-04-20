package enfok.server.ports.port;

import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.model.entity.dto.auth.LoginResponse;
import enfok.server.model.entity.dto.auth.ValidateResponse;

public interface AuthOrchestator {
    public LoginResponse logIn(String email, String pwd) throws NotFoundException, InfrastructureOfflineException;
    public boolean signUp(String email, String pwd, String username, int role_id) throws NotFoundException, InfrastructureOfflineException;
    public boolean logOut(String token) throws NotFoundException, InfrastructureOfflineException;
    public ValidateResponse validateToken(String token) throws NotFoundException, InfrastructureOfflineException;
    public boolean forgotPassword(String email, String newPassword) throws NotFoundException, InfrastructureOfflineException;
    public boolean resetPassword(String token, String newPassword) throws NotFoundException, InfrastructureOfflineException;
}
