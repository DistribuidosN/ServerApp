package enfok.server.model.soap.auth;

import enfok.server.model.entity.dto.auth.LoginResponse;
import enfok.server.model.entity.dto.auth.ValidateResponse;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;

/**
 * Interfaz fundamental del Servicio SOAP para operaciones de Autenticación y Cuentas.
 */
@WebService
public interface apiSoapAuthDb {

    @WebMethod
    public LoginResponse logIn(@WebParam(name="email") String email, @WebParam(name="password") String pwd) throws NotFoundException;

    @WebMethod
    public boolean register(@WebParam(name = "email") String email, @WebParam(name = "password") String password, 
                           @WebParam(name = "username") String username, @WebParam(name = "role_id") int role_id) throws NotFoundException;

    @WebMethod
    public boolean logOut()throws InfrastructureOfflineException;

    @WebMethod
    public boolean forgetPwd(@WebParam(name="email") String email, @WebParam(name="newPassword") String newPassword)throws NotFoundException;

    @WebMethod
    public boolean resetPassword(@WebParam(name="newPassword") String newPassword) throws NotFoundException;

    @WebMethod
    public ValidateResponse validateToken() throws NotFoundException;
}