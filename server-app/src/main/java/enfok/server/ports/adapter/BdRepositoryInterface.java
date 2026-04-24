package enfok.server.ports.adapter;

import java.util.ArrayList;
import java.util.List;
import enfok.server.model.entity.bd.*;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.model.entity.dto.node.BatchProgressDTO;
import enfok.server.model.entity.dto.node.NodeMetricsDTO;
import enfok.server.model.entity.dto.node.TransformationItem;

/**
 * Interface para el adaptador de salida (Persistence) hacia la base de datos distribuida (Go Backend).
 */
public interface BdRepositoryInterface {
    boolean validateServer() throws InfrastructureOfflineException;

    // --- Nodes ---
    boolean registerNode(Node node) throws InfrastructureOfflineException;
    List<Node> listNodes() throws InfrastructureOfflineException;
    Node getNode(String node_id) throws InfrastructureOfflineException;
    boolean heartbeatNode(String node_id) throws InfrastructureOfflineException;
    boolean updateNodeStatus(String node_id, String status) throws InfrastructureOfflineException;

    // --- Images ---
    Image getImage(String image_uuid) throws InfrastructureOfflineException;
    boolean updateImageStatus(String image_uuid, String status) throws InfrastructureOfflineException;
    boolean updateImageResult(String image_uuid, byte[] resImage, String nodeId) throws InfrastructureOfflineException;
    boolean uploadImage(String userUuid, String batch_uuid, String image_uuid, String fileName) throws InfrastructureOfflineException;

    // --- Batches ---
    Batches getBatch(String batch_uuid) throws InfrastructureOfflineException;
    List<Image> getBatchImages(String batch_uuid) throws InfrastructureOfflineException;
    boolean updateBatchStatus(String batch_uuid, String status) throws InfrastructureOfflineException;
    boolean insertBatchTransformations(String batch_uuid, List<TransformationItem> transformations) throws InfrastructureOfflineException;

    boolean uploadBatch(String userUuid, String batch_uuid) throws InfrastructureOfflineException;
    List<BatchWithCover> listUserBatchesWithCovers(String userUuid) throws InfrastructureOfflineException;
    PaginatedImages getBatchImagesPaginated(String batch_uuid, int page, int limit) throws InfrastructureOfflineException;
    BatchProgressDTO getBatchProgress(String batch_uuid) throws InfrastructureOfflineException;

    // --- Métodos Legacy para Servicio de Orquestación de BD ---
    ArrayList<Image> getUserImages(String token, int limit, int offset) throws InfrastructureOfflineException;
    ArrayList<Batches> getUserBatches(String token, int limit, int offset) throws InfrastructureOfflineException;

    // --- Logs ---
    boolean createLog(LogRecord log) throws InfrastructureOfflineException;
    List<LogRecord> getLogsByImage(String image_uuid) throws InfrastructureOfflineException;

    // --- Metrics ---
    boolean createMetrics(NodeMetricsBd metrics) throws InfrastructureOfflineException;
    List<NodeMetricsBd> getMetricsByNode(String node_id) throws InfrastructureOfflineException;
    List<NodeMetricsDTO> getMetricsByImage(String image_uuid) throws InfrastructureOfflineException;

    // --- User Statistics & Activity ---
    UserStatistics getUserStatistics(String user_uuid) throws InfrastructureOfflineException;
    List<UserActivity> getUserActivity(String user_uuid) throws InfrastructureOfflineException;

    @Deprecated
    default String getTransformations(String node_id) throws InfrastructureOfflineException { return ""; }
}
