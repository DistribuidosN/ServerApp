package enfok.server.endpoint;

import jakarta.jws.WebService;

import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.auth.Activity;
import enfok.server.model.soap.user.apiSoapUser;
import enfok.server.error.NotFoundException;

import enfok.server.error.InfrastructureOfflineException;
import enfok.server.ports.port.UserOrchestrator;
import enfok.server.utility.TokenMapper;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.xml.ws.WebServiceContext;

/**
 * Endpoint de Usuarios (SOAP Service).
 * API Protegida (Requiere inyección de Token interceptado). Permite recuperar perfiles,
 * editar estatus de cuenta e interrogar las estadísticas internas del usuario firmado.
 */
@WebService(endpointInterface = "enfok.server.model.soap.user.apiSoapUser", serviceName = "UserService")
public class UserEndpoint implements apiSoapUser {

    @Inject
    UserOrchestrator userOrchestrator;

    @Inject
    TokenMapper tokenMapper;

    @Resource
    WebServiceContext context;

    /**
     * Extrae el perfil base (ID, nombre, permisos) del usuario que remite esta llamada.
     * @return Clase User con datos anonimizados.
     * @throws NotFoundException Si el token es huérfano.
     */
    @Override
    public User profile() throws NotFoundException {
        try {
            return userOrchestrator.profile(tokenMapper.extractToken(context));
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor est\u00E1 fuera de l\u00EDnea", e);
        }
    }

    /**
     * Aplica actualizaciones Parciales o Totales al objeto del usuario correspondiente en Base de Datos.
     * @param data Payload conteniendo los campos alterados.
     * @return Booleano sobre validación exitosa.
     * @throws NotFoundException Fallo de edición o token falsificado.
     */
    @Override
    public boolean updateProfile(User data) throws NotFoundException {
        try {
            return userOrchestrator.updateProfile(tokenMapper.extractToken(context), data);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor est\u00E1 fuera de l\u00EDnea", e);
        }
    }

    /**
     * Elimina lógicamente o físicamente la cuenta asosiada a las credenciales encriptadas en la Petición SOAP.
     */
    @Override
    public boolean deleteAccount() throws NotFoundException {
        try {
            return userOrchestrator.deleteAccount(tokenMapper.extractToken(context));
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor est\u00E1 fuera de l\u00EDnea", e);
        }
    }

    @Override
    public Activity getUserActivity() throws NotFoundException {
        try {
            return userOrchestrator.getUserActivity(tokenMapper.extractToken(context));
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor est\u00E1 fuera de l\u00EDnea", e);
        }
    }

    @Override
    public String getUserStatistics() throws NotFoundException {
        try {
            return userOrchestrator.getUserStatistics(tokenMapper.extractToken(context));
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor est\u00E1 fuera de l\u00EDnea", e);
        }
    }

    @Override
    public User searchUser(String uid) throws NotFoundException {
        try {
            return userOrchestrator.searchUser(tokenMapper.extractToken(context), uid);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor est\u00E1 fuera de l\u00EDnea", e);
        }
    }
}
