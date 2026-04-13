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
import enfok.server.model.soap.node.apiSoapNode;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.ports.port.NodeOrchestrator;
import enfok.server.utility.TokenMapper;

/**
 * Endpoint de Procesamiento de Nodos (SOAP Service).
 * Controla el ciclo de vida de los Nodos worker (registrar, obtener, eliminar) y expone
 * los métodos directos para subir imágenes e iniciar Batches de conversión pesada.
 */
@WebService(endpointInterface = "enfok.server.model.soap.node.apiSoapNode", serviceName = "NodeService")
public class NodeEndpoint implements apiSoapNode {

    @Inject
    NodeOrchestrator nodeOrchestrator;

    @Inject
    TokenMapper tokenMapper;

    @Resource
    WebServiceContext context;

    /**
     * Registra un nuevo Nodo en el Cluster para poder asginarle cargas posteriormente.
     * @param data Payload con IP, puerto e información del host.
     * @return true si el nodo se integra a la DB.
     */
    @Override
    public boolean createNode(Node data) throws NotFoundException {
        try {
            return nodeOrchestrator.createNode(tokenMapper.extractToken(context), data);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    @Override
    public boolean updateNode(Node data) throws NotFoundException {
        try {
            return nodeOrchestrator.updateNode(tokenMapper.extractToken(context), data);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    @Override
    public boolean deleteNode(String nodeId) throws NotFoundException {
        try {
            return nodeOrchestrator.deleteNode(tokenMapper.extractToken(context), nodeId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    @Override
    public Node getNode(String nodeId) throws NotFoundException {
        try {
            return nodeOrchestrator.getNode(tokenMapper.extractToken(context), nodeId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    @Override
    public List<Node> getAllNodes() throws NotFoundException {
        try {
            return nodeOrchestrator.getAllNodes(tokenMapper.extractToken(context));
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    /**
     * Remite una imagen (en Bytes) conteniendo transformaciones anidadas directas hacia el NodeLoadBalancer 
     * el cual determinará cuál proceso físico se hará cargo.
     * @param imageData Arreglo de bits en crudo de la fotografía/imagen a tratar.
     * @param fileName Nombre legítimo del archivo para guardar la salida.
     * @param transformations Múltiples filtros a aplicar en secuencia.
     * @return Un objeto Batches con un ID de seguimiento (jobId) del servidor de Nodos.
     */
    @Override
    public Batches uploadImages(byte[] imageData, String fileName, 
                              ArrayList<Transformation> transformations, 
                              ArrayList<Transformation> parameters) throws NotFoundException {
        try {
            return nodeOrchestrator.uploadImages(tokenMapper.extractToken(context), imageData, fileName, transformations, parameters);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    @Override
    public Batches uploadImagesBatch(ArrayList<Image> images, 
                                   ArrayList<Transformation> transformations, 
                                   ArrayList<Transformation> parameters) throws NotFoundException {
        try {
            return nodeOrchestrator.uploadImagesBatch(tokenMapper.extractToken(context), images, transformations, parameters);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    /**
     * Comprueba el estado asíncrono actual de transmisión general del Batch enviado al nodo.
     * @param batchId Identificador devuelto por los métodos de upload.
     * @return Texto: PENDING, UPLOADING, PROCESSING, FINISHED.
     */
    @Override
    public String getBatchStatus(String batchId) throws NotFoundException {
        try {
            return nodeOrchestrator.getBatchStatus(tokenMapper.extractToken(context), batchId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    @Override
    public String getUploadStatus(String jobId) throws NotFoundException {
        try {
            return nodeOrchestrator.getUploadStatus(tokenMapper.extractToken(context), jobId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    @Override
    public byte[] downloadBatchResult(String jobId) throws NotFoundException {
        try {
            return nodeOrchestrator.downloadBatchResult(tokenMapper.extractToken(context), jobId);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor de nodos est\u00E1 fuera de l\u00EDnea.", e);
        }
    }
}
