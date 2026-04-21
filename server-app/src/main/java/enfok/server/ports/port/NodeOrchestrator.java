package enfok.server.ports.port;

import java.util.ArrayList;
import java.util.List;

import enfok.server.model.entity.bd.Node;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.Transformation;
import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.dto.node.UploadBatchRequest;
import enfok.server.model.entity.dto.node.UploadBatchResult;
import enfok.server.model.entity.dto.node.BatchStatusResult;
import enfok.server.model.entity.dto.node.BatchProcessedResult;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;

public interface NodeOrchestrator {
    public boolean createNode(String token, Node data) throws NotFoundException, InfrastructureOfflineException;
    public boolean updateNode(String token, Node data) throws NotFoundException, InfrastructureOfflineException;
    public boolean deleteNode(String token, String nodeId) throws NotFoundException, InfrastructureOfflineException;
    public Node getNode(String token, String nodeId) throws NotFoundException, InfrastructureOfflineException;
    public List<Node> getAllNodes(String token) throws NotFoundException, InfrastructureOfflineException;

    public Batches uploadImages(String token, byte[] imageData, String fileName,
            ArrayList<Transformation> transformations, ArrayList<Transformation> parameters)
            throws NotFoundException, InfrastructureOfflineException;

    public Batches uploadImagesBatch(String token, ArrayList<Image> images, ArrayList<Transformation> transformations,
            ArrayList<Transformation> parameters) throws NotFoundException, InfrastructureOfflineException;

    public String getBatchStatus(String token, String batchId) throws NotFoundException, InfrastructureOfflineException;
    public String getUploadStatus(String token, String jobId) throws NotFoundException, InfrastructureOfflineException;
    public byte[] downloadBatchResult(String token, String jobId) throws NotFoundException, InfrastructureOfflineException;
    
    // Nuevos métodos para Lotes (Batch)
    public UploadBatchResult uploadBatch(String token, UploadBatchRequest request) throws NotFoundException, InfrastructureOfflineException;
    public BatchStatusResult getBatchStatusV2(String token, String jobId) throws NotFoundException, InfrastructureOfflineException;
    public BatchProcessedResult getBatchResults(String token, String jobId) throws NotFoundException, InfrastructureOfflineException;
}
