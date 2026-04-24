package enfok.server.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import enfok.server.utility.NetworkValidator;

@ApplicationScoped
public class RuntimeValidator {

    @Inject
    Config config;

    @Inject
    NetworkValidator networkValidator;

    /**
     * Se ejecuta automáticamente cuando Quarkus se levanta.
     */
    void onStart(@Observes StartupEvent ev) {
        System.out.println("==========================================");
        System.out.println("Iniciando validación de dependencias de red...");
        
        checkService("Auth Database API", config.getAuthBd());
        checkService("Core BD System API", config.getBdSystem());

        
        System.out.println("==========================================");
    }

    private void checkService(String serviceName, String ipUrl) {
        try {
            if (ipUrl == null || ipUrl.isEmpty()) {
                System.out.println("[ADVERTENCIA] " + serviceName + ": No configurado o vacío.");
                return;
            }
            // Agregamos ping/health según corresponda o simplemente la base
            networkValidator.validate(ipUrl);
            System.out.println("[OK] " + serviceName + " conectado correctamente en: " + ipUrl);
        } catch (Exception e) {
            System.err.println("[FALLO] " + serviceName + " NO RESPONDE en " + ipUrl + " - Detalle: " + e.getMessage());
        }
    }
}
