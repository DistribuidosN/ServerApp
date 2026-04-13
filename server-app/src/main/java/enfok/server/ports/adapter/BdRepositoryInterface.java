package enfok.server.ports.adapter;

import java.util.ArrayList;
import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;
import enfok.server.error.InfrastructureOfflineException;

public interface BdRepositoryInterface {
    public boolean validateServer() throws InfrastructureOfflineException;
    public String getNodeStatus(String token) throws InfrastructureOfflineException;
    public String getTransformations(String token) throws InfrastructureOfflineException;
    public ArrayList<Image> getUserImages(String token, int limit, int offset) throws InfrastructureOfflineException;
    public ArrayList<Batches> getUserBatches(String token, int limit, int offset) throws InfrastructureOfflineException;
}
