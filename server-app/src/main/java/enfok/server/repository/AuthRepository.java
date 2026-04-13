package enfok.server.repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.ports.adapter.AuthRepositoryInterface;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthRepository implements AuthRepositoryInterface {

    @Inject
    Config config;

    @Inject
    NetworkValidator networkValidator;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private String getBaseUrl() {
        String url = config.getAuthBd();
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    @Override
    public boolean validateServer() throws InfrastructureOfflineException {
        return networkValidator.validate(config.getAuthBd());
    }

    @Override
    public String logIn(String email, String pwd) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) return "mocked-auth-token-12345";
        
        validateServer();
        System.out.println(">>> [AuthRepository] Ejecutando POST a /login");

        String jsonPayload = String.format("{\"email\":\"%s\", \"password\":\"%s\"}", email, pwd);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getBaseUrl() + "/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                if (body.contains("token")) {
                    return body.split("\"token\":\"")[1].split("\"")[0];
                }
                return "token-from-external-auth";
            } else if (response.statusCode() == 404 || response.statusCode() == 401) {
                throw new NotFoundException("Credenciales inv\u00E1lidas o usuario no encontrado");
            } else {
                throw new InfrastructureOfflineException("Auth Service devolvi\u00F3 error HTTP " + response.statusCode());
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error de red contactando al Auth Service: " + e.getMessage());
        }
    }

    @Override
    public boolean signUp(String email, String pwd, String name, String lastName)
            throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) return true;

        validateServer();
        System.out.println(">>> [AuthRepository] Ejecutando POST a /register");

        String jsonPayload = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\", \"name\":\"%s\", \"lastName\":\"%s\"}",
                email, pwd, name, lastName);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getBaseUrl() + "/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                return true;
            } else if (response.statusCode() == 409 || response.statusCode() == 404) {
                throw new NotFoundException("El usuario ya existe (409) o validaci\u00F3n fallida (404)");
            } else {
                throw new InfrastructureOfflineException("Auth Service devolvi\u00F3 error HTTP " + response.statusCode());
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error de red contactando al Auth Service: " + e.getMessage());
        }
    }

    @Override
    public boolean logOut(String token) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) return true;

        validateServer();
        System.out.println(">>> [AuthRepository] Ejecutando POST a /logout con Token");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getBaseUrl() + "/logout"))
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            } else if (response.statusCode() == 404 || response.statusCode() == 401) {
                throw new NotFoundException("Token inv\u00E1lido o expirado");
            } else {
                throw new InfrastructureOfflineException("Auth Service devolvi\u00F3 error HTTP " + response.statusCode());
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error de red contactando al Auth Service: " + e.getMessage());
        }
    }

    @Override
    public boolean forgotPassword(String email, String newPassword)
            throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) return true;

        validateServer();
        System.out.println(">>> [AuthRepository] Ejecutando PUT a /forgot-password");

        String jsonPayload = String.format("{\"email\":\"%s\", \"newPassword\":\"%s\"}", email, newPassword);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getBaseUrl() + "/forgot-password"))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            } else if (response.statusCode() == 404) {
                throw new NotFoundException("Usuario no encontrado (404)");
            } else {
                throw new InfrastructureOfflineException("Auth Service devolvi\u00F3 error HTTP " + response.statusCode());
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error de red contactando al Auth Service: " + e.getMessage());
        }
    }

    @Override
    public boolean validateToken(String token) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) return true;

        validateServer();
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(getBaseUrl() + "/validate-token"))
                    .header("Authorization", "Bearer " + token)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error validando token en la red: " + e.getMessage());
        }
    }
}
