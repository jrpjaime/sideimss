package mx.gob.imss.contadores.service;

import mx.gob.imss.contadores.dto.DespachoRequestDto;
import mx.gob.imss.contadores.dto.DespachoResponseDto;

public interface DespachoService {
    DespachoResponseDto consultarDatosDespacho(DespachoRequestDto request);
}
