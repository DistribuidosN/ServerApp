package enfok.server.repository;

import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.auth.Activity;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.ports.adapter.UserRepositoryInterface;
import enfok.server.model.entity.dto.user.*;
import enfok.server.repository.client.UserServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.sql.Timestamp;
import enfok.server.ports.adapter.BdRepositoryInterface;
import enfok.server.model.entity.bd.UserActivity;
import enfok.server.model.entity.bd.UserStatistics;

@ApplicationScoped
public class UserRepository implements UserRepositoryInterface {

    @Inject
    Config config;

    @Inject
    NetworkValidator networkValidator;

    @Inject
    @RestClient
    UserServiceClient userClient;

    @Inject
    BdRepositoryInterface bdRepository;

    @Override
    public boolean validateServer() throws InfrastructureOfflineException {
        return networkValidator.validate(config.getAuthBd());
    }

    @Override
    public User profile(String token) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        try {
            UserProfileResponse resp = userClient.getProfile("Bearer " + token);
            return mapToUser(resp);
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error obteniendo perfil: " + e.getMessage());
        }
    }

    @Override
    public boolean updateProfile(String token, User data) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        try {
            UserUpdateRequest req = new UserUpdateRequest(data.getUsername());
            UserUpdateResponse resp = userClient.updateProfile("Bearer " + token, req);
            return resp != null && resp.isValid();
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error actualizando perfil: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteAccount(String token) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        try {
            try (Response response = userClient.deleteAccount("Bearer " + token)) {
                return response.getStatus() == 200;
            }
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error eliminando cuenta: " + e.getMessage());
        }
    }

    @Override
    public Activity getUserActivity(String token) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        User u = profile(token);
        if (u == null || u.getUserUuid() == null) return new Activity();

        java.util.List<UserActivity> logs = bdRepository.getUserActivity(u.getUserUuid());
        UserStatistics stats = bdRepository.getUserStatistics(u.getUserUuid());

        Activity ac = new Activity();
        ac.setTotalBatches(stats != null ? stats.getTotalBatches() : 0);
        ac.setTotalImagesProcessed(stats != null ? stats.getImagesSuccess() : 0);
        
        if (logs != null && !logs.isEmpty()) {
            UserActivity last = logs.get(0);
            ac.setLastActivity(last.getEventType() + " en " + last.getRefUuid());
        } else {
            ac.setLastActivity("Sin actividad reciente");
        }
        
        return ac;
    }

    @Override
    public String getUserStatistics(String token) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        User u = profile(token);
        if (u == null || u.getUserUuid() == null) return "Usuario no encontrado";

        UserStatistics stats = bdRepository.getUserStatistics(u.getUserUuid());
        if (stats == null) return "Estadísticas no encontradas";

        return String.format("Lotes: %d | Total Imágenes: %d | Éxito: %d | Fallo: %d",
                stats.getTotalBatches(), stats.getTotalImages(), stats.getImagesSuccess(), stats.getImagesFailed());
    }

    @Override
    public User searchUser(String token, String uid) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        try {
            // Suponemos que uid es el username para la búsqueda en este contexto
            UserProfileResponse resp = userClient.searchUser("Bearer " + token, uid);
            return mapToUser(resp);
        } catch (Exception e) {
            throw new InfrastructureOfflineException("Error buscando usuario: " + e.getMessage());
        }
    }

    private User mapToUser(UserProfileResponse resp) {
        if (resp == null) return null;
        User user = new User();
        user.setId(resp.getId());
        user.setUserUuid(resp.getUserUuid());
        user.setUsername(resp.getUsername());
        user.setEmail(resp.getEmail());
        user.setRoleId(resp.getRoleId());
        user.setStatus(resp.getStatus());
        if (resp.getCreatedAt() != null) {
            try {
                // Formatting date string to SQL timestamp
                user.setCreatedAt(Timestamp.valueOf(resp.getCreatedAt().replace("T", " ").substring(0, 19)));
            } catch (Exception e) {
                // Handling date parsing if needed
            }
        }
        return user;
    }
}
