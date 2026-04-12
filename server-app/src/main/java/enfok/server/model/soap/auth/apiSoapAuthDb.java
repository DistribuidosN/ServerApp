package enfok.server.model.soap.auth;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

@WebService
public interface apiSoapAuthDb {

    @WebMethod
    public String logIn(@WebParam(name="email") String email, @WebParam(name="password") String pwd);

    @WebMethod
    public boolean register(@WebParam(name = "email") String email, @WebParam(name = "password") String password, 
                           @WebParam(name = "name") String name, @WebParam(name = "lastName") String lastName);

    @WebMethod
    public boolean logOut(@WebParam(name="token") String token);

    @WebMethod
    public boolean forgetPwd(@WebParam(name="email") String email, @WebParam(name="newPassword") String newPassword);
}