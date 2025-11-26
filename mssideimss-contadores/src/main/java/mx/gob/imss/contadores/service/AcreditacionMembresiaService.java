package mx.gob.imss.contadores.service;

import mx.gob.imss.contadores.dto.DocumentoIndividualDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import reactor.core.publisher.Mono;

public interface AcreditacionMembresiaService {
 
    Mono<DocumentoIndividualDto> cargarDocumentoAlmacenamiento(DocumentoIndividualDto documento, String finalJwtToken);
    NdtPlantillaDato guardarPlantillaDato(NdtPlantillaDato plantillaDato);
    Mono<String> enviarCorreoAcreditacion(String rfc, String nombreCompleto, String jwtToken); 
    public Mono<String> enviarCorreoModificacionDatosContacto(String rfc, String nombreCompleto, String jwtToken);
     public Mono<String> enviarCorreoModificacionDatosDespacho(String rfc, String nombreCompleto, String jwtToken);
      public Mono<String> enviarCorreoModificacionDatosColegio(String rfc, String nombreCompleto, String jwtToken);
    public Mono<String> enviarCorreoSolicitudBaja(String rfc, String nombreCompleto, String jwtToken);
    Mono<NdtPlantillaDato> obtenerSelloYGuardarPlantilla(NdtPlantillaDato ndtPlantillaDato, String jwtToken);
}
