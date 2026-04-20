package enfok.server.repository;

import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.ports.adapter.AuthRepositoryInterface;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.model.entity.dto.auth.*;
import enfok.server.repository.client.AuthServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AuthRepository implements AuthRepositoryInterface {

    @Inject
    Config config;

    @Inject
    NetworkValidator networkValidator;

    @Inject
    @RestClient
    AuthServiceClient authClient;

    @Override
    public boolean validateServer() throws InfrastructureOfflineException {
        return networkValidator.validate(config.getAuthBd());
    }

    @Override
    public LoginResponse logIn(String email, String pwd) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        System.out.println(">>> [AuthRepository] Ejecutando POST a /auth/login");

        try {
            LoginRequest request = new LoginRequest(email, pwd);
            LoginResponse response = authClient.login(request);

            if (response != null) {
                return response;
            }
            throw new NotFoundException("Respuesta vacía del servidor de autenticación");
        } catch (Exception e) {
            handleException(e, "logIn");
            return null;
        }
    }

    @Override
    public boolean signUp(String email, String pwd, String username, int role_id)
            throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        System.out.println(">>> [AuthRepository] Ejecutando POST a /auth/register");

        try {
            RegisterRequest request = new RegisterRequest(username, pwd, email, role_id);
            try (Response response = authClient.register(request)) {
                if (response.getStatus() == 200 || response.getStatus() == 201) {
                    return true;
                }
                handleErrorResponse(response);
                return false;
            }
        } catch (Exception e) {
            handleException(e, "signUp");
            return false;
        }
    }

    @Override
    public boolean logOut(String token) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        System.out.println(">>> [AuthRepository] Ejecutando POST a /auth/logout");

        try {
            try (Response response = authClient.logout("Bearer " + token)) {
                if (response.getStatus() == 200) {
                    return true;
                }
                handleErrorResponse(response);
                return false;
            }
        } catch (Exception e) {
            handleException(e, "logOut");
            return false;
        }
    }

    @Override
    public boolean forgotPassword(String email, String newPassword)
            throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        System.out.println(">>> [AuthRepository] Ejecutando POST a /auth/forget-password");

        try {
            ForgetPasswordRequest request = new ForgetPasswordRequest(email, newPassword);
            try (Response response = authClient.forgetPassword(request)) {
                if (response.getStatus() == 200) {
                    return true;
                }
                handleErrorResponse(response);
                return false;
            }
        } catch (Exception e) {
            handleException(e, "forgotPassword");
            return false;
        }
    }

    @Override
    public boolean resetPassword(String token, String newPassword)
            throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        System.out.println(">>> [AuthRepository] Ejecutando POST a /auth/reset-password");

        try {
            ResetPasswordRequest request = new ResetPasswordRequest(newPassword);
            try (Response response = authClient.resetPassword("Bearer " + token, request)) {
                if (response.getStatus() == 200) {
                    return true;
                }
                handleErrorResponse(response);
                return false;
            }
        } catch (Exception e) {
            handleException(e, "resetPassword");
            return false;
        }
    }

    @Override
    public ValidateResponse validateToken(String token) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        try {
            return authClient.validate(token);
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error validando token en la red: " + e.getMessage());
        }
    }

    private void handleErrorResponse(Response response) throws NotFoundException, InfrastructureOfflineException {
        int status = response.getStatus();
        if (status == 404 || status == 401 || status == 409) {
            throw new NotFoundException("Acción fallida: código " + status);
        } else {
            throw new InfrastructureOfflineException("Auth Service devolvió error HTTP " + status);
        }
    }

    private void handleException(Exception e, String method) throws NotFoundException, InfrastructureOfflineException {
        if (e instanceof NotFoundException)
            throw (NotFoundException) e;
        if (e.getCause() instanceof NotFoundException)
            throw (NotFoundException) e.getCause();
        throw new InfrastructureOfflineException(
                "Error en " + method + " contactando al Auth Service: " + e.getMessage());
    }
}
