package enfok.server.ports.port;

import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;

public interface AuthOrchestator {
    public String logIn(String email, String pwd) throws NotFoundException, InfrastructureOfflineException;
    public boolean signUp(String email, String pwd, String name, String lastName) throws NotFoundException, InfrastructureOfflineException;
    public boolean logOut(String token) throws NotFoundException, InfrastructureOfflineException;
    public boolean validateToken(String token) throws NotFoundException, InfrastructureOfflineException;
    public boolean forgotPassword(String email, String newPassword) throws NotFoundException, InfrastructureOfflineException;
}
