package mx.gob.imss.contadores.service;

 
 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import mx.gob.imss.contadores.dto.DocumentoIndividualDto;
import reactor.core.publisher.Mono;  
 
import org.springframework.http.HttpHeaders;  

@Service("acreditacionMembresiaService")
public class AcreditacionMembresiaServiceImpl implements AcreditacionMembresiaService {
	private static final Logger logger = LogManager.getLogger(AcreditacionMembresiaServiceImpl.class);
	
 
	
    private final WebClient webClient;

    @Value("${documentos.microservice.url}")
    private String documentosMicroserviceUrl;

 
    public AcreditacionMembresiaServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<DocumentoIndividualDto> cargarDocumentoAlmacenamiento(DocumentoIndividualDto documento, String jwtToken) {
        logger.info("Preparando para enviar documento '{}' al microservicio de documentos.", documento.getNomArchivo());
        String url = documentosMicroserviceUrl;

        return webClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .bodyValue(documento)
            .retrieve()  
            .onStatus(HttpStatusCode::isError, response -> {  
                logger.error("Error HTTP {} al intentar cargar documento '{}'.", response.statusCode(), documento.getNomArchivo());
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                         
                        return Mono.error(new RuntimeException(
                            "Error del microservicio de documentos (" + response.statusCode().value() + "): " + errorBody
                        ));
                    });
            })
            .bodyToMono(DocumentoIndividualDto.class)
            .doOnSuccess(result -> {
                if (result.getCodigo() != null && result.getCodigo() == 0) {
                    logger.info("Documento '{}' cargado exitosamente. Path HDFS: {}", documento.getNomArchivo(), result.getDesPathHdfs());
                } else {
                    logger.warn("El microservicio de documentos devolvió un código de error para '{}': {} - {}", documento.getNomArchivo(), result.getCodigo(), result.getMensaje());
                }
            })
            .onErrorResume(e -> {
                logger.error("Fallo completo al cargar documento '{}': {}", documento.getNomArchivo(), e.getMessage(), e);
                DocumentoIndividualDto errorDto = new DocumentoIndividualDto();
                //mensaje de la excepción si es una WebClientResponseException
                // para obtener un código más preciso, o usar un genérico como BAD_GATEWAY si el microservicio falló.
                if (e instanceof RuntimeException && e.getMessage() != null && e.getMessage().contains("Error del microservicio de documentos")) {
                    errorDto.setCodigo(HttpStatus.BAD_GATEWAY.value()); // O el que consideres apropiado
                } else {
                    errorDto.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
                errorDto.setMensaje("Fallo en la comunicación/procesamiento del documento: " + e.getMessage());
                return Mono.just(errorDto);
            });
    }
}