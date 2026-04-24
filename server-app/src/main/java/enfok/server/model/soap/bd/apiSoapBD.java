package enfok.server.model.soap.bd;

import java.util.ArrayList;
import java.util.List;
import enfok.server.error.InfrastructureOfflineException;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.BatchWithCover;
import enfok.server.model.entity.bd.PaginatedImages;
import enfok.server.model.entity.dto.node.NodeMetricsDTO;
import enfok.server.model.entity.dto.node.BatchProgressDTO;

@WebService
public interface apiSoapBD {

    @WebMethod
    public String getNodeStatus() throws InfrastructureOfflineException;

    @WebMethod
    public String getTransformations() throws InfrastructureOfflineException;

    @WebMethod
    public ArrayList<Image> getUserImages(@WebParam(name = "limit") int limit, 
                                          @WebParam(name = "offset") int offset) throws InfrastructureOfflineException;

    @WebMethod
    public ArrayList<Batches> getUserBatches(@WebParam(name = "limit") int limit, 
                                           @WebParam(name = "offset") int offset) throws InfrastructureOfflineException;

    @WebMethod
    public List<BatchWithCover> getUserBatchesWithCovers() throws InfrastructureOfflineException;

    @WebMethod
    public PaginatedImages getPaginatedImages(@WebParam(name = "batchUuid") String batchUuid, 
                                            @WebParam(name = "page") int page, 
                                            @WebParam(name = "limit") int limit) throws InfrastructureOfflineException;

    @WebMethod
    public List<NodeMetricsDTO> getImageMetrics(@WebParam(name = "imageUuid") String imageUuid) throws InfrastructureOfflineException;

    @WebMethod
    public BatchProgressDTO getBatchProgress(@WebParam(name = "batchUuid") String batchUuid) throws InfrastructureOfflineException;
}
