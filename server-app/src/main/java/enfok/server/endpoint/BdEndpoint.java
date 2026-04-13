package enfok.server.endpoint;

import java.util.ArrayList;

import jakarta.jws.WebService;

import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.soap.bd.apiSoapBD;

import enfok.server.error.InfrastructureOfflineException;
import enfok.server.ports.port.BdOrchestrator;
import enfok.server.utility.TokenMapper;
import jakarta.annotation.Resource;
import jakarta.inject.Inject;
import jakarta.xml.ws.WebServiceContext;

/**
 * Endpoint de Consulta de Base de Datos (SOAP Service).
 * Brinda servicios de lecturas pasivas (queries), historial de imágenes consumidas, 
 * y lectura de transformaciones permitidas en el sistema central.
 */
@WebService(endpointInterface = "enfok.server.model.soap.bd.apiSoapBD", serviceName = "BdService")
public class BdEndpoint implements apiSoapBD {

    @Inject
    BdOrchestrator bdOrchestrator;

    @Inject
    TokenMapper tokenMapper;

    @Resource
    WebServiceContext context;

    /**
     * Interroga si el nodo central asociado a esta petición sigue en línea reportándose a BD.
     * @return ACTIVE o DEAD.
     */
    @Override
    public String getNodeStatus() {
        try {
            return bdOrchestrator.getNodeStatus(tokenMapper.extractToken(context));
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor esta fuera de linea.", e);
        }
    }

    @Override
    public String getTransformations() {
        try {
            return bdOrchestrator.getTransformations(tokenMapper.extractToken(context));
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor esta fuera de linea.", e);
        }
    }

    /**
     * Recupera una página del historial de todas las imágenes del usuario firmante.
     * @param limit Tamaño del bloque resultante (Cantidad de fotos).
     * @param offset Salto en número típico de paginación (Offset BD).
     * @return Un array con objetos Image.
     */
    @Override
    public ArrayList<Image> getUserImages(int limit, int offset) {
        try {
            return bdOrchestrator.getUserImages(tokenMapper.extractToken(context), limit, offset);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor est\u00E1 fuera de l\u00EDnea.", e);
        }
    }

    @Override
    public ArrayList<Batches> getUserBatches(int limit, int offset) {
        try {
            return bdOrchestrator.getUserBatches(tokenMapper.extractToken(context), limit, offset);
        } catch (InfrastructureOfflineException e) {
            throw new RuntimeException("El servidor est\u00E1 fuera de l\u00EDnea.", e);
        }
    }
}
