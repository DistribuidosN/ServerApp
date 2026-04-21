package enfok.server.repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Base64;

import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.ports.adapter.NodeRepositoryInterface;
import enfok.server.ports.adapter.MessageQueueProducer;
import enfok.server.model.entity.bd.Node;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.Transformation;
import enfok.server.model.entity.bd.TransformationType;
import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.dto.node.UploadBatchRequest;
import enfok.server.model.entity.dto.node.UploadBatchResult;
import enfok.server.model.entity.dto.node.ImageItemBatch;
import enfok.server.model.entity.dto.node.BatchStatusResult;
import enfok.server.model.entity.dto.node.BatchProcessedResult;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import enfok.server.utility.TaskQueue;
import enfok.server.utility.ImageTaskHelper;

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
    public UploadBatchResult uploadBatch(String token, UploadBatchRequest request) throws NotFoundException, InfrastructureOfflineException {
        validateServer();

        // 1. TODO: Insertar registro del Batch en la BD (PENDING)
        // String batchUuid = request.getId();

        // 2. Procesar cada imagen del lote con la misma lógica de uploadImages
        for (ImageItemBatch imageItem : request.getImages()) {
            
            // Decodificar Base64 a bytes
            byte[] imageData = Base64.getDecoder().decode(imageItem.getBase64());

            // --- A. Calcular Peso de Transformaciones (Filtros) ---
            double taskWeight = 0;
            for (String filterName : request.getFilters()) {
                taskWeight += TransformationType.getWeightByName(filterName);
            }
            if (taskWeight <= 0) taskWeight = 1.0;

            // --- B. Extraer Metadatos de la Imagen ---
            ImageTaskHelper.ImageMetadata metadata = imageHelper.extractMetadata(imageData);

            // --- C. Encolar en la TaskQueue principal (Lógica de uploadImages) ---
            taskQueue.addNewImageTask(
                    imageItem.getId(),
                    metadata.width(),
                    metadata.height(),
                    metadata.format(),
                    taskWeight,
                    imageData,
                    request.getFilters(),
                    imageItem.getName()
            );

            // --- D. TODO: Insertar registro de la imagen asociada al Batch en BD ---
            // db.insertBatchImage(request.getId(), imageItem.getId(), ...);
        }

        return new UploadBatchResult(request.getId(), "ACCEPTED", "El lote ha sido recibido y sus imágenes han sido encoladas para procesamiento.");
    }

    @Override
    public BatchStatusResult getBatchStatusV2(String jobId) throws NotFoundException {
        // Lógica requerida: Consultar cuántas imágenes corresponden al mismo jobId
        // y calcular porcentajes y estado.
        
        // TODO: Consultar en la base de datos la totalidad y procesados para el jobId
        // int total = db.countImagesByBatch(jobId);
        // int processed = db.countProcessedImagesByBatch(jobId);
        
        int total = 10; // Mock
        int processed = 5; // Mock
        double percentage = (double) processed / total * 100;
        String status = (processed == total) ? "COMPLETED" : "PROCESSING";

        return new BatchStatusResult(jobId, status, percentage, total, processed);
    }

    @Override
    public BatchProcessedResult getBatchProcessedImages(String jobId) throws NotFoundException {
        // Primero verificamos el estado (Regla de negocio: Solo si está COMPLETED)
        BatchStatusResult statusInfo = getBatchStatusV2(jobId);
        
        if (!"COMPLETED".equals(statusInfo.getStatus())) {
            throw new NotFoundException("El lote " + jobId + " aún no se ha completado.");
        }

        // TODO: Consultar todas las imágenes procesadas con su Base64 de la BD
        // List<ImageItemBatch> results = db.getProcessedImages(jobId);
        
        List<ImageItemBatch> results = new ArrayList<>();
        return new BatchProcessedResult(jobId, results);
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
    public String getBatchStatus(String token, String batchId) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return "PENDING";
    }

    @Override
    public String getUploadStatus(String token, String jobId) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return "UPLOADING";
    }

    @Override
    public byte[] downloadBatchResult(String token, String jobId) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        return new byte[0];
    }
}
