package mx.gob.imss.autodeterminaciones.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mx.gob.imss.autodeterminaciones.dto.SwtAseguradoDto;
import mx.gob.imss.autodeterminaciones.dto.SwtMovimientoDto;

public interface SwtAseguradoService {
   
    public Page<SwtAseguradoDto> findAllPageableSwtAsegurado(SwtAseguradoDto swtAseguradoDto, Pageable pageable);

    public Page<SwtMovimientoDto> findAllPageableSwtMovimientos(SwtMovimientoDto swtMovimientoDto, Pageable pageable);
}
