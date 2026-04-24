package enfok.server.repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Base64;

import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.ports.adapter.NodeRepositoryInterface;
import enfok.server.ports.adapter.BdRepositoryInterface;
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
import enfok.server.model.entity.dto.node.TransformationItem;

@ApplicationScoped
public class NodeRepository implements NodeRepositoryInterface {

    @Inject
    Config config;

    @Inject
    TaskQueue taskQueue;

    @Inject
    ImageTaskHelper imageHelper;
    @Inject
    BdRepositoryInterface bdRepository;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Override
    public boolean validateServer() throws InfrastructureOfflineException {
        return true;
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
    public UploadBatchResult uploadBatch(String userUuid, UploadBatchRequest request) throws NotFoundException, InfrastructureOfflineException {


        // 1. TODO: Insertar registro del Batch en la BD (PENDING)
        String batchUuid = request.getId();

        // 1. Registro del Batch (Write-Through)
        bdRepository.uploadBatch(userUuid, batchUuid);
        
        // 2. Registro MASIVO del pipeline de transformaciones (con parámetros)
        bdRepository.insertBatchTransformations(batchUuid, request.getFilters());

        // --- Analizar Peso de Transformaciones para Planificación ---
        ImageTaskHelper.TransformationAnalysis analysis = imageHelper.analyzeTransformationItems(request.getFilters());
        double taskWeight = analysis.totalWeight();

        // 3. Procesar y encolar cada imagen
        for (ImageItemBatch imageItem : request.getImages()) {
            byte[] imageData = Base64.getDecoder().decode(imageItem.getBase64());
            ImageTaskHelper.ImageMetadata metadata = imageHelper.extractMetadata(imageData);

            // --- Encolar en memoria con parámetros ---
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
            
            // --- Registrar imagen individual ---
            bdRepository.uploadImage(userUuid, batchUuid, imageItem.getId(), imageItem.getName());
        }

        return new UploadBatchResult(batchUuid, "ACCEPTED", "Lote recibido y pipeline de filtros registrado correctamente.");
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
        // Generamos un identificador único para el lote
        batch.setBatchUuid(UUID.randomUUID().toString());
        batch.setStatus("PENDING");

        // --- 1. Analizar Transformaciones y Calcular Peso ---
        ImageTaskHelper.TransformationAnalysis analysis = imageHelper.analyzeTransformations(transformations);
        double taskWeight = analysis.totalWeight();
        List<TransformationItem> filters = analysis.filters();

        // --- 2. Extraer Metadatos de la Imagen ---
        ImageTaskHelper.ImageMetadata metadata = imageHelper.extractMetadata(imageData);
        int realWidth = metadata.width();
        int realHeight = metadata.height();
        String imageFormat = metadata.format();

        System.out.println("Formato detectado: " + imageFormat);

        // --- 3. ¡Nuevo Encolamiento con todos los datos! ---
        taskQueue.addNewImageTask(
                batch.getBatchUuid(),
                realWidth,
                realHeight,
                imageFormat,
                taskWeight,
                imageData,
                filters,
                fileName
        );


        // --- 4. Logs ---
        System.out.println("====== TAREA ENCOLADA ======");
        System.out.println("BATCH ID     : " + batch.getBatchUuid());
        System.out.println("DIMENSIONES  : " + realWidth + "x" + realHeight + "px");
        System.out.println("PESO TOTAL   : " + taskWeight);
        System.out.println("FILTROS      : " + filters);
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
