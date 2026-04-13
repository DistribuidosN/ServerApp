package enfok.server.error;

import jakarta.xml.ws.WebFault;

@WebFault(name = "InvalidTokenFault", targetNamespace = "http://error.server.enfok/")
public class InvalidTokenException extends Exception {
    private final String codigoError = "ERR_AUTH_401";

    public InvalidTokenException(String mensaje) {
        super(mensaje);
    }
    public String getCodigoError() { return codigoError; }
}