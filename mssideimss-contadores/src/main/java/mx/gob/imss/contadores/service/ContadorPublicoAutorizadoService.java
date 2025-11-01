package mx.gob.imss.contadores.service;

import mx.gob.imss.contadores.dto.SolicitudBajaDto;

public interface ContadorPublicoAutorizadoService {

    /**
     * Consulta y devuelve todos los datos del contador, concentrados en un único objeto DTO.
     * @param rfc El RFC del contador a consultar.
     * @return Objeto SolicitudBajaDTO con los datos del contador.
     */
    SolicitudBajaDto getDatosContador(String rfc);
    
}
