package mx.gob.imss.catalogos.service;

import mx.gob.imss.catalogos.dto.RfcColegioRequestDto;
import mx.gob.imss.catalogos.dto.RfcColegioResponseDto;

public interface  SatService { 
 RfcColegioResponseDto consultarRfc(RfcColegioRequestDto rfcRequestDto);
}
