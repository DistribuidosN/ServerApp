package enfok.server.model.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;

/**
 * [CAPA MODEL / CONTRATOS]: BatchService
 * Esta es la Interfaz oficial que define cómo se comunican las máquinas.
 * Esto es lo que Apache CXF lee para generar el "WSDL" púbico.
 */
@WebService
public interface BatchService {
    @WebMethod
    BatchResponseDto processBatch(@WebParam(name = "request") BatchRequestDto request);
}
