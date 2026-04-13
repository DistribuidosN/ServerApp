package enfok.server.model.soap.bd;

import java.util.ArrayList;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;

@WebService
public interface apiSoapBD {

    @WebMethod
    public String getNodeStatus();

    @WebMethod
    public String getTransformations();

    @WebMethod
    public ArrayList<Image> getUserImages(@WebParam(name = "limit") int limit, 
                                          @WebParam(name = "offset") int offset);

    @WebMethod
    public ArrayList<Batches> getUserBatches(@WebParam(name = "limit") int limit, 
                                           @WebParam(name = "offset") int offset);
}
