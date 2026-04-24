package enfok.server.config;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class Config {
    // Variable especial para el MOCK_SERVICES = true por defecto
    @ConfigProperty(name = "MOCK_SERVICES", defaultValue = "true")
    private boolean mockServices;

    @ConfigProperty(name = "BD_AUTH", defaultValue = "")
    private String authBd;

    @ConfigProperty(name = "BD_SYSTEM", defaultValue = "")
    private String bdSystem;

    public boolean isMockServices() {
        return mockServices;
    }
    public String getAuthBd() {
        return authBd;
    }
    public String getBdSystem() {
        return bdSystem;
    }

}
