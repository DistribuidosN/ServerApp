package enfok.server.service;

import enfok.server.ports.port.AuthOrchestator;
import enfok.server.ports.adapter.AuthRepositoryInterface;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthOrchestatorService implements AuthOrchestator {

    @Inject
    private AuthRepositoryInterface authRepository;

    @Override
    public String logIn(String email, String pwd) throws NotFoundException, InfrastructureOfflineException {
        if (email == null || email.trim().isEmpty() || pwd == null || pwd.trim().isEmpty()) {
            throw new NotFoundException("Credenciales en blanco o faltantes");
        }
        String emailE = email.toLowerCase();
        
        String result = authRepository.logIn(emailE, pwd);
        if (result == null || result.isEmpty()) {
            throw new NotFoundException("Credenciales inválidas o el usuario no existe");
        }
        return result;
    }

    @Override
    public boolean signUp(String email, String pwd, String name, String lastName) throws NotFoundException, InfrastructureOfflineException {
        if (email == null || email.trim().isEmpty() || pwd == null || pwd.trim().isEmpty()) {
            throw new NotFoundException("Credenciales o datos obligatorios est\u00E1n en blanco");
        }
        
        boolean result = authRepository.signUp(email.toLowerCase(), pwd, name.toLowerCase(), lastName.toLowerCase());
        if (!result) {
            throw new NotFoundException("El usuario ya existe o los datos son inv\u00E1lidos");
        }
        return result;
    }
    
    @Override
    public boolean logOut(String token) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.trim().isEmpty()) {
            throw new NotFoundException("Falta Token de autorizaci\u00F3n");
        }
        
        boolean result = authRepository.logOut(token);
        if (!result) {
            throw new NotFoundException("Fallo al cerrar sesi\u00F3n, token inv\u00E1lido");
        }
        return result;
    }

    @Override
    public boolean validateToken(String token) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.trim().isEmpty()) {
            throw new NotFoundException("Falta Token");
        }
        return authRepository.validateToken(token);
    }

    @Override
    public boolean forgotPassword(String email, String newPassword) throws NotFoundException, InfrastructureOfflineException {
        if (email == null || email.trim().isEmpty() || newPassword == null || newPassword.trim().isEmpty()) {
            throw new NotFoundException("Email o contrase\u00F1a no proporcionados");
        }
        
        boolean result = authRepository.forgotPassword(email.toLowerCase(), newPassword);
        if (!result) {
            throw new NotFoundException("Usuario no encontrado para recuperar contrase\u00F1a");
        }
        return result;
    }
}
