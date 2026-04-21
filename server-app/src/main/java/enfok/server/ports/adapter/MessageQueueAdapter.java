package enfok.server.ports.adapter;

import enfok.server.model.entity.dto.node.ImageItemBatch;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

/**
 * Implementación básica del productor de mensajes para la cola de prioridad.
 * Por ahora actúa como un Mock que registra la actividad en consola.
 * TODO: Integrar con el sistema de mensajería real (RabbitMQ, Kafka, etc.)
 */
@ApplicationScoped
public class MessageQueueAdapter implements MessageQueueProducer {

    @Override
    public void sendToPriorityQueue(String batchId, List<String> filters, ImageItemBatch image) {
        System.out.println(">>> [MSG QUEUE] Encolando imagen: " + image.getName() 
                + " (ID: " + image.getId() + ") para el lote: " + batchId);
        
        // Aquí iría la lógica de serialización y envío al broker.
        // Por ahora, simulamos que la tarea se envía exitosamente.
    }
}
