package enfok.server.ports.port;

import enfok.server.model.entity.auth.User;
import enfok.server.model.entity.auth.Activity;
import enfok.server.error.NotFoundException;
import enfok.server.error.InfrastructureOfflineException;

public interface UserOrchestrator {
    public User profile(String token) throws NotFoundException, InfrastructureOfflineException;
    public boolean updateProfile(String token, User data) throws NotFoundException, InfrastructureOfflineException;
    public boolean deleteAccount(String token) throws NotFoundException, InfrastructureOfflineException;
    public Activity getUserActivity(String token) throws NotFoundException, InfrastructureOfflineException;
    public String getUserStatistics(String token) throws NotFoundException, InfrastructureOfflineException;
    public User searchUser(String token, String uid) throws NotFoundException, InfrastructureOfflineException;
}
