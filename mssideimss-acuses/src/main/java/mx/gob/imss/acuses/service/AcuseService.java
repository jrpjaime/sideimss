package mx.gob.imss.acuses.service;

import mx.gob.imss.acuses.dto.DecargarAcuseDto;
import mx.gob.imss.acuses.dto.PlantillaDatoDto;

public interface AcuseService {

	public DecargarAcuseDto consultaAcuseByUrlDocumento(String urlDocumento);
	public DecargarAcuseDto consultaAcuseByPlantillaDato(PlantillaDatoDto plantillaDatoDto);

}
