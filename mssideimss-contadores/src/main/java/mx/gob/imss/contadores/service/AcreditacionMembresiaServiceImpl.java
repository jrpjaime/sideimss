package mx.gob.imss.contadores.service;

 
 

import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import mx.gob.imss.contadores.dto.CorreoDto;
import mx.gob.imss.contadores.dto.DocumentoIndividualDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.repository.NdtPlantillaDatoRepository;
import mx.gob.imss.contadores.dto.MedioContactoContadoresDto;  
import mx.gob.imss.contadores.dto.MediosContactoContadoresResponseDto; 
import reactor.core.publisher.Mono;  
 
import org.springframework.http.HttpHeaders;  

@Service("acreditacionMembresiaService")
public class AcreditacionMembresiaServiceImpl implements AcreditacionMembresiaService {
	private static final Logger logger = LogManager.getLogger(AcreditacionMembresiaServiceImpl.class);

    @Value("${serviciosdigitales.url.correo}")
    private String urlSendCorreoElectronico;

    @Value("${sideimss.catalogos.microservice.url}") 
    private String catalogosMicroserviceUrl;
	
    @Autowired
    private NdtPlantillaDatoRepository  ndtPlantillaDatoRepository;
	
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
                if (e instanceof RuntimeException && e.getMessage() != null && e.getMessage().contains("Error del microservicio de documentos")) {
                    errorDto.setCodigo(HttpStatus.BAD_GATEWAY.value()); 
                } else {
                    errorDto.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }
                errorDto.setMensaje("Fallo en la comunicación/procesamiento del documento: " + e.getMessage());
                return Mono.just(errorDto);
            });
    }

    @Override
    public NdtPlantillaDato guardarPlantillaDato(NdtPlantillaDato plantillaDato) {
        logger.info("Guardando NdtPlantillaDato con datos: {}", plantillaDato.getDesDatos());
        return ndtPlantillaDatoRepository.save(plantillaDato);
    }

    // Implementación del método para el envío de correo
    @Override
    public Mono<String> enviarCorreoAcreditacion(String rfc, String nombreCompleto, String jwtToken) { // Cambia el retorno a Mono<String> para el mensaje
        logger.info("Preparando para enviar correo por acreditación/membresía para RFC: {}.", rfc);

        return obtenerCorreoDeMediosContacto(rfc, jwtToken)
            .flatMap(correoDestino -> {
                if (correoDestino == null || correoDestino.isEmpty()) {
                    logger.warn("No se pudo obtener un correo destino para el RFC: {}. Continuando sin enviar correo.", rfc);
                    return Mono.just("No se encontró un correo electrónico para el RFC proporcionado. El guardado de la plantilla continuará."); // Mensaje para el controlador
                }

                logger.info("Correo destino obtenido para RFC {}: {}", rfc, correoDestino);

                CorreoDto correoDto = new CorreoDto();
                correoDto.setRemitente("tramites.cpa@imss.gob.mx");
                correoDto.setCorreoPara(Collections.singletonList("jaime.rodriguez@imss.gob.mx")); // Para: (mantienes tu override para testing)
                correoDto.setAsunto("ES UNA PRUEBA NO RESPONDER Constancia de acreditación/membresía");

                String cuerpoCorreoHtml = String.format(
                        "<!DOCTYPE html>" +
                        "<html>" +
                        "<head><meta charset=\"UTF-8\"></head>" +
                        "<body>" +
                        "<strong>Estimado(a) %s con RFC %s,</strong><br><br>" +
                        "<p style='margin-bottom: 15px; line-height: 1.5;'>" +
                        "Se le informa que la presentación de su constancia de acreditación de evaluación en materia de la " +
                        "Ley del Seguro Social y sus reglamentos y su constancia de ser integrante o miembro de un colegio o " +
                        "asociación de profesionales de la contaduría pública, han sido recibidas." +
                        "</p>" +
                        "<p style='margin-bottom: 15px; line-height: 1.5;'>" +
                        "<strong>Por lo anterior, se anexa al presente el respectivo acuse de recibo.</strong>" +
                        "</p>" +
                        "<p style='margin-bottom: 15px; line-height: 1.5;'>" +
                        "Asimismo, podrá dar seguimiento a su trámite en la siguiente liga: " +
                        "<a href=\"http://agqa.imss.gob.mx/escritorio/web/publico\">" +
                        "http://agqa.imss.gob.mx/escritorio/web/publico" +
                        "</a>" +
                        "</p>" +
                        "<br>" +
                        "<p style='font-size: 12px; color: #777;'>" +
                        "Este es un correo automático. Por favor, no responda a esta dirección." +
                        "</p>" +
                        "</body>" +
                        "</html>",
                        nombreCompleto, rfc
                    );
                correoDto.setCuerpoCorreo(cuerpoCorreoHtml);

                return webClient.post()
                    .uri(urlSendCorreoElectronico)
                    .bodyValue(correoDto)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        logger.error("Error HTTP {} al intentar enviar correo a {}.", response.statusCode(), correoDestino);
                        return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException(
                                "Fallo al enviar el correo: " + response.statusCode().value() + " - " + errorBody
                            )));
                    })
                    .toBodilessEntity()
                    .thenReturn("Correo enviado exitosamente.") // Si el envío es exitoso, devuelve este mensaje
                    .onErrorResume(e -> {
                        if (e instanceof WebClientResponseException) {
                            WebClientResponseException we = (WebClientResponseException) e;
                            logger.error("Fallo WebClient al enviar correo a {}: {} - {}. Continuando con el guardado de la plantilla.", correoDestino, we.getStatusCode(), we.getMessage());
                        } else {
                            logger.error("Fallo genérico al enviar correo a {}: {}. Continuando con el guardado de la plantilla.", correoDestino, e.getMessage());
                        }
                        return Mono.just("Error en el servicio de envío de correo. El guardado de la plantilla continuará."); // Mensaje para el controlador
                    });
            })
            .onErrorResume(e -> { // Manejo de errores de obtenerCorreoDeMediosContacto
                logger.warn("No se pudo obtener el correo para RFC {}: {}. Continuando sin enviar correo.", rfc, e.getMessage());
                return Mono.just("No se pudo obtener un correo electrónico. El guardado de la plantilla continuará."); // Mensaje para el controlador
            });
    }

    private Mono<String> obtenerCorreoDeMediosContacto(String rfc, String jwtToken) {
        logger.info("Llamando a mssideimss-catalogos para obtener medios de contacto para RFC: {}", rfc);
        String url = catalogosMicroserviceUrl.trim() + "/mediosContacto/" + rfc;
        logger.info("conectando a : {}", url);

        return webClient.get()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> {
                logger.error("Error HTTP {} al obtener medios de contacto de mssideimss-catalogos para RFC {}.", response.statusCode(), rfc);
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                        "Fallo al obtener correo de catalogos (" + response.statusCode().value() + "): " + errorBody
                    )));
            })
            .bodyToMono(MediosContactoContadoresResponseDto.class)
            .map(response -> {
                if (response != null && response.getMedios() != null && !response.getMedios().isEmpty()) {
                    for (MedioContactoContadoresDto medio : response.getMedios()) {
                        if ("1".equalsIgnoreCase(medio.getTipoContacto())) {
                            logger.info("Correo electrónico obtenido de mssideimss-catalogos para RFC {}: {}", rfc, medio.getDesFormaContacto());
                            return medio.getDesFormaContacto();
                        }
                    }
                }
                logger.warn("No se encontró un correo electrónico en la respuesta de mssideimss-catalogos para RFC {}.", rfc);
                return null; // Devuelve null si no se encuentra un correo
            })
            .onErrorResume(e -> {
                logger.error("Fallo completo al obtener correo de mssideimss-catalogos para RFC {}: {}. Se asume que no hay correo.", rfc, e.getMessage(), e);
                return Mono.just(""); // Devuelve un string vacío para indicar que no hay correo, pero no detiene la ejecución.
            });
    }
}