package enfok.server.utility;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.time.Duration;
import enfok.server.model.entity.NodeState;
import enfok.server.ports.adapter.BdRepositoryInterface;

/**
 * Node Watchdog Service: Monitorea la salud de los nodos en tiempo real.
 * Si un nodo no ha enviado un heartbeat en más de 20 segundos, se marca 
 * como inactivo tanto en memoria como en la base de datos.
 */
@ApplicationScoped
public class NodeWatchdogService {

    private static final Logger LOG = Logger.getLogger(NodeWatchdogService.class);
    private static final long INACTIVITY_THRESHOLD_SECONDS = 20;

    @Inject
    NodeMemoryRegistry memoryRegistry;

    @Inject
    BdRepositoryInterface bdRepository;

    /**
     * Tarea programada que se ejecuta cada minuto para limpiar nodos caídos.
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional
    public void cleanupInactiveNodes() {
        LocalDateTime now = LocalDateTime.now();
        
        LOG.debug("Iniciando ciclo de limpieza de nodos inactivos...");

        // Iteramos y removemos en una sola operación thread-safe usando removeIf
        memoryRegistry.getActiveNodes().entrySet().removeIf(entry -> {
            String nodeId = entry.getKey();
            NodeState state = entry.getValue();
            
            long secondsSinceLastSeen = Duration.between(state.getLastSeen(), now).getSeconds();

            if (secondsSinceLastSeen > INACTIVITY_THRESHOLD_SECONDS) {
                LOG.warnf("Nodo [%s] detectado como inactivo (%d s sin señales). Eliminando de memoria y actualizando base de datos.", 
                        nodeId, secondsSinceLastSeen);
                
                try {
                    // Actualizamos el estado en la base de datos de forma transaccional
                    bdRepository.updateNodeStatus(nodeId, "INACTIVE");
                } catch (Exception e) {
                    LOG.errorf("Error al actualizar estado INACTIVE para el nodo [%s]: %s", nodeId, e.getMessage());
                }
                
                return true; // Remueve del ConcurrentHashMap
            }
            return false;
        });
    }
}
