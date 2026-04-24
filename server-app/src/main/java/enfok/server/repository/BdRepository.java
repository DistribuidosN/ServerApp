package enfok.server.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import enfok.server.ports.adapter.BdRepositoryInterface;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.model.entity.bd.*;
import enfok.server.utility.JsonMapper;
import enfok.server.model.entity.dto.node.TransformationItem;

@ApplicationScoped
public class BdRepository implements BdRepositoryInterface {

    @Inject
    Config config;

    @Inject
    NetworkValidator networkValidator;

    @Inject
    JsonMapper jsonMapper;

    private final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
            .connectTimeout(java.time.Duration.ofSeconds(5))
            .build();

    private String getBaseUrl() {

        String url = "http://" + config.getBdSystem();
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    private String getApiUrl() {
        return getBaseUrl() + "/api/v1";
    }

    @Override
    public boolean validateServer() throws InfrastructureOfflineException {
        return networkValidator.validate(getBaseUrl());
    }

    // --- Nodes ---

    @Override
    public boolean registerNode(Node node) throws InfrastructureOfflineException {
        validateServer();
        try {
            String json = jsonMapper.toJson(node);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/nodes"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 201 || response.statusCode() == 200;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al registrar nodo en BD: " + e.getMessage());
        }
    }

    @Override
    public List<Node> listNodes() throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/nodes"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), new TypeReference<List<Node>>() {});
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al listar nodos: " + e.getMessage());
        }
    }

    @Override
    public Node getNode(String nodeId) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/nodes/" + nodeId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), Node.class);
            }
            return null;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al obtener nodo: " + e.getMessage());
        }
    }

    @Override
    public boolean updateNodeStatus(String nodeId, String status) throws InfrastructureOfflineException {
        validateServer();
        try {
            String json = jsonMapper.toJson(Map.of("status", status));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/nodes/" + nodeId + "/status"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al actualizar estado de nodo: " + e.getMessage());
        }
    }


    @Override
    public boolean heartbeatNode(String nodeId) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/nodes/" + nodeId + "/heartbeat"))
                    .POST(BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al enviar heartbeat: " + e.getMessage());
        }
    }


    // --- Images ---

    @Override
    public Image getImage(String id) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/images/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), Image.class);
            }
            return null;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al obtener imagen: " + e.getMessage());
        }
    }

    @Override
    public boolean updateImageStatus(String id, String status) throws InfrastructureOfflineException {
        validateServer();
        try {
            String json = jsonMapper.toJson(Map.of("status", status));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/images/" + id + "/status"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al actualizar estado de imagen: " + e.getMessage());
        }
    }

    @Override
    public boolean updateImageResult(String id, byte[] resImage, String nodeId) throws InfrastructureOfflineException {
        validateServer();
        try {
            // [SENIOR OPTIMIZATION]: Enviamos bytes puros y el NodeID en un Header
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/images/" + id + "/result"))
                    .header("Content-Type", "application/octet-stream")
                    .header("X-Node-Id", nodeId != null ? nodeId : "unknown")
                    .method("PATCH", BodyPublishers.ofByteArray(resImage))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al actualizar resultado de imagen: " + e.getMessage());
        }
    }

    @Override
    public boolean uploadImage(String userUuid, String batch_uuid, String image_uuid, String fileName) throws InfrastructureOfflineException {
        validateServer();
        try {
            // Registro de imagen (JSON)
            Map<String, String> body = Map.of(
                "user_uuid", userUuid,
                "batch_uuid", batch_uuid,
                "image_uuid", image_uuid,
                "file_name", fileName
            );
            String json = jsonMapper.toJson(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/images"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 201 || response.statusCode() == 200;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al registrar imagen: " + e.getMessage());
        }
    }

    // --- Batches ---

    @Override
    public Batches getBatch(String id) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/batches/" + id))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), Batches.class);
            }
            return null;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al obtener batch: " + e.getMessage());
        }
    }

    @Override
    public List<Image> getBatchImages(String id) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/batches/" + id + "/images"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), new TypeReference<List<Image>>() {});
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al obtener imágenes del batch: " + e.getMessage());
        }
    }

    @Override
    public boolean updateBatchStatus(String id, String status) throws InfrastructureOfflineException {
        validateServer();
        try {
            String json = jsonMapper.toJson(Map.of("status", status));
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/batches/" + id + "/status"))
                    .header("Content-Type", "application/json")
                    .method("PATCH", BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al actualizar estado de batch: " + e.getMessage());
        }
    }

    @Override
    public boolean insertBatchTransformations(String batch_uuid, List<TransformationItem> transformations) throws InfrastructureOfflineException {
        validateServer();
        if (transformations == null || transformations.isEmpty()) {
            System.out.println("[DEBUG] No hay transformaciones para insertar en el batch: " + batch_uuid);
            return true;
        }

        System.out.println("[DEBUG] Intentando insertar " + transformations.size() + " transformaciones para batch: " + batch_uuid);
        
        boolean allSuccess = true;
        for (TransformationItem t : transformations) {
            try {
                // Enviamos como lista de un solo elemento para mantener compatibilidad con el binding de Go
                String json = jsonMapper.toJson(List.of(t));
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(getApiUrl() + "/batches/" + batch_uuid + "/transformations"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 201 && response.statusCode() != 200) {
                    allSuccess = false;
                    System.err.println("[ERROR] Fallo al insertar transformación " + t.getName() + ": Status " + response.statusCode());
                }
            } catch (Exception e) {
                allSuccess = false;
                System.err.println("[ERROR] Excepción al insertar transformación: " + e.getMessage());
            }
        }
        return allSuccess;
    }

    @Override
    public List<BatchWithCover> listUserBatchesWithCovers(String userUuid) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/batches?user_uuid=" + userUuid))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), new TypeReference<List<BatchWithCover>>() {});
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al listar batches con portada: " + e.getMessage());
        }
    }

    @Override
    public PaginatedImages getBatchImagesPaginated(String batchUuid, int page, int limit) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/batches/" + batchUuid + "/images?page=" + page + "&limit=" + limit))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), PaginatedImages.class);
            }
            return null;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al obtener galería paginada: " + e.getMessage());
        }
    }

    @Override
    public boolean uploadBatch(String userUuid, String batch_uuid) throws InfrastructureOfflineException {
        validateServer();
        try {
            Map<String, String> body = Map.of(
                "user_uuid", userUuid,
                "batch_uuid", batch_uuid
            );
            String json = jsonMapper.toJson(body);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/batches/upload"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 201 || response.statusCode() == 200;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al registrar batch: " + e.getMessage());
        }
    }


    // --- Logs ---

    @Override
    public boolean createLog(LogRecord logRecord) throws InfrastructureOfflineException {
        validateServer();
        try {
            String json = jsonMapper.toJson(logRecord);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/logs"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 201;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al crear log: " + e.getMessage());
        }
    }

    @Override
    public List<LogRecord> getLogsByImage(String imageUuid) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/logs/" + imageUuid))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), new TypeReference<List<LogRecord>>() {});
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al obtener logs: " + e.getMessage());
        }
    }

    // --- Metrics ---

    @Override
    public boolean createMetrics(NodeMetricsBd metrics) throws InfrastructureOfflineException {
        validateServer();
        try {
            String json = jsonMapper.toJson(metrics);
            System.out.println("[DEBUG] Enviando métricas a Go: " + json);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/metrics"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 201;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al crear métricas: " + e.getMessage());
        }
    }

    @Override
    public List<NodeMetricsBd> getMetricsByNode(String nodeId) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/metrics/" + nodeId))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), new TypeReference<List<NodeMetricsBd>>() {});
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al obtener métricas del nodo: " + e.getMessage());
        }
    }

    // --- User ---

    @Override
    public UserStatistics getUserStatistics(String userUuid) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/users/" + userUuid + "/statistics"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), UserStatistics.class);
            }
            return null;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al obtener estadísticas de usuario: " + e.getMessage());
        }
    }

    @Override
    public List<UserActivity> getUserActivity(String userUuid) throws InfrastructureOfflineException {
        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getApiUrl() + "/users/" + userUuid + "/activity"))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return jsonMapper.fromJson(response.body(), new TypeReference<List<UserActivity>>() {});
            }
            return new ArrayList<>();
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error al obtener actividad de usuario: " + e.getMessage());
        }
    }

    @Override
    public ArrayList<Image> getUserImages(String token, int limit, int offset) throws InfrastructureOfflineException {
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Batches> getUserBatches(String token, int limit, int offset) throws InfrastructureOfflineException {
        return new ArrayList<>();
    }
}

