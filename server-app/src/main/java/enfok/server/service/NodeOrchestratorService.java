package enfok.server.service;

import enfok.server.ports.port.NodeOrchestrator;
import enfok.server.ports.adapter.NodeRepositoryInterface;
import enfok.server.utility.NodeLoadBalancer;
import enfok.server.utility.TaskQueue;
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
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NodeOrchestratorService implements NodeOrchestrator {

    @Inject
    private NodeRepositoryInterface nodeRepository;

    @Inject
    private NodeLoadBalancer loadBalancer;

    @Inject
    private TaskQueue taskQueue;

    @Override
    public boolean createNode(String token, Node data) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("Token de autorizaci\u00F3n no proporcionado.");
        }
        if (data == null){
            throw new NotFoundException("Payload de Node vac\u00EDo.");
        }
        boolean result = nodeRepository.createNode(token, data);
        if (!result){
            throw new NotFoundException("Fallo al crear el Nodo en la DB.");
        }
        return result;
    }

    @Override
    public boolean updateNode(String token, Node data) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()) throw new NotFoundException("Token de autorizaci\u00F3n no proporcionado.");
        if (data == null) throw new NotFoundException("Payload vac\u00EDo.");
        boolean result = nodeRepository.updateNode(token, data);
        if (!result) throw new NotFoundException("El nodo a actualizar no existe.");
        return result;
    }

    @Override
    public boolean deleteNode(String token, String nodeId) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()) {
            throw new NotFoundException("Token de autorizaci\u00F3n no proporcionado.");
        }
        boolean result = nodeRepository.deleteNode(token, nodeId);
        if (!result){
            throw new NotFoundException("El nodo no pudo ser eliminado (No encontrado).");
        }
        return result;
    }

    @Override
    public Node getNode(String token, String nodeId) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("No hay token.");   
        }
        Node result = nodeRepository.getNode(token, nodeId);
        if (result == null){
            throw new NotFoundException("No existe nodo con el id: " + nodeId);
        }
        return result;
    }

    @Override
    public List<Node> getAllNodes(String token) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("Token missing.");
        }
        List<Node> result = nodeRepository.getAllNodes(token);
        if (result == null){
            throw new NotFoundException("La lista de nodos retorn\u00F3 null.");
        }
        return result;
    }

    @Override
    public Batches uploadImages(String token, byte[] imageData, String fileName, ArrayList<Transformation> transformations, ArrayList<Transformation> parameters) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("Token faltante.");
        } 
        if (imageData == null || imageData.length == 0) {
            throw new NotFoundException("ImageData est\u00E1 vac\u00EDo.");
        }
        
        Batches result = nodeRepository.uploadImages(token, imageData, fileName, transformations, parameters);
        if (result == null){
            throw new NotFoundException("No se proces\u00F3 y no se gener\u00F3 el registro de Batch.");
        }
    
        return result;
    }

    @Override
    public Batches uploadImagesBatch(String token, ArrayList<Image> images, ArrayList<Transformation> transformations, ArrayList<Transformation> parameters) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw   new NotFoundException("Token faltante.");
        }
        if (images == null || images.isEmpty()){
            throw new NotFoundException("Lista de im\u00E1genes vac\u00EDa.");
        }
        
        List<Node> availableNodes = nodeRepository.getAllNodes(token);
        Node selectedNode = loadBalancer.selectNodeToProcess(availableNodes);
        
        Batches result = nodeRepository.uploadImagesBatch(token, images, transformations, parameters);
        if (result == null){
            throw new NotFoundException("Error al estructurar el lote (Batch nulo).");
        }
        return result;
    }

    @Override
    public UploadBatchResult uploadBatch(String token, UploadBatchRequest request) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()) {
            throw new NotFoundException("Token faltante.");
        }
        if (request == null || request.getId() == null) {
            throw new NotFoundException("El cuerpo de la peticion del lote es invalido.");
        }
        
        UploadBatchResult result = nodeRepository.uploadBatch(token, request);
        
        if (result == null) {
            throw new NotFoundException("No se pudo iniciar el procesamiento del lote.");
        }
        
        return result;
    }

    @Override
    public BatchStatusResult getBatchStatusV2(String token, String jobId) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()) {
            throw new NotFoundException("Token de autorizaci\u00F3n inv\u00E1lido.");
        }
        
        BatchStatusResult result = nodeRepository.getBatchStatusV2(jobId);
        
        if (result == null) {
            throw new NotFoundException("No se encontr\u00F3 informaci\u00F3n para el lote: " + jobId);
        }
        
        return result;
    }

    @Override
    public BatchProcessedResult getBatchResults(String token, String jobId) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()) {
            throw new NotFoundException("Token de autorizaci\u00F3n requerido.");
        }
        
        // Regla de negocio: Validar que esté completado antes de pedir resultados
        BatchStatusResult status = nodeRepository.getBatchStatusV2(jobId);
        if (status == null || !"COMPLETED".equals(status.getStatus())) {
            throw new NotFoundException("Los resultados no est\u00E1n listos o el lote no existe.");
        }
        
        BatchProcessedResult result = nodeRepository.getBatchProcessedImages(jobId);
        if (result == null) {
            throw new NotFoundException("Error al recuperar los resultados del lote.");
        }
        
        return result;
    }

    @Override
    public String getBatchStatus(String token, String batchId) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
                throw new NotFoundException("Sin autorizacion.");
        }
        String result = nodeRepository.getBatchStatus(token, batchId);
        if (result == null || result.isEmpty()){
            throw new NotFoundException("No se encontr\u00F3 el batch asociado.");
        }
        return result;
    }

    @Override
    public String getUploadStatus(String token, String jobId) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
                throw new NotFoundException("Token invalid.");
        }
        String result = nodeRepository.getUploadStatus(token, jobId);
        if (result == null){
            throw new NotFoundException("El job especificado no existe.");
        }
        return result;
    }

    @Override
    public byte[] downloadBatchResult(String token, String jobId) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("Token requerido.");
        }
        byte[] result = nodeRepository.downloadBatchResult(token, jobId);
        if (result == null){
            throw new NotFoundException("El job " + jobId + " no cuenta con resultados o no existe.");
        }
        return result;
    }
}
