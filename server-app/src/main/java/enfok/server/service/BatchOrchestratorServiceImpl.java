package enfok.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import enfok.server.model.soap.BatchRequestDto;
import enfok.server.model.soap.BatchResponseDto;
import enfok.server.model.entity.BatchRecord;
import enfok.server.error.BatchNotFoundException;

// IMPORTANTE: Aquí ENLAZAMOS el núcleo de negocio con las interfaces de nuestra arquitectura (Puertos)
import enfok.server.port.BatchOrchestratorService;
import enfok.server.port.BatchRepository;
import enfok.server.port.NodeGrpcClient;
import enfok.server.port.BatchMapper;

/**
 * [CORE IMPL]: El Corazón del Negocio.
 * Esta clase implementa el "Puerto de Entrada", resolviendo por fin la lógica.
 * Y para lograrlo, inyecta los "Puertos de Salida", manteniendo el absoluto control
 * sin acoplarse a tecnologías raras.
 */
@ApplicationScoped
public class BatchOrchestratorServiceImpl implements BatchOrchestratorService {
    
    // Al usar las Interfaces del paquete "port", logramos Desacoplamiento Total.
    @Inject BatchRepository repository;   
    @Inject NodeGrpcClient grpcClient;    
    @Inject BatchMapper mapper;           

    @Override
    public BatchResponseDto handleBatch(BatchRequestDto request) {
        BatchResponseDto response = new BatchResponseDto();
        try {
            if (request.getBatchId() == null || request.getBatchId().isEmpty()) {
                throw new BatchNotFoundException("El Batch ID provisto no puede estar vacío");
            }

            // Delegamos en las interfaces. La clase delegada en memoria es inyectada por Quarkus.
            BatchRecord record = mapper.toEntity(request);
            repository.persist(record);
            boolean success = grpcClient.sendToPythonNode(record.id, record.data);
            
            response.setStatus(success ? "SUCCESS" : "ERROR");
            response.setMessage("Procesado vía Arquitectura de Puertos y Adaptadores.");
            System.out.println(">>> 5. Core: Orquestación terminada. Toda la lógica respetó las capas.");
            
        } catch(BatchNotFoundException bnfe) {
            response.setStatus("FAILED");
            response.setMessage(bnfe.getMessage());
        } catch(Exception e) {
            response.setStatus("CRITICAL_ERROR");
            response.setMessage("Falla interna: " + e.getMessage());
        }
        return response;
    }
}
