package enfok.server.repository;

import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.dto.user.ActivityEventDTO;
import enfok.server.model.entity.dto.user.UserStatisticsDTO;
import enfok.server.model.entity.dto.user.TransformationStatDTO;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import enfok.server.ports.adapter.UserRepositoryInterface;
import enfok.server.model.entity.dto.user.*;
import enfok.server.repository.client.UserServiceClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
    public List<ActivityEventDTO> getUserActivity(String token) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        User u = profile(token);
        if (u == null || u.getUserUuid() == null) return new ArrayList<>();

        java.util.List<UserActivity> logs = bdRepository.getUserActivity(u.getUserUuid());
        if (logs == null) return new ArrayList<>();

        return logs.stream().map(log -> {
            ActivityEventDTO dto = new ActivityEventDTO();
            dto.setEventType(log.getEventType());
            dto.setRefUuid(log.getRefUuid());
            dto.setParentUuid(log.getParentUuid());
            dto.setDescription(log.getDescription());
            dto.setOccurredAt(log.getOccurredAt() != null ? log.getOccurredAt().toString() : "");
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public UserStatisticsDTO getUserStatistics(String token) throws NotFoundException, InfrastructureOfflineException {
        validateServer();
        User u = profile(token);
        if (u == null || u.getUserUuid() == null) throw new NotFoundException("Usuario no encontrado");

        UserStatistics stats = bdRepository.getUserStatistics(u.getUserUuid());
        if (stats == null) return null;

        UserStatisticsDTO dto = new UserStatisticsDTO();
        dto.setTotalBatches(stats.getTotalBatches());
        dto.setTotalImages(stats.getTotalImages());
        dto.setImagesSuccess(stats.getImagesSuccess());
        dto.setImagesFailed(stats.getImagesFailed());
        
        if (stats.getTopTransformations() != null) {
            dto.setTopTransformations(stats.getTopTransformations().stream()
                .map(t -> new TransformationStatDTO(t.getName(), t.getCount()))
                .collect(Collectors.toList()));
        }
        
        return dto;
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
