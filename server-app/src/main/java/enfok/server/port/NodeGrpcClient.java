package enfok.server.port;

/**
 * [PORT - OUT]: Puerto de Salida de Red
 * Abstracción de comunicación. Permite que el servicio envíe mensajes a otra máquina (ej. Python)
 * sin acoplarse físicamente a si es gRPC, HTTP puramente o RabbitMQ.
 */
public interface NodeGrpcClient {
    boolean sendToPythonNode(String batchId, String data);
}
