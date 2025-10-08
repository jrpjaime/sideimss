package mx.gob.imss.acuses.service;

import mx.gob.imss.acuses.dto.DecargarAcuseDto;

public interface AcuseService {

	DecargarAcuseDto consultaAcuseByUrlDocumento(String urlDocumento);

}
