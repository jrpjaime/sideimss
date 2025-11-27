package mx.gob.imss.catalogos.service;

import mx.gob.imss.catalogos.dto.DespachoRequestDto;
import mx.gob.imss.catalogos.dto.DespachoResponseDto;

public interface DespachoService {
    DespachoResponseDto consultarDatosDespacho(DespachoRequestDto request);
}
