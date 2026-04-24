package enfok.server.ports.port;

import java.util.ArrayList;
import java.util.List;
import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.BatchWithCover;
import enfok.server.model.entity.bd.PaginatedImages;
import enfok.server.model.entity.dto.node.NodeMetricsDTO;
import enfok.server.model.entity.dto.node.BatchProgressDTO;
import enfok.server.error.InfrastructureOfflineException;

public interface BdOrchestrator {
    public String getNodeStatus(String token) throws InfrastructureOfflineException;
    public String getTransformations(String token) throws InfrastructureOfflineException;
    public ArrayList<Image> getUserImages(String token, int limit, int offset) throws InfrastructureOfflineException;
    public ArrayList<Batches> getUserBatches(String token, int limit, int offset) throws InfrastructureOfflineException;
    public List<BatchWithCover> getUserBatchesWithCovers(String token) throws InfrastructureOfflineException;
    public PaginatedImages getPaginatedImages(String token, String batchUuid, int page, int limit) throws InfrastructureOfflineException;
    public List<NodeMetricsDTO> getImageMetrics(String token, String imageUuid) throws InfrastructureOfflineException;
    public BatchProgressDTO getBatchProgress(String token, String batchUuid) throws InfrastructureOfflineException;
}
