package enfok.server.utility;

import jakarta.enterprise.context.ApplicationScoped;
import enfok.server.model.soap.BatchRequestDto;
import enfok.server.model.entity.BatchRecord;
import enfok.server.ports.port.BatchMapper; // <-- Enlazando

@ApplicationScoped
public class BatchMapperImpl implements BatchMapper {
    @Override
    public BatchRecord toEntity(BatchRequestDto dto) {
        System.out.println(">>> 2. Utility (Mapper): Convistiendo campos del contrato SOAP hacia un Entity puro");
        BatchRecord entity = new BatchRecord();
        entity.id = dto.getBatchId();
        entity.data = dto.getData();
        entity.status = "PENDING";
        return entity;
    }
}
