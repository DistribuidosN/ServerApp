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
        validateServer();
        return true;
    }

    @Override
    public boolean updateNode(String token, Node data) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return true;
    }

    @Override
    public boolean deleteNode(String token, String nodeId) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return true;
    }

    @Override
    public Node getNode(String token, String nodeId) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return new Node();
    }

    @Override
    public List<Node> getAllNodes(String token) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return new ArrayList<>();
    }

    @Override
    public Batches uploadImages(String token, byte[] imageData, String fileName,
            ArrayList<Transformation> transformations, ArrayList<Transformation> parameters)
            throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        Batches batch = new Batches();
        Random rand = new Random();

        // TODO: Lógica real de HTTP POST hacia la DB o Backend real
        batch.setId(rand.nextInt(1000000)); // De 0 a 999,999
        batch.setStatusId(1);

        // --- 1. Analizar Transformaciones y Calcular Peso ---
        ImageTaskHelper.TransformationAnalysis analysis = imageHelper.analyzeTransformations(transformations);
        double taskWeight = analysis.totalWeight();
        List<String> filterNames = analysis.filterNames();

        // --- 2. Extraer Metadatos de la Imagen ---
        ImageTaskHelper.ImageMetadata metadata = imageHelper.extractMetadata(imageData);
        int realWidth = metadata.width();
        int realHeight = metadata.height();
        String imageFormat = metadata.format();

        System.out.println("Formato detectado: " + imageFormat);

        // --- 3. ¡Nuevo Encolamiento con todos los datos! ---
        taskQueue.addNewImageTask(
                String.valueOf(batch.getId()),
                realWidth,
                realHeight,
                imageFormat,
                taskWeight,
                imageData,
                filterNames,
                fileName
        );

        // --- 4. Logs ---
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
        validateServer();
        return new Batches();
    }

    @Override
    public String getBatchStatus(String token, String batchId)
            throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return "PENDING";
    }

    @Override
    public String getUploadStatus(String token, String jobId) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return "UPLOADING";
    }

    @Override
    public byte[] downloadBatchResult(String token, String jobId)
            throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return new byte[0];
    }
}

