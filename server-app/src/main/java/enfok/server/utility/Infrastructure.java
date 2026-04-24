package enfok.server.utility;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utilidad para gestionar la infraestructura global del servidor, 
 * como el pool de hilos para procesamiento asíncrono.
 */
public class Infrastructure {
    
    private static final ExecutorService workerPool = Executors.newFixedThreadPool(10);

    /**
     * Retorna un ExecutorService para tareas que no deben bloquear el hilo principal (ej. Logging).
     */
    public static ExecutorService getDefaultWorkerPool() {
        return workerPool;
    }
}
