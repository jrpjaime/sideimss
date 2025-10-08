package mx.gob.imss.bi.service;

import mx.gob.imss.bi.dto.DecargarAcuseDto;

public interface AcuseService {

	DecargarAcuseDto consultaAcuseByUrlDocumento(String urlDocumento);

}
