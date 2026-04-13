package enfok.server.model.soap.user;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.auth.Activity;
import enfok.server.error.NotFoundException;

@WebService
public interface apiSoapUser {

    @WebMethod
    public User profile() throws NotFoundException;

    @WebMethod
    public boolean updateProfile(@WebParam(name = "userData") User data) throws NotFoundException;

    @WebMethod
    public boolean deleteAccount() throws NotFoundException;

    @WebMethod
    public Activity getUserActivity() throws NotFoundException;

    @WebMethod
    public String getUserStatistics() throws NotFoundException;

    @WebMethod
    public User searchUser(@WebParam(name = "uid") String uid) throws NotFoundException;
}
