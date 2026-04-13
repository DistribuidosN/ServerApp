package enfok.server.error;

import jakarta.xml.ws.WebFault;

@WebFault(name = "InfrastructureOfflineFault", targetNamespace = "http://error.server.enfok/")
public class InfrastructureOfflineException extends Exception {
    private final String codigoError = "ERR_INFRA_500";

    public InfrastructureOfflineException(String componente) {
        super("El componente critico no responde: " + componente);
    }
    public String getCodigoError() { return codigoError; }
}