package enfok.server.endpoint;

import enfok.server.model.soap.BatchService;
import enfok.server.model.soap.BatchRequestDto;
import enfok.server.model.soap.BatchResponseDto;

import enfok.server.port.BatchOrchestratorService; // <-- Importamos del Puerto
import jakarta.inject.Inject;
import jakarta.jws.WebService;

/**
 * [CAPA ENDPOINT]: Capa de Exposición.
 * Es la puerta de entrada a nuestra API SOAP.
 * La anotación @WebService enlaza esta implementación técnica con el contrato
 * formal (BatchService).
 * 
 * REGLA DE ORO: Los Endpoints JAMÁS deben contener lógica de negocio ni
 * comunicarse con Bases de Datos.
 * Solo reciben la petición y delegan al Orquestador (Service).
 */
@WebService(endpointInterface = "enfok.server.model.soap.BatchService")
public class BatchEndpoint implements BatchService {

    @Inject // Inyección de dependencias (Quarkus/CDI): Instancia el servicio de forma
            // automática
    BatchOrchestratorService orchestratorService;

    @Override
    public BatchResponseDto processBatch(BatchRequestDto request) {
        System.out.println(">>> 1. Endpoint SOAP Interceptó la llamada con ID: " + request.getBatchId());

        // Delegamos TODA la responsabilidad al Orquestador
        return orchestratorService.handleBatch(request);
    }
}
