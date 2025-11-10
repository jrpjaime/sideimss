package mx.gob.imss.contadores.service;

import mx.gob.imss.contadores.dto.ColegioContadorDto;
import mx.gob.imss.contadores.dto.SolicitudBajaDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import reactor.core.publisher.Mono;

public interface ContadorPublicoAutorizadoService {

    /**
     * Consulta y devuelve todos los datos del contador, concentrados en un único objeto DTO.
     * @param rfc El RFC del contador a consultar.
     * @return Objeto SolicitudBajaDTO con los datos del contador.
     */
    SolicitudBajaDto getDatosContador(String rfc);

     public Mono<NdtPlantillaDato> obtenerSelloYGuardarPlantilla(NdtPlantillaDato ndtPlantillaDato, String jwtToken);

      ColegioContadorDto getColegioByRfcContador(String rfcContador);
    
}
