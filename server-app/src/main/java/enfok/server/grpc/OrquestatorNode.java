package enfok.server.grpc;

import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import enfok.server.service.TaskQueue;

@GrpcService
public class OrquestatorNode implements ProcessingOrchestrator {
    @Inject
    public TaskQueue taskQueue;

   @Override
    public Uni<ImageTask> getNextTask(WorkerIdentity request) {
        // Sacamos la tarea con más prioridad de la cola
        var task = taskQueue.pullTask(); 
        
        if (task == null) {
            return Uni.createFrom().item(ImageTask.newBuilder().setIsEmpty(true).build());
        }

        return Uni.createFrom().item(ImageTask.newBuilder()
                .setTaskId(task.id)
                .setImageUrl(task.url != null ? task.url : "")
                .build());
    }

    @Override
    public Uni<Ack> updateTaskStatus(TaskProgress request) {
        // Se ejecuta cuando el worker en Python nos avisa cómo va aplicando el filtro
        System.out.println("====== REPORTE DE WORKER ======");
        System.out.println("WORKER ID: " + request.getWorkerId());
        System.out.println("BATCH ID : " + request.getTaskId());
        System.out.println("PROGRESO : " + request.getProgressPercentage() + "%");
        System.out.println("MENSAJE  : " + request.getStatusMessage());
        
        // (Futuro) Aquí inyectaremos la Base de Datos para que tu App de celular vea el % actualizado.
        return Uni.createFrom().item(Ack.newBuilder().setSuccess(true).build());
    }

    @Override
    public Uni<Ack> reportMetrics(NodeHealth request) {
        // Aquí actualizas tu mapa de monitoreo (para Grafana/Prometheus)
        System.out.println("Nodo " + request.getWorkerId() + " CPU: " + request.getCpuUsage());
        return Uni.createFrom().item(Ack.newBuilder().setSuccess(true).build());
    }

    @Override
    public Uni<Ack> sendHeartbeat(WorkerIdentity request) {
        // Actualizamos la estampa de tiempo de "última vez visto"
        // Si pasan 10 seg sin esto, re-encolamos sus tareas
        return Uni.createFrom().item(Ack.newBuilder().setSuccess(true).build());
    }
}
