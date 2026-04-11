package enfok.server.error;

import jakarta.xml.ws.WebFault;

/**
 * [CAPA ERRORES]: BatchNotFoundException
 * Es una excepción personalizada de nuestra lógica de negocio.
 * La anotación @WebFault le indica a Apache CXF que si ocurre este error, 
 * debe empaquetarlo en formato XML como un "SOAP Fault" estándar 
 * para avisarle de forma nativa al cliente (ej. programa Go/Python) del error.
 */
@WebFault(name = "BatchNotFound", targetNamespace = "http://soap.model.server.enfok/")
public class BatchNotFoundException extends Exception {
    public BatchNotFoundException(String message) {
        super(message);
    }
}
