package enfok.server;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

/**
 * [CLASE PRINCIPAL]: Main de Quarkus
 * Por defecto Quarkus genera su propio "main" invisible para arrancar ultrarrápido.
 * Sin embargo, usando @QuarkusMain podemos exponerlo para que puedas darle al botón de "Play"
 * desde tu editor cómodamente como en cualquier proyecto tradicional de Java.
 */
@QuarkusMain
public class Main {
    public static void main(String... args) {
        System.out.println("Iniciando ServerApp (Quarkus) desde el método main manual...");
        
        // Esto le dice a Quarkus que despierte, lea las configuraciones y levante la API SOAP
        Quarkus.run(args);
    }
}
