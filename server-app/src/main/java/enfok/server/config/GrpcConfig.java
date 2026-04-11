package enfok.server.config;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * [CAPA CONFIG]: GrpcConfig
 * Esta clase se encarga de definir cómo nos conectaremos con los otros servicios (como Python o Go).
 * Aquí típicamente configuraríamos canales, puertos, y timeouts para gRPC.
 */
@ApplicationScoped
public class GrpcConfig {
    // URL simulada del servidor Python donde derivamos tareas pesadas.
    private String pythonNodeUrl = "localhost:50051";

    public String getPythonNodeUrl() {
        return pythonNodeUrl;
    }
}
