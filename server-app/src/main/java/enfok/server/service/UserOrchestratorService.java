package enfok.server.service;

import enfok.server.ports.port.UserOrchestrator;
import enfok.server.ports.adapter.UserRepositoryInterface;
import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.dto.user.ActivityEventDTO;
import enfok.server.model.entity.dto.user.UserStatisticsDTO;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import java.util.List;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserOrchestratorService implements UserOrchestrator {

    @Inject
    private UserRepositoryInterface userRepository;

    @Override
    public User profile(String token) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
                throw new NotFoundException("Token de autorizaci\u00F3n no proporcionado.");
        }
        User result = userRepository.profile(token);
        if (result == null) {
            throw new NotFoundException("No se encontr\u00F3 el perfil del usuario activo.");
        }
        return result;
    }

    @Override
    public boolean updateProfile(String token, User data) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("Token de autorizaci\u00F3n no proporcionado.");
        }
        if (data == null){
            throw new NotFoundException("Payload de usuario vac\u00EDo.");
        }
        
        boolean result = userRepository.updateProfile(token, data);
        if (!result){
            throw new NotFoundException("No se pudo actualizar el perfil.");
        }
        return result;
    }

    @Override
    public boolean deleteAccount(String token) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("Token de autorizaci\u00F3n no proporcionado.");
        }
        boolean result = userRepository.deleteAccount(token);
        if (!result){
            throw new NotFoundException("No se pudo eliminar la cuenta.");
        }
        return result;
    }

    @Override
    public List<ActivityEventDTO> getUserActivity(String token) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("Token de autorizaci\u00F3n no proporcionado.");
        }
        List<ActivityEventDTO> result = userRepository.getUserActivity(token);
        if (result == null || result.isEmpty()){
            throw new NotFoundException("No se encontraron registros de actividad.");
        }
        return result;
    }

    @Override
    public UserStatisticsDTO getUserStatistics(String token) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("Token de autorizaci\u00F3n no proporcionado.");
        }
        UserStatisticsDTO result = userRepository.getUserStatistics(token);
        if (result == null){
            throw new NotFoundException("No hay estad\u00EDsticas disponibles.");
        }
        return result;
    }

    @Override
    public User searchUser(String token, String uid) throws NotFoundException, InfrastructureOfflineException {
        if (token == null || token.isEmpty()){
            throw new NotFoundException("Token de autorizaci\u00F3n no proporcionado.");
        }
        if (uid == null || uid.isEmpty()){
            throw new NotFoundException("Se requiere el UID del usuario a buscar.");
        }
        
        User result = userRepository.searchUser(token, uid);
        if (result == null){
            throw new NotFoundException("El usuario pesquisado no existe.");
        }
        return result;
    }
}
