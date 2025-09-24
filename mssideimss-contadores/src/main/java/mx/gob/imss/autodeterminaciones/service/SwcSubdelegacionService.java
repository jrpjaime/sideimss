package mx.gob.imss.autodeterminaciones.service;

import java.util.List;

import mx.gob.imss.autodeterminaciones.dto.SdcSubdelegacionDto;
import mx.gob.imss.autodeterminaciones.dto.SdcSubdelegacionFiltroDto;
 

public interface  SwcSubdelegacionService { 

 public List<SdcSubdelegacionDto> findAllSdcSubdelegacion(SdcSubdelegacionFiltroDto sdcSubdelegacionFiltroDto);

}
