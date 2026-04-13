package enfok.server.error;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import jakarta.enterprise.context.ApplicationScoped;

import enfok.server.error.InvalidTokenException;
import enfok.server.error.NotFoundException;

/**
 * [CAPA ERRORES]: SoapFaultInterceptor
 * Atrapa cualquier error genérico no controlado (ej. caída de base de datos)
 * y limpia el mensaje antes de enviarlo al cliente.
 */
@ApplicationScoped
public class SoapFaultInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

    public SoapFaultInterceptor() {
        // Nos enganchamos justo antes de que el XML salga hacia el cliente
        super(Phase.PRE_STREAM);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        Fault fault = (Fault) message.getContent(Exception.class);

        if (fault != null) {
            Throwable causaReal = fault.getCause();

            if (causaReal instanceof InvalidTokenException ||
                    causaReal instanceof NotFoundException) {
                // Modificamos el HTTP Status Code de la respuesta SOAP
                if (causaReal instanceof InvalidTokenException) {
                    message.put(org.apache.cxf.message.Message.RESPONSE_CODE, 401); // 401 Unauthorized
                } else {
                    message.put(org.apache.cxf.message.Message.RESPONSE_CODE, 404); // 404 Not Found
                }
                
                // IMPORTANTE: JAXB no sabe cómo serializar a XML tus excepciones personalizadas 
                // si no están declaradas explicitamente en los @WebMethod. Para evitar el "Marshalling Error",
                // envolvemos tu mensaje en una Exception genérica inofensiva y reemplazamos el error original.
                Fault safeFault = new Fault(new Exception(causaReal.getMessage()));
                safeFault.setFaultCode(fault.getFaultCode());
                message.setContent(Exception.class, safeFault);

                return;
            }

            System.err.println("[ALERTA CRÍTICA DEL SISTEMA] Falla interna detectada:");
            if (causaReal != null) {
                causaReal.printStackTrace();
            }

            fault.setMessage(
                    "ERR_SYS_500: Servicio temporalmente fuera de linea o error interno. Contacte al administrador.");
        }
    }
}