package enfok.server.model.soap.bd;

import java.util.ArrayList;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import enfok.server.model.entity.bd.Batch;
import enfok.server.model.entity.bd.Image;

@WebService
public interface apiSoapBD {

    @WebMethod
    public String getNodeStatus(@WebParam(name = "token") String token);

    @WebMethod
    public String getTransformations(@WebParam(name = "token") String token);

    @WebMethod
    public ArrayList<Image> getUserImages(@WebParam(name = "token") String token, 
                                          @WebParam(name = "limit") int limit, 
                                          @WebParam(name = "offset") int offset);

    @WebMethod
    public ArrayList<Batch> getUserBatches(@WebParam(name = "token") String token, 
                                           @WebParam(name = "limit") int limit, 
                                           @WebParam(name = "offset") int offset);
}
