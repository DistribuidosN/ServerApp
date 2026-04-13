package enfok.server.utility;

import enfok.server.error.InfrastructureOfflineException;
import jakarta.enterprise.context.ApplicationScoped;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import enfok.server.config.Config;
import jakarta.inject.Inject;

@ApplicationScoped
public class NetworkValidator {

    @Inject
    Config config;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    /**
     * Valida si un IP o URL está en línea mediante una petición HTTP GET básica.
     * @param ip URL o IP destino para comprobar.
     * @return true si responde, lanza excepción si no.
     */
    public boolean validate(String ip) throws InfrastructureOfflineException {
        if (config.isMockServices()) {
            return true; // Bypass local mockeado
        }
        
        if (ip == null || ip.trim().isEmpty()) {
            throw new InfrastructureOfflineException("La configuración de red (IP/URL) está vacía o es nula.");
        }
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ip))
                    .timeout(Duration.ofSeconds(2))
                    .GET()
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
            return true;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Servidor en " + ip + " no responde o está fuera de línea.");
        }
    }
}
