package mx.gob.imss.contadores.service;

import mx.gob.imss.contadores.dto.DocumentoIndividualDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import reactor.core.publisher.Mono;

public interface AcreditacionMembresiaService {
 
    Mono<DocumentoIndividualDto> cargarDocumentoAlmacenamiento(DocumentoIndividualDto documento, String finalJwtToken);
    NdtPlantillaDato guardarPlantillaDato(NdtPlantillaDato plantillaDato);
}
