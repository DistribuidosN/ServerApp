package enfok.server.endpoint;

import jakarta.jws.WebService;
import jakarta.inject.Inject;
import enfok.server.model.soap.auth.apiSoapAuthDb;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.ports.port.AuthOrchestator;

import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import java.util.List;
import java.util.Map;
import enfok.server.model.entity.dto.auth.LoginResponse;
import enfok.server.model.entity.dto.auth.ValidateResponse;
import enfok.server.utility.TokenMapper;

/**
 * Endpoint de Autenticación (SOAP Service).
 * Expone las operaciones para loguear, registrar, y gestionar contraseñas
 * de forma abierta al público, delegando validaciones a AuthOrchestator.
 */
@WebService(endpointInterface = "enfok.server.model.soap.auth.apiSoapAuthDb", serviceName = "AuthService")
public class AuthEndpoint implements apiSoapAuthDb {

    @Inject
    AuthOrchestator authOrchestator;

    @Inject
    TokenMapper tokenMapper;

    @Resource
    WebServiceContext context;

    /**
     * Inicia sesión verificando credenciales en el servidor subyacente.
     * @param email Correo electrónico en texto plano.
     * @param pwd Contraseña en texto plano.
     * @return Objeto LoginResponse con Token y RoleID.
     * @throws NotFoundException Si las credenciales no válidas.
     */
    @Override
    public LoginResponse logIn(String email, String pwd) throws NotFoundException {
        try {
            return authOrchestator.logIn(email, pwd);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("Servicio de autenticación no disponible", e);
        }
    }

    /**
     * Registra un nuevo usuario en la plataforma.
     * @param email Nuevo correo.
     * @param password Contraseña plana (a ser hasheada post).
     * @param name Nombre de usuario.
     * @param lastName Apellido.
     * @return true si la creación triunfó.
     * @throws NotFoundException Si el usuario ya existe o campos denegados.
     */
    @Override
    public boolean register(String email, String password, String username, int role_id) throws NotFoundException {
        try {
            return authOrchestator.signUp(email, password, username, role_id);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("Servicio de autenticación no disponible", e);
        }
    }

    /**
     * Cierra la sesión activa invalidando el Token enviado en las cabeceras.
     * @return true si invalida correctamente.
     * @throws InfrastructureOfflineException Si detecta caída del servidor remoto al finalizar sesión.
     */
    @Override
    public boolean logOut() throws InfrastructureOfflineException {
        try {
            return authOrchestator.logOut(tokenMapper.extractToken(context));
        } catch (NotFoundException e) {
            throw new InfrastructureOfflineException("Fallo al cerrar sesión: " + e.getMessage());
        }
    }

    /**
     * Recuperación de contraseña (olvido).
     */
    @Override
    public boolean forgetPwd(String email, String newPassword) throws NotFoundException {
        try {
            return authOrchestator.forgotPassword(email, newPassword);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("Servicio de autenticación no disponible", e);
        }
    }

    @Override
    public boolean resetPassword(String newPassword) throws NotFoundException {
        try {
            String token = tokenMapper.extractToken(context);
            return authOrchestator.resetPassword(token, newPassword);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("Servicio de autenticación no disponible", e);
        }
    }

    @Override
    public ValidateResponse validateToken() throws NotFoundException {
        try {
            String token = tokenMapper.extractToken(context);
            return authOrchestator.validateToken(token);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("Servicio de autenticación no disponible", e);
        }
    }
}
