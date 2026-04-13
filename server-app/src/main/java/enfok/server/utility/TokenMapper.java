package enfok.server.utility;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class TokenMapper {
    
    /**
     * Extrae el Token de los headers HTTP del WebServiceContext
     */
    public String extractToken(WebServiceContext context) {
        if (context == null || context.getMessageContext() == null) {
            return null;
        }
        
        MessageContext mc = context.getMessageContext();
        Map<String, List<String>> headers = (Map<String, List<String>>) mc.get(MessageContext.HTTP_REQUEST_HEADERS);
        
        if (headers != null && headers.containsKey("Authorization")) {
            String authHeader = headers.get("Authorization").get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
            return authHeader;
        }
        return null;
    }
}
