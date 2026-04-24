package enfok.server.ports.adapter;

import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.dto.user.ActivityEventDTO;
import enfok.server.model.entity.dto.user.UserStatisticsDTO;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;
import java.util.List;

public interface UserRepositoryInterface {
    public boolean validateServer() throws InfrastructureOfflineException;
    public User profile(String token) throws NotFoundException, InfrastructureOfflineException;
    public boolean updateProfile(String token, User data) throws NotFoundException, InfrastructureOfflineException;
    public boolean deleteAccount(String token) throws NotFoundException, InfrastructureOfflineException;
    public List<ActivityEventDTO> getUserActivity(String token) throws NotFoundException, InfrastructureOfflineException;
    public UserStatisticsDTO getUserStatistics(String token) throws NotFoundException, InfrastructureOfflineException;
    public User searchUser(String token, String uid) throws NotFoundException, InfrastructureOfflineException;
}
