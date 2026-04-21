package enfok.server.endpoint;

import java.util.ArrayList;
import java.util.List;

import jakarta.jws.WebService;
import jakarta.inject.Inject;
import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;

import enfok.server.model.entity.bd.Node;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.Transformation;
import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.dto.node.UploadBatchRequest;
import enfok.server.model.entity.dto.node.UploadBatchResult;
import enfok.server.model.entity.dto.node.BatchStatusResult;
import enfok.server.model.entity.dto.node.BatchProcessedResult;
import enfok.server.model.soap.node.apiSoapNode;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.ports.port.NodeOrchestrator;
import enfok.server.utility.TokenMapper;

/**
 * Endpoint de Procesamiento de Nodos (SOAP Service).
 * Implementa el contrato definido en apiSoapNode con el namespace http://node.soap.model.server.enfok/
 */
@WebService(endpointInterface = "enfok.server.model.soap.node.apiSoapNode", serviceName = "NodeService")
public class NodeEndpoint implements apiSoapNode {

    @Inject
    NodeOrchestrator nodeOrchestrator;

    @Inject
    TokenMapper tokenMapper;

    @Resource
    WebServiceContext context;

    @Override
    public boolean createNode(Node data) throws NotFoundException {
        try {
            return nodeOrchestrator.createNode(tokenMapper.extractToken(context), data);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public boolean updateNode(Node data) throws NotFoundException {
        try {
            return nodeOrchestrator.updateNode(tokenMapper.extractToken(context), data);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public boolean deleteNode(String nodeId) throws NotFoundException {
        try {
            return nodeOrchestrator.deleteNode(tokenMapper.extractToken(context), nodeId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public Node getNode(String nodeId) throws NotFoundException {
        try {
            return nodeOrchestrator.getNode(tokenMapper.extractToken(context), nodeId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public List<Node> getAllNodes() throws NotFoundException {
        try {
            return nodeOrchestrator.getAllNodes(tokenMapper.extractToken(context));
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public Batches uploadImages(byte[] imageData, String fileName, 
                              ArrayList<Transformation> transformations, 
                              ArrayList<Transformation> parameters) throws NotFoundException {
        try {
            return nodeOrchestrator.uploadImages(tokenMapper.extractToken(context), imageData, fileName, transformations, parameters);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public Batches uploadImagesBatch(ArrayList<Image> images, 
                                   ArrayList<Transformation> transformations, 
                                   ArrayList<Transformation> parameters) throws NotFoundException {
        try {
            return nodeOrchestrator.uploadImagesBatch(tokenMapper.extractToken(context), images, transformations, parameters);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public UploadBatchResult uploadBatch(UploadBatchRequest request) throws NotFoundException {
        try {
            return nodeOrchestrator.uploadBatch(tokenMapper.extractToken(context), request);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public BatchStatusResult getBatchStatusV2(String jobId) throws NotFoundException {
        try {
            return nodeOrchestrator.getBatchStatusV2(tokenMapper.extractToken(context), jobId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("Error al consultar el estado del lote.", e);
        }
    }

    @Override
    public BatchProcessedResult getBatchResults(String jobId) throws NotFoundException {
        try {
            return nodeOrchestrator.getBatchResults(tokenMapper.extractToken(context), jobId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("Error al obtener resultados del lote.", e);
        }
    }

    @Override
    public String getBatchStatus(String batchId) throws NotFoundException {
        try {
            return nodeOrchestrator.getBatchStatus(tokenMapper.extractToken(context), batchId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public String getUploadStatus(String jobId) throws NotFoundException {
        try {
            return nodeOrchestrator.getUploadStatus(tokenMapper.extractToken(context), jobId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }

    @Override
    public byte[] downloadBatchResult(String jobId) throws NotFoundException {
        try {
            return nodeOrchestrator.downloadBatchResult(tokenMapper.extractToken(context), jobId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos está fuera de línea.", e);
        }
    }
}
