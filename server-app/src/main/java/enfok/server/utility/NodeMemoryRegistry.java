package enfok.server.utility;

import enfok.server.model.entity.NodeState;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class NodeMemoryRegistry {
    
    private final ConcurrentHashMap<String, NodeState> activeNodes = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, NodeState> getActiveNodes() {
        return activeNodes;
    }

    public String registerOrUpdate(String nodeId, String ip, int port) {
        final String[] status = new String[1];
        activeNodes.compute(nodeId, (k, v) -> {
            if (v == null) {
                status[0] = "REGISTERED";
                return new NodeState(nodeId, ip, port);
            }
            status[0] = "UPDATED";
            v.update(ip, port);
            return v;
        });
        return status[0];
    }

    public void updateHeartbeat(String nodeId) {
        NodeState state = activeNodes.get(nodeId);
        if (state != null) {
            state.updateHeartbeat();
        }
    }

    public void removeNode(String nodeId) {
        activeNodes.remove(nodeId);
    }
}
