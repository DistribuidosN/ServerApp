package enfok.server.repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.auth.Activity;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.ports.adapter.UserRepositoryInterface;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserRepository implements UserRepositoryInterface {

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
        return networkValidator.validate(getBaseUrl());
    }

    @Override
    public User profile(String token) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) {
            User mockUser = new User();
            mockUser.setUsername("Mock User Local");
            mockUser.setStatus(1);
            return mockUser;
        }

        validateServer();
        // Implementaci\u00F3n HTTP pendiente
        return new User();
    }

    @Override
    public boolean updateProfile(String token, User data) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) return true;

        validateServer();
        // Implementaci\u00F3n HTTP pendiente
        return true;
    }

    @Override
    public boolean deleteAccount(String token) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) return true;

        validateServer();
        // Implementaci\u00F3n HTTP pendiente
        return true;
    }

    @Override
    public Activity getUserActivity(String token) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) {
            Activity ac = new Activity();
            ac.setTotalBatches(15);
            ac.setTotalImagesProcessed(105);
            return ac;
        }

        validateServer();
        // Implementaci\u00F3n HTTP pendiente
        return new Activity();
    }

    @Override
    public String getUserStatistics(String token) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) return "Estad\u00EDsticas Mockeadas: 10 horas procesando.";

        validateServer();
        // Implementaci\u00F3n HTTP pendiente
        return "Stad\u00EDsticas de usuario completas";
    }

    @Override
    public User searchUser(String token, String uid) throws NotFoundException, InfrastructureOfflineException {
        if (config.isMockServices()) {
            User mockUser = new User();
            mockUser.setUserUuid(uid);
            mockUser.setUsername("Pesquisado Mock");
            return mockUser;
        }

        validateServer();
        // Implementaci\u00F3n HTTP pendiente
        return new User();
    }
}
