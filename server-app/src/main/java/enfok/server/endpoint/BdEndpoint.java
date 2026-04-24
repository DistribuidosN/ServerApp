package enfok.server.endpoint;

import java.util.ArrayList;
import java.util.List;

import jakarta.jws.WebService;

import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.BatchWithCover;
import enfok.server.model.entity.bd.PaginatedImages;
import enfok.server.model.entity.dto.node.NodeMetricsDTO;
import enfok.server.model.entity.dto.node.BatchProgressDTO;
import enfok.server.model.soap.bd.apiSoapBD;

import enfok.server.error.InfrastructureOfflineException;
import enfok.server.ports.port.BdOrchestrator;
import enfok.server.utility.TokenMapper;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.xml.ws.WebServiceContext;

/**
 * Endpoint de Consulta de Base de Datos (SOAP Service).
 */
@WebService(endpointInterface = "enfok.server.model.soap.bd.apiSoapBD", serviceName = "BdService")
public class BdEndpoint implements apiSoapBD {

    @Inject
    BdOrchestrator bdOrchestrator;

    @Inject
    TokenMapper tokenMapper;

    @Resource
    WebServiceContext context;

    @Override
    public String getNodeStatus() throws InfrastructureOfflineException {
        return bdOrchestrator.getNodeStatus(tokenMapper.extractToken(context));
    }

    @Override
    public String getTransformations() throws InfrastructureOfflineException {
        return bdOrchestrator.getTransformations(tokenMapper.extractToken(context));
    }

    @Override
    public ArrayList<Image> getUserImages(int limit, int offset) throws InfrastructureOfflineException {
        return bdOrchestrator.getUserImages(tokenMapper.extractToken(context), limit, offset);
    }

    @Override
    public ArrayList<Batches> getUserBatches(int limit, int offset) throws InfrastructureOfflineException {
        return bdOrchestrator.getUserBatches(tokenMapper.extractToken(context), limit, offset);
    }

    @Override
    public List<BatchWithCover> getUserBatchesWithCovers() throws InfrastructureOfflineException {
        return bdOrchestrator.getUserBatchesWithCovers(tokenMapper.extractToken(context));
    }

    @Override
    public PaginatedImages getPaginatedImages(String batchUuid, int page, int limit) throws InfrastructureOfflineException {
        return bdOrchestrator.getPaginatedImages(tokenMapper.extractToken(context), batchUuid, page, limit);
    }

    @Override
    public List<NodeMetricsDTO> getImageMetrics(String imageUuid) throws InfrastructureOfflineException {
        return bdOrchestrator.getImageMetrics(tokenMapper.extractToken(context), imageUuid);
    }

    @Override
    public BatchProgressDTO getBatchProgress(String batchUuid) throws InfrastructureOfflineException {
        return bdOrchestrator.getBatchProgress(tokenMapper.extractToken(context), batchUuid);
    }
}
