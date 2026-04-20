package enfok.server.service;

import enfok.server.ports.port.AuthOrchestator;
import enfok.server.ports.adapter.AuthRepositoryInterface;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.model.entity.dto.auth.LoginResponse;
import enfok.server.model.entity.dto.auth.ValidateResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthOrchestatorService implements AuthOrchestator {

    @Inject
    private AuthRepositoryInterface authRepository;

    @Override
    public LoginResponse logIn(String email, String pwd) throws NotFoundException, InfrastructureOfflineException {
        if (email == null || email.trim().isEmpty() || pwd == null || pwd.trim().isEmpty()) {
            throw new NotFoundException("Credenciales en blanco o faltantes");
        }
        String emailE = email.toLowerCase();
        
        LoginResponse response = authRepository.logIn(emailE, pwd);
        if (response == null || response.getToken() == null || response.getToken().isEmpty()) {
            throw new NotFoundException("Credenciales inválidas o el usuario no existe");
        }
        return response;
    }

    @Override
    public boolean signUp(String email, String pwd, String username, int role_id) throws NotFoundException, InfrastructureOfflineException {
        if (email == null || email.trim().isEmpty() || pwd == null || pwd.trim().isEmpty()) {
            throw new NotFoundException("Credenciales o datos obligatorios están en blanco");
        }
        
        boolean result = authRepository.signUp(email.toLowerCase(), pwd, username.toLowerCase(), role_id);
        if (!result) {
            throw new NotFoundException("El usuario ya existe o los datos son inválidos");
        }
        return result;
    }
    
    @Override
    public boolean logOut(String token) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.trim().isEmpty()) {
            throw new NotFoundException("Falta Token de autorización");
        }
        
        boolean result = authRepository.logOut(token);
        if (!result) {
            throw new NotFoundException("Fallo al cerrar sesión, token inválido");
        }
        return result;
    }

    @Override
    public ValidateResponse validateToken(String token) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.trim().isEmpty()) {
            throw new NotFoundException("Falta Token");
        }
        return authRepository.validateToken(token);
    }

    @Override
    public boolean forgotPassword(String email, String newPassword) throws NotFoundException, InfrastructureOfflineException {
        if (email == null || email.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            throw new NotFoundException("Email o contraseña no proporcionados");
        }
        
        boolean result = authRepository.forgotPassword(email.toLowerCase(), newPassword);
        if (!result) {
            throw new NotFoundException("Usuario no encontrado para recuperar contraseña");
        }
        return result;
    }

    @Override
    public boolean resetPassword(String token, String newPassword) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            throw new NotFoundException("Token o nueva contraseña no proporcionados");
        }
        
        boolean result = authRepository.resetPassword(token, newPassword);
        if (!result) {
            throw new NotFoundException("Error al resetear contraseña (token inválido o expirado)");
        }
        return result;
    }
}
