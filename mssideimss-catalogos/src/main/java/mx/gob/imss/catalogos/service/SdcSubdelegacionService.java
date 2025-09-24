package mx.gob.imss.catalogos.service;

import java.util.List;

import mx.gob.imss.catalogos.dto.SdcSubdelegacionDto;
import mx.gob.imss.catalogos.dto.SdcSubdelegacionFiltroDto;
 

public interface  SdcSubdelegacionService { 

 public List<SdcSubdelegacionDto> findAllSdcSubdelegacion(SdcSubdelegacionFiltroDto sdcSubdelegacionFiltroDto);

}
