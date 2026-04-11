package enfok.server.port;

import enfok.server.model.entity.BatchRecord;

/**
 * [PORT - OUT]: Puerto de Salida
 * Arquitectura Hexagonal: Este puerto dictamina las necesidades del núcleo hacia la infraestructura.
 * El núcleo grita: "No me importa qué base de datos uses (SQL, Mongo, RAM), 
 * pero necesito conectar alguien aquí que implemente el método persist()".
 */
public interface BatchRepository {
    void persist(BatchRecord record);
}
