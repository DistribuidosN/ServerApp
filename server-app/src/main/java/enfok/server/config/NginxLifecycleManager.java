package enfok.server.config;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import java.io.File;
import java.io.IOException;

@ApplicationScoped
public class NginxLifecycleManager {

    private static final Logger LOG = Logger.getLogger(NginxLifecycleManager.class);

    @ConfigProperty(name = "nginx.path")
    String nginxPath;

    private Process nginxProcess;

    void onStart(@Observes StartupEvent ev) {
        LOG.info("Inicializando NginxLifecycleManager...");

        File nginxExe = new File(nginxPath);
        if (!nginxExe.exists() || !nginxExe.isFile()) {
            LOG.error("El archivo ejecutable de Nginx no se encontró en la ruta configurada: " + nginxPath);
            return;
        }

        File nginxDir = nginxExe.getParentFile();

        try {
            LOG.info("Iniciando Nginx desde: " + nginxPath);
            ProcessBuilder pb = new ProcessBuilder(nginxPath);
            pb.directory(nginxDir);
            
            // Inicia el proceso en segundo plano
            nginxProcess = pb.start();
            LOG.info("Nginx iniciado correctamente.");
        } catch (IOException e) {
            LOG.error("Error de E/S al intentar iniciar Nginx: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error("Error inesperado al intentar iniciar Nginx: " + e.getMessage(), e);
        }
    }

    void onStop(@Observes ShutdownEvent ev) {
        LOG.info("Deteniendo Nginx...");

        File nginxExe = new File(nginxPath);
        if (!nginxExe.exists() || !nginxExe.isFile()) {
            LOG.warn("No se puede detener Nginx de forma limpia, no se encontró el ejecutable en: " + nginxPath);
            return;
        }

        File nginxDir = nginxExe.getParentFile();

        try {
            LOG.info("Enviando señal de stop a Nginx (-s stop)...");
            ProcessBuilder pb = new ProcessBuilder(nginxPath, "-s", "stop");
            pb.directory(nginxDir);
            
            Process stopProcess = pb.start();
            int exitCode = stopProcess.waitFor();
            
            if (exitCode == 0) {
                LOG.info("Nginx detenido exitosamente de forma limpia.");
            } else {
                LOG.warn("El comando de detención de Nginx retornó el código: " + exitCode);
            }
            
            // Destruir la referencia al proceso original si sigue vivo por algún motivo
            if (nginxProcess != null && nginxProcess.isAlive()) {
                nginxProcess.destroy();
            }

        } catch (IOException e) {
            LOG.error("Error de E/S al intentar detener Nginx: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            LOG.error("Proceso de detención interrumpido: " + e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }
}
