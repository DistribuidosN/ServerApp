package enfok.server.ports.port;

import enfok.server.model.soap.BatchRequestDto;
import enfok.server.model.soap.BatchResponseDto;

/**
 * [PORT - IN]: Puerto de Entrada
 * Arquitectura Hexagonal: Los "Puertos" son las interfaces que dictan las reglas del juego.
 * Este puerto define qué operaciones puede realizar el núcleo de nuestra aplicación (Service),
 * abstrayéndolo para que quienes estén afuera (el Endpoint SOAP) sepan cómo llamarlo sin ver su código.
 */
public interface BatchOrchestratorService {
    BatchResponseDto handleBatch(BatchRequestDto request);
}
