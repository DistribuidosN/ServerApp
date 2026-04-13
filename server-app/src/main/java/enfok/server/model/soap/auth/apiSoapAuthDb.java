package enfok.server.model.soap.auth;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;

/**
 * Interfaz fundamental del Servicio SOAP para operaciones de Autenticaci\u00F3n y Cuentas.
 * Define la estructura abstracta que interactuar\u00E1 con los clientes WSDL.
 */
@WebService
public interface apiSoapAuthDb {

    /**
     * Autentica a un usuario devolviendo un Token JWT si las credenciales son v\u00E1lidas.
     * @param email Correo registrado
     * @param pwd Contrase\u00F1a en texto plano (que se encripta post-proceso)
     * @return El token JWT de acceso.
     * @throws NotFoundException Si las credenciales fallan o el usuario no existe.
     */
    @WebMethod
    public String logIn(@WebParam(name="email") String email, @WebParam(name="password") String pwd) throws NotFoundException;

    @WebMethod
    public boolean register(@WebParam(name = "email") String email, @WebParam(name = "password") String password, 
                           @WebParam(name = "name") String name, @WebParam(name = "lastName") String lastName) throws NotFoundException;

    @WebMethod
    public boolean logOut()throws InfrastructureOfflineException;

    @WebMethod
    public boolean forgetPwd(@WebParam(name="email") String email, @WebParam(name="newPassword") String newPassword)throws NotFoundException;
}