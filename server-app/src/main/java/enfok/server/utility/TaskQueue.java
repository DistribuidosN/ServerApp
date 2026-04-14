package enfok.server.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.concurrent.PriorityBlockingQueue;
import enfok.server.model.grpc.ImageQueue;

@ApplicationScoped
public class TaskQueue {
    
    // Una cola en memoria, a prueba de hilos (Thread-safe)
    private PriorityBlockingQueue<ImageQueue> colaDeTrabajo = new PriorityBlockingQueue<>();

    // El Gateway (Go) llama a este método para encolar
    public void addNewImageTask(String id, int w, int h, double filterMultiplier) {
        ImageQueue task = new ImageQueue(id, w, h, filterMultiplier);
        colaDeTrabajo.offer(task); // Se inserta y se reordena sola al instante
    }

    // Los Nodos Python llaman a este método vía gRPC para pedir trabajo
    public ImageQueue pullTask() {
        // poll() saca la tarea con más prioridad. Devuelve null si la cola está vacía.
        return colaDeTrabajo.poll();
    }
}