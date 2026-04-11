package enfok.server.error;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;

/**
 * [CAPA ERRORES]: SoapFaultInterceptor
 * Apache CXF utiliza un sistema de "Interceptores" para manipular el flujo HTTP en cualquier fase.
 * Este interceptor está configurado para la "Fase de Error" (PRE_STREAM).
 * Sirve para atrapar cualquier error genérico no controlado (ej. caída de base de datos) 
 * y limpiar el mensaje o hacer un log especial antes de enviarlo al cliente.
 */
public class SoapFaultInterceptor extends AbstractSoapInterceptor {

    public SoapFaultInterceptor() {
        // Se ejecuta justo al preparar el envío del error de vuelta.
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        Fault fault = (Fault) message.getContent(Exception.class);
        if (fault != null) {
            System.err.println(">>> [Interceptor CXF] Atrapando error crudo y empaquetándolo: " + fault.getMessage());
            // Modifica el fault o extrae métricas de caídas
        }
    }
}
