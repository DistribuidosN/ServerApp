package enfok.server.ports.port;

import enfok.server.model.soap.BatchRequestDto;
import enfok.server.model.entity.BatchRecord;

/**
 * [PORT - INTERNAL]: Puerto Interno para traducciones.
 * Obliga a tener una regla estricta de cómo los Objetos que viajan por red (DTO) 
 * se vuelven Objetos de dominio (Entities).
 */
public interface BatchMapper {
    BatchRecord toEntity(BatchRequestDto dto);
}
