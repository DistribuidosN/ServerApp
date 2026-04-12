package enfok.server.model.soap.node;

import java.util.List;
import java.util.ArrayList;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import enfok.server.model.entity.bd.Node;
import enfok.server.model.entity.bd.Batch;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.Transformation;

@WebService
public interface apiSoapNode {

    @WebMethod
    public boolean createNode(@WebParam(name = "token") String token, @WebParam(name = "nodeData") Node data);

    @WebMethod
    public boolean updateNode(@WebParam(name = "token") String token, @WebParam(name = "nodeData") Node data);

    @WebMethod
    public boolean deleteNode(@WebParam(name = "token") String token, @WebParam(name = "nodeId") String nodeId);

    @WebMethod
    public Node getNode(@WebParam(name = "token") String token, @WebParam(name = "nodeId") String nodeId);

    @WebMethod
    public List<Node> getAllNodes(@WebParam(name = "token") String token);

    @WebMethod
    public Batch uploadImages(@WebParam(name = "token") String token, 
                              @WebParam(name = "imageData") byte[] imageData, 
                              @WebParam(name = "fileName") String fileName, 
                              @WebParam(name = "transformations") ArrayList<Transformation> transformations, 
                              @WebParam(name = "parameters") ArrayList<Transformation> parameters);

    @WebMethod
    public Batch uploadImagesBatch(@WebParam(name = "token") String token, 
                                   @WebParam(name = "images") ArrayList<Image> images, 
                                   @WebParam(name = "transformations") ArrayList<Transformation> transformations, 
                                   @WebParam(name = "parameters") ArrayList<Transformation> parameters);

    @WebMethod
    public String getBatchStatus(@WebParam(name = "batchId") String batchId, @WebParam(name = "token") String token);

    @WebMethod
    public String getUploadStatus(@WebParam(name = "jobId") String jobId, @WebParam(name = "token") String token);

    @WebMethod
    public byte[] downloadBatchResult(@WebParam(name = "jobId") String jobId, @WebParam(name = "token") String token);
}
