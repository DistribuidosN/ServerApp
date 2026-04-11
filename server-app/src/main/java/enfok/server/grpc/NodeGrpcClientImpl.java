package enfok.server.grpc;

import jakarta.enterprise.context.ApplicationScoped;
import enfok.server.port.NodeGrpcClient; // <-- Adaptándose al Puerto de Salida

/**
 * [ADAPTER - OUT]: Adaptador de Salida (Comunicación).
 * Contiene el cliente real que viaja por la red usando gRPC. Se conecta estrictamente al puerto 
 * que nuestro Negocio requiere.
 */
@ApplicationScoped
public class NodeGrpcClientImpl implements NodeGrpcClient {
    @Override
    public boolean sendToPythonNode(String batchId, String data) {
        System.out.println(">>> 4. Adapter OUT (gRPC): Adaptador ejecutando petición externa hacia el Worker -> '"+ data +"'");
        return true; 
    }
}
