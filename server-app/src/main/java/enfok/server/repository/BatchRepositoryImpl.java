package enfok.server.repository;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.Map;

import enfok.server.model.entity.BatchRecord;
import enfok.server.port.BatchRepository; // <-- Adaptándose al Puerto de Salida

/**
 * [ADAPTER - OUT]: Adaptador de Salida de Base de Datos.
 * Aquí está todo el código "tecnológico" y sucio que la arquitectura Hexagonal busca aislar.
 * Nosotros simplemente "enchufamos" (implements) esta clase al puerto correspondiente.
 */
@ApplicationScoped
public class BatchRepositoryImpl implements BatchRepository {
    private Map<String, BatchRecord> database = new HashMap<>();

    @Override
    public void persist(BatchRecord record) {
        System.out.println(">>> 3. Adapter OUT (DB): Ejecutando guardado falso en Memoria, ID: " + record.id);
        database.put(record.id, record);
    }
}
