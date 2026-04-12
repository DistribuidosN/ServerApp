package enfok.server.model.soap.user;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.auth.Activity;

@WebService
public interface apiSoapUser {

    @WebMethod
    public User profile(@WebParam(name = "token") String token);

    @WebMethod
    public boolean updateProfile(@WebParam(name = "token") String token, @WebParam(name = "userData") User data);

    @WebMethod
    public boolean deleteAccount(@WebParam(name = "token") String token);

    @WebMethod
    public Activity getUserActivity(@WebParam(name = "token") String token);

    @WebMethod
    public String getUserStatistics(@WebParam(name = "token") String token);

    @WebMethod
    public User searchUser(@WebParam(name = "uid") String uid);
}
