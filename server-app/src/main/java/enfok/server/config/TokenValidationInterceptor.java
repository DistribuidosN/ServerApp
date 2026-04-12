package enfok.server.config;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.helpers.CastUtils;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.List;
import java.util.Map;

/**
 * [GUARDIÁN DE SEGURIDAD]: Interceptor de SOAP
 * 
 * IDEA DE SISTEMAS DISTRIBUIDOS: Antes de que el mensaje siquiera llegue a tu "BatchEndpoint",
 * este guardián ataja la petición, extrae las credenciales, y se comunica con 
 * tu "Servidor de Autenticación" para validar si el usuario tiene permiso.
 */
@ApplicationScoped
public class TokenValidationInterceptor extends AbstractSoapInterceptor {

    // Extrae la IP de tu Servidor de Autenticación desde tu archivo .env
    @ConfigProperty(name = "BD_AUTH", defaultValue = "http://auth-server:9000") 
    String authServerUrl;

    public TokenValidationInterceptor() {
        // Phase.PRE_PROTOCOL: Actúa muy temprano, evaluando las cabeceras HTTP crudas.
        super(Phase.PRE_PROTOCOL);
    }

    @Override
    public void handleMessage(SoapMessage message) throws Fault {
        System.out.println(">>> [Seguridad] 🛡️ Atajando Petición SOAP antes del Endpoint...");

        // 1. Extraer Headers estándar (ej. Mando el token en Authorization)
        Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) message.get(SoapMessage.PROTOCOL_HEADERS));

        if (headers == null || !headers.containsKey("Authorization")) {
            System.err.println(">>> [Seguridad] Bloqueado: No se detectó Token.");
            throw new Fault(new Exception("ACCESO DENEGADO: Falta el Header de Autorización."));
        }

        String clientToken = headers.get("Authorization").get(0);

        // 2. REDIRECCIÓN/CONSULTA AL SERVER DE AUTH
        boolean isTokenValid = callExternalAuthServer(clientToken);

        if (!isTokenValid) {
            System.err.println(">>> [Seguridad] Bloqueado: Servidor Auth rechazó el Token.");
            throw new Fault(new Exception("ACCESO DENEGADO: Token expirado o inválido."));
        }
        
        System.out.println(">>> [Seguridad] ✅ Permiso Concedido por el Auth Server. Pasando al Núcleo...");
    }

    /**
     * Simulación de llamada a tu Servidor de Autenticación Externo
     */
    private boolean callExternalAuthServer(String token) {
        System.out.println(">>> [Seguridad] Redirigiendo Token a Servidor Central: " + authServerUrl);
        // Aquí harías un HTTP Client (ej. restTemplate o grpc) enviándole el token al Servidor Auth.
        // Si responde 200 OK -> Return true.
        // Si responde 401 Unauth -> Return false.
        System.out.println(">>> [Seguridad] Auth Server respondió: Token Correcto.");
        return token.startsWith("Bearer ");
    }
}
