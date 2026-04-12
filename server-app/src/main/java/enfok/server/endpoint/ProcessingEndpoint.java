package enfok.server.endpoint;

import java.util.ArrayList;
import java.util.List;

import jakarta.jws.WebService;

import enfok.server.model.entity.bd.Batch;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.Node;
import enfok.server.model.entity.bd.Transformation;
import enfok.server.model.soap.bd.apiSoapBD;
import enfok.server.model.soap.node.apiSoapNode;

/**
 * Endpoint de Procesamiento.
 * 
 * Agrupa la lógica de Nodos y el manejo de imágenes/lotes (apiSoapNode y apiSoapBD).
 */
@WebService(serviceName = "ProcessingService")
public class ProcessingEndpoint implements apiSoapNode, apiSoapBD {

    // ==========================================
    // Implementación de apiSoapNode
    // ==========================================

    @Override
    public boolean createNode(String token, Node data) {
        System.out.println("Creando nodo: " + data.getHost());
        return true;
    }

    @Override
    public boolean updateNode(String token, Node data) {
        return true;
    }

    @Override
    public boolean deleteNode(String token, String nodeId) {
        return true;
    }

    @Override
    public Node getNode(String token, String nodeId) {
        Node n = new Node();
        n.setNodeId(nodeId);
        return n;
    }

    @Override
    public List<Node> getAllNodes(String token) {
        return new ArrayList<>();
    }

    @Override
    public Batch uploadImages(String token, byte[] imageData, String fileName, 
                              ArrayList<Transformation> transformations, 
                              ArrayList<Transformation> parameters) {
        Batch b = new Batch();
        b.setId(1);
        b.setStatusId(1);
        return b;
    }

    @Override
    public Batch uploadImagesBatch(String token, ArrayList<Image> images, 
                                   ArrayList<Transformation> transformations, 
                                   ArrayList<Transformation> parameters) {
        Batch b = new Batch();
        b.setId(2);
        return b;
    }

    @Override
    public String getBatchStatus(String batchId, String token) {
        return "PROCESSING";
    }

    @Override
    public String getUploadStatus(String jobId, String token) {
        return "UPLOADED";
    }

    @Override
    public byte[] downloadBatchResult(String jobId, String token) {
        return new byte[0]; // mock bytes
    }

    // ==========================================
    // Implementación de apiSoapBD
    // ==========================================

    @Override
    public String getNodeStatus(String token) {
        return "ACTIVE";
    }

    @Override
    public String getTransformations(String token) {
        return "BLUR, GRAYSCALE"; // mock types
    }

    @Override
    public ArrayList<Image> getUserImages(String token, int limit, int offset) {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Batch> getUserBatches(String token, int limit, int offset) {
        return new ArrayList<>();
    }
}
