package enfok.server.model.entity;

/**
 * [CAPA MODEL / ENTITY]: Modelo exclusivo de Datos Puros.
 * Estas clases usualmente llevan las anotaciones como @Entity, @Id, @Column.
 * Jamás viajan directamente en el endpoint SOAP (solo las usa el Repository y Mapper).
 */
public class BatchRecord {
    public String id;
    public String data;
    public String status;
}
