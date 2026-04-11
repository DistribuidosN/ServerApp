package enfok.server.config;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * [CAPA CONFIG]: SecurityConfig
 * En el mundo de SOAP, la seguridad suele manejarse a través de "WS-Security" o Interceptores en Apache CXF.
 * Esta clase puede usarse para registrar interceptores que validen un Token JWT o credenciales
 * antes de dejar pasar la petición al Endpoint.
 */
@ApplicationScoped
public class SecurityConfig {
    public void setupSecurity() {
        System.out.println(">>> [Configuración] Validando reglas de seguridad para los Web Services...");
    }
}
