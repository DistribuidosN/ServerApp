package enfok.server.ports.adapter;

import java.util.List;
import enfok.server.model.entity.dto.node.ImageItemBatch;

/**
 * Puerto de Salida para la cola de mensajes (Protocolo Agnóstico).
 */
public interface MessageQueueProducer {
    /**
     * Envía una tarea de procesamiento de imagen a la cola de prioridad.
     * @param batchId ID del lote completo.
     * @param filters Lista de filtros/transformaciones a aplicar.
     * @param image Objeto con la información de la imagen y su base64.
     */
    void sendToPriorityQueue(String batchId, List<String> filters, ImageItemBatch image);
}
