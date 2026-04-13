package enfok.server.utility;

import java.util.List;
import enfok.server.model.entity.bd.Node;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Balanceador de carga para distribuci\u00F3n de tareas pesadas (Por ejemplo, conversiones masivas de fotos).
 * Utiliza un algoritmo simulado estilo Round-Robin o ponderado, eligiendo un nodo din\u00E1micamente
 * de la lista total de Nodos activos registrados en la base de datos.
 */
@ApplicationScoped
public class NodeLoadBalancer {
    
    // Un contador simple para el algoritmo Round-Robin simulado
    private int currentIndex = 0;

    /**
     * Analiza y decide a qué nodo enviar una carga de trabajo específica.
     * En este caso usamos un algoritmo base Round-Robin (Rotativo).
     * @param availableNodes Lista de nodos que están actualmente MOCK u Online.
     * @return El Nodo seleccionado para procesar, o null si no hay nodos.
     */
    public synchronized Node selectNodeToProcess(List<Node> availableNodes) {
        if (availableNodes == null || availableNodes.isEmpty()) {
            System.err.println("LoadBalancer: No hay nodos disponibles para recibir carga.");
            return null;
        }

        // Selección Round-Robin
        Node selected = availableNodes.get(currentIndex % availableNodes.size());
        
        System.out.println("LoadBalancer: Asignando carga al Nodo ID=" + selected.getNodeId() + " [" + selected.getHost() + ":" + selected.getPort() + "]");
        
        currentIndex++;
        return selected;
    }
}
