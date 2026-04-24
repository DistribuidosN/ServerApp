package enfok.server.model.soap.node;

import java.util.List;
import java.util.ArrayList;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import enfok.server.model.entity.bd.Node;
import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.Transformation;
import enfok.server.model.entity.bd.LogRecord;
import enfok.server.model.entity.bd.NodeMetricsBd;
import enfok.server.model.entity.dto.node.UploadBatchRequest;
import enfok.server.model.entity.dto.node.UploadBatchResult;
import enfok.server.model.entity.dto.node.BatchStatusResult;
import enfok.server.model.entity.dto.node.BatchProcessedResult;
import enfok.server.error.NotFoundException;

/**
 * Interfaz del Servicio SOAP con el contrato WSDL.
 */
@WebService(targetNamespace = "http://node.soap.model.server.enfok/")
public interface apiSoapNode {

    @WebMethod
    public boolean createNode(@WebParam(name = "nodeData") Node data) throws NotFoundException;

    @WebMethod
    public boolean updateNode(@WebParam(name = "nodeData") Node data) throws NotFoundException;

    @WebMethod
    public boolean deleteNode(@WebParam(name = "nodeId") String nodeId) throws NotFoundException;

    @WebMethod
    public Node getNode(@WebParam(name = "nodeId") String nodeId) throws NotFoundException;

    @WebMethod
    public List<Node> getAllNodes() throws NotFoundException;

    @WebMethod
    public Batches uploadImages(@WebParam(name = "imageData") byte[] imageData, 
                              @WebParam(name = "fileName") String fileName, 
                              @WebParam(name = "transformations") ArrayList<Transformation> transformations, 
                              @WebParam(name = "parameters") ArrayList<Transformation> parameters) throws NotFoundException;

    @WebMethod
    public Batches uploadImagesBatch(@WebParam(name = "images") ArrayList<Image> images, 
                                   @WebParam(name = "transformations") ArrayList<Transformation> transformations, 
                                   @WebParam(name = "parameters") ArrayList<Transformation> parameters) throws NotFoundException;

    @WebMethod
    public UploadBatchResult uploadBatch(@WebParam(name = "request") UploadBatchRequest request) throws NotFoundException;

    @WebMethod
    public BatchStatusResult getBatchStatusV2(@WebParam(name = "jobId") String jobId) throws NotFoundException;

    @WebMethod
    public BatchProcessedResult getBatchResults(@WebParam(name = "jobId") String jobId) throws NotFoundException;

    @WebMethod
    public String getBatchStatus(@WebParam(name = "batchId") String batchId) throws NotFoundException;

    @WebMethod
    public String getUploadStatus(@WebParam(name = "jobId") String jobId) throws NotFoundException;

    @WebMethod
    public byte[] downloadBatchResult(@WebParam(name = "jobId") String jobId) throws NotFoundException;

    @WebMethod
    public List<LogRecord> getLogsByImage(@WebParam(name = "imageUuid") String imageUuid) throws NotFoundException;

    @WebMethod
    public List<NodeMetricsBd> getMetricsByNode(@WebParam(name = "nodeId") String nodeId) throws NotFoundException;
}
