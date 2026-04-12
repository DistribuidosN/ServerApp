package enfok.server.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
/**
 * [CAPA CONFIG]: GrpcConfig
 * Esta clase se encarga de definir cómo nos conectaremos con los otros servicios (como Python o Go).
 * Aquí típicamente configuraríamos canales, puertos, y timeouts para gRPC.
 */
@ApplicationScoped
public class GrpcConfig {
    // Quarkus leerá automáticamente estas variables del archivo .env que está en la raíz
    // Si la variable no existe en el archivo, utilizará el "defaultValue".
    @ConfigProperty(name = "NODE", defaultValue = "localhost:50051")
    private String node;

    @ConfigProperty(name = "BD_AUTH", defaultValue = "")
    private String authBd;

    @ConfigProperty(name = "BD_SYSTEM", defaultValue = "")
    private String bdSystem;

    public String getNode() {
        return node;
    }
    public String getAuthBd() {
        return authBd;
    }
    public String getBdSystem() {
        return bdSystem;
    }
}
