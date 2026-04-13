package enfok.server.service;

import enfok.server.ports.port.BdOrchestrator;
import enfok.server.ports.adapter.BdRepositoryInterface;
import java.util.ArrayList;
import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;
import enfok.server.error.InfrastructureOfflineException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BdOrchestratorService implements BdOrchestrator {

    @Inject
    private BdRepositoryInterface bdRepository;

    @Override
    public String getNodeStatus(String token) throws InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new RuntimeException("Token de autorizaci\u00F3n nulo.");
        }
        String result = bdRepository.getNodeStatus(token);
        if (result == null || result.isEmpty()){
            throw new RuntimeException("El nodo devolvi\u00F3 un estado incorrecto o nulo.");
        }
        return result;
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
}
