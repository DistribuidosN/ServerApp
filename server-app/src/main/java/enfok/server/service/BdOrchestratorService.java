package enfok.server.service;

import enfok.server.ports.port.BdOrchestrator;
import enfok.server.ports.adapter.BdRepositoryInterface;
import java.util.ArrayList;
import java.util.List;
import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.BatchWithCover;
import enfok.server.model.entity.bd.PaginatedImages;
import enfok.server.error.InfrastructureOfflineException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import enfok.server.ports.adapter.UserRepositoryInterface;
import enfok.server.ports.port.AuthOrchestator;
import enfok.server.model.entity.dto.auth.ValidateResponse;
import enfok.server.model.entity.dto.node.NodeMetricsDTO;
import enfok.server.model.entity.dto.node.BatchProgressDTO;
import enfok.server.error.NotFoundException;

@ApplicationScoped
public class BdOrchestratorService implements BdOrchestrator {

    @Inject
    private BdRepositoryInterface bdRepository;

    @Inject
    private UserRepositoryInterface userRepository;

    @Inject
    private AuthOrchestator authOrchestator;

    @Override
    public String getNodeStatus(String token) throws InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new RuntimeException("Token de autorizaci\u00F3n nulo.");
        }
        // Simplified: if bd system validates, it's ACTIVE
        return bdRepository.validateServer() ? "ACTIVE" : "DEAD";
    }

    @Override
    public String getTransformations(String token) throws InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new RuntimeException("Token de autorizaci\u00F3n no proporcionado.");
        }
        String result = bdRepository.getTransformations(token);
        if (result == null || result.isEmpty()){
            throw new RuntimeException("No se obtuvieron transformaciones de la base de datos.");
        }
        return result;
    }

    @Override
    public ArrayList<Image> getUserImages(String token, int limit, int offset) throws InfrastructureOfflineException {
        if (token == null || token.isEmpty())  {
            throw new RuntimeException("Token faltante.");
        }
        ArrayList<Image> result = bdRepository.getUserImages(token, limit, offset);
        if (result == null){
            throw new RuntimeException("Objeto Image list es nulo.");
        }
        return result;
    }

    @Override
    public ArrayList<Batches> getUserBatches(String token, int limit, int offset) throws InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new RuntimeException("Token nulo.");
        }
        ArrayList<Batches> result = bdRepository.getUserBatches(token, limit, offset);
        if (result == null){
            throw new RuntimeException("La consulta de batches devolvi\u00F3 nulo.");
        }
        return result;
    }

    @Override
    public List<BatchWithCover> getUserBatchesWithCovers(String token) throws InfrastructureOfflineException {
        if (token == null || token.isEmpty()) throw new RuntimeException("Token faltante");
        try {
            ValidateResponse resToken = authOrchestator.validateToken(token);
            if (resToken == null || resToken.isValid() == false || resToken.getUserUuid() == null) throw new RuntimeException("Usuario no encontrado");
            return bdRepository.listUserBatchesWithCovers(resToken.getUserUuid());
        } catch (enfok.server.error.NotFoundException e) {
            throw new RuntimeException("Perfil no encontrado: " + e.getMessage());
        }
    }

    @Override
    public PaginatedImages getPaginatedImages(String token, String batchUuid, int page, int limit) throws InfrastructureOfflineException {
        if (token == null || token.isEmpty()) throw new RuntimeException("Token faltante");
        try {
            ValidateResponse resToken = authOrchestator.validateToken(token);
            if (resToken == null || resToken.isValid() == false || resToken.getUserUuid() == null) throw new RuntimeException("Usuario no encontrado");
            return bdRepository.getBatchImagesPaginated(batchUuid   , page, limit);
        } catch (enfok.server.error.NotFoundException e) {
            throw new RuntimeException("Perfil no encontrado: " + e.getMessage());
        }
    }

    @Override
    public List<NodeMetricsDTO> getImageMetrics(String token, String imageUuid) throws InfrastructureOfflineException {
        if (token == null || token.isEmpty()) throw new RuntimeException("Token faltante");
        try {
            ValidateResponse resToken = authOrchestator.validateToken(token);
            if (resToken == null || resToken.isValid() == false) throw new RuntimeException("Usuario no autorizado");
            return bdRepository.getMetricsByImage(imageUuid);
        } catch (enfok.server.error.NotFoundException e) {
            throw new RuntimeException("Error de autenticación: " + e.getMessage());
        }
    }

    @Override
    public BatchProgressDTO getBatchProgress(String token, String batchUuid) throws InfrastructureOfflineException {
        if (token == null || token.isEmpty()) throw new RuntimeException("Token faltante");
        try {
            ValidateResponse resToken = authOrchestator.validateToken(token);
            if (resToken == null || resToken.isValid() == false) throw new RuntimeException("Usuario no autorizado");
            return bdRepository.getBatchProgress(batchUuid);
        } catch (enfok.server.error.NotFoundException e) {
            throw new RuntimeException("Error de autenticación: " + e.getMessage());
        }
    }

    @Override
    public String createZip(String batchId) throws InfrastructureOfflineException {
        // Podríamos validar el token aquí si lo pasáramos, pero para este caso el orquestador confía en el parámetro o podemos añadir auth después.
        // Dado el prompt, asumimos que se llamará directamente.
        return bdRepository.createZip(batchId);
    }
}
