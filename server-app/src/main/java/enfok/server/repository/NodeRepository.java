package enfok.server.repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.ports.adapter.NodeRepositoryInterface;
import enfok.server.model.entity.bd.Node;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.Transformation;
import enfok.server.model.entity.bd.TransformationType;
import enfok.server.model.entity.bd.Batches;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import enfok.server.utility.TaskQueue;
import enfok.server.utility.ImageTaskHelper;
import java.util.Random;

@ApplicationScoped
public class NodeRepository implements NodeRepositoryInterface {

    @Inject
    Config config;

    @Inject
    TaskQueue taskQueue;

    @Inject
    NetworkValidator networkValidator;

    @Inject
    ImageTaskHelper imageHelper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private String getBaseUrl() {
        String url = config.getNode();
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    @Override
    public boolean validateServer() throws InfrastructureOfflineException {
        return networkValidator.validate(getBaseUrl());
    }

    @Override
    public boolean createNode(String token, Node data) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices())
            return true;

        validateServer();
        return true;
    }

    @Override
    public boolean updateNode(String token, Node data) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices())
            return true;

        validateServer();
        return true;
    }

    @Override
    public boolean deleteNode(String token, String nodeId) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices())
            return true;

        validateServer();
        return true;
    }

    @Override
    public Node getNode(String token, String nodeId) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) {
            Node n = new Node();
            n.setNodeId(nodeId);
            n.setHost("localhost:8080");
            return n;
        }

        validateServer();
        return new Node();
    }

    @Override
    public List<Node> getAllNodes(String token) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) {
            List<Node> nodes = new ArrayList<>();
            Node n1 = new Node();
            n1.setNodeId("local-node-1");
            n1.setHost("127.0.0.1:9091");
            nodes.add(n1);
            return nodes;
        }

        validateServer();
        return new ArrayList<>();
    }

    @Override
    public Batches uploadImages(String token, byte[] imageData, String fileName,
            ArrayList<Transformation> transformations, ArrayList<Transformation> parameters)
            throws NotFoundException, InfrastructureOfflineException {
        Batches batch = new Batches();
        Random rand = new Random();

        if (config.isMockServices()) {
            batch.setId(rand.nextInt(1000000)); // De 0 a 999,999
            batch.setStatusId(1);
        } else {
            validateServer();
            // TODO: Lógica real de HTTP POST hacia la DB o Backend real
            batch.setId(1);
            batch.setStatusId(2);
        }

        // --- 1. Analizar Transformaciones y Calcular Peso ---
        ImageTaskHelper.TransformationAnalysis analysis = imageHelper.analyzeTransformations(transformations);
        double taskWeight = analysis.totalWeight();
        List<String> filterNames = analysis.filterNames();

        // --- 2. Extraer Metadatos de la Imagen ---
        ImageTaskHelper.ImageMetadata metadata = imageHelper.extractMetadata(imageData);
        int realWidth = metadata.width();
        int realHeight = metadata.height();
        String imageFormat = metadata.format();

        // (Opcional) Puedes imprimir el formato para verificar
        System.out.println("Formato detectado: " + imageFormat);

        // --- 3. ¡Nuevo Encolamiento con todos los datos! ---
        taskQueue.addNewImageTask(
                String.valueOf(batch.getId()),
                realWidth,
                realHeight,
                imageFormat,
                taskWeight,
                imageData, // Pasamos los bytes puros
                filterNames, // Pasamos la lista de filtros ["blur", "ocr"]
                fileName // Pasamos el nombre del archivo
        );

        // --- 4. Logs actualizados para ver qué está entrando ---
        System.out.println("====== TAREA ENCOLADA ======");
        System.out.println("BATCH ID     : " + batch.getId());
        System.out.println("DIMENSIONES  : " + realWidth + "x" + realHeight + "px");
        System.out.println("PESO TOTAL   : " + taskWeight);
        System.out.println("FILTROS      : " + filterNames);
        System.out.println("TAMAÑO BYTES : " + (imageData != null ? imageData.length : 0) + " bytes");
        System.out.println("ARCHIVO      : " + fileName);
        System.out.println("============================");

        return batch;
    }

    @Override
    public Batches uploadImagesBatch(String token, ArrayList<Image> images, ArrayList<Transformation> transformations, ArrayList<Transformation> parameters) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) {
            Batches batch = new Batches();
            Random rand = new Random();
            batch.setId(rand.nextInt(1000000));

            batch.setStatusId(1);
            return batch;
        }

        validateServer();
        return new Batches();
    }

    @Override
    public String getBatchStatus(String token, String batchId)
            throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices())
            return "FINISHED_MOCK";

        validateServer();
        return "PENDING";
    }

    @Override
    public String getUploadStatus(String token, String jobId) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices())
            return "UPLOADED_MOCK";

        validateServer();
        return "UPLOADING";
    }

    @Override
    public byte[] downloadBatchResult(String token, String jobId)
            throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) {
            return new byte[] { 0x4D, 0x4F, 0x43, 0x4B }; // MOCK
        }

        validateServer();
        return new byte[0];
    }
}
