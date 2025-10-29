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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode; 
import com.fasterxml.jackson.databind.JsonNode; 


import mx.gob.imss.contadores.dto.CadenaOriginalRequestDto;
import mx.gob.imss.contadores.dto.CorreoDto;
import mx.gob.imss.contadores.dto.DocumentoIndividualDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.repository.NdtPlantillaDatoRepository;
import mx.gob.imss.contadores.dto.MedioContactoContadoresDto;  
import mx.gob.imss.contadores.dto.MediosContactoContadoresResponseDto;
import mx.gob.imss.contadores.dto.SelloResponseDto;
import reactor.core.publisher.Mono;  
 
import org.springframework.http.HttpHeaders;  

@Service("acreditacionMembresiaService")
public class AcreditacionMembresiaServiceImpl implements AcreditacionMembresiaService {
	private static final Logger logger = LogManager.getLogger(AcreditacionMembresiaServiceImpl.class);

    @Value("${serviciosdigitales.url.correo}")
    private String urlSendCorreoElectronico;

    @Value("${sideimss.catalogos.microservice.url}") 
    private String catalogosMicroserviceUrl;

    @Value("${sideimss.acuses.microservice.url}")  
    private String acusesMicroserviceUrl;
	
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






    @Override
 public Mono<NdtPlantillaDato> obtenerSelloYGuardarPlantilla(NdtPlantillaDato ndtPlantillaDato, String jwtToken) {
        logger.info("Iniciando proceso para obtener sello digital y guardar plantilla.");

        final String datosJson = ndtPlantillaDato.getDesDatos(); // Hacer final 
        logger.info("obtenerSelloYGuardarPlantilla Contenido inicial de desDatos (datosJson): {}", datosJson);
        final String initialCadenaOriginal;
        final String nombreCompleto;
        final String curp;
        final String folioFirma;

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(datosJson);

            JsonNode cadenaOriginalNode = rootNode.get("cadenaOriginal");
            if (cadenaOriginalNode != null) {
                initialCadenaOriginal = cadenaOriginalNode.asText();
                logger.debug("Cadena original extraída: {}", initialCadenaOriginal);
            } else {
                logger.warn("No se encontró 'cadenaOriginal' en el JSON de datos.");
                return Mono.error(new RuntimeException("Error: La cadenaOriginal no se encontró en los datos de la plantilla."));
            }

            JsonNode nombreCompletoNode = rootNode.get("nombreCompleto");
            nombreCompleto = (nombreCompletoNode != null) ? nombreCompletoNode.asText() : null;
            if (nombreCompleto != null) logger.debug("Nombre completo extraído: {}", nombreCompleto);

            JsonNode curpNode = rootNode.get("curp");
            curp = (curpNode != null) ? curpNode.asText() : null;
            if (curp != null) logger.debug("CURP extraída: {}", curp);

            JsonNode folioFirmaNode = rootNode.get("folioFirma");
            folioFirma = (folioFirmaNode != null) ? folioFirmaNode.asText() : null;
            if (folioFirma != null) logger.debug("folioFirma extraída: {}", folioFirma);

        } catch (Exception e) {
            logger.error("Error al parsear datosJson para extraer cadenaOriginal: {}", e.getMessage(), e);
            return Mono.error(new RuntimeException("Error al procesar los datos de la plantilla para obtener la cadena original."));
        }

     
        final String modifiedCadenaOriginal;  

        if (folioFirma != null && !folioFirma.isEmpty()) {
            String hashTag = "|HASH|";
            int indexHash = initialCadenaOriginal.indexOf(hashTag);
            if (indexHash != -1) {
                int startIndexHashValue = indexHash + hashTag.length();
                int endIndexHashValue = initialCadenaOriginal.indexOf("|", startIndexHashValue);

                String currentHashValue = "";
                if (endIndexHashValue != -1) {
                    currentHashValue = initialCadenaOriginal.substring(startIndexHashValue, endIndexHashValue);
                } else {
                    currentHashValue = initialCadenaOriginal.substring(startIndexHashValue);
                }

                String newHashSegment = hashTag + folioFirma + "|" + currentHashValue;
                modifiedCadenaOriginal = initialCadenaOriginal.replace(hashTag + currentHashValue, newHashSegment);
                logger.info("Cadena original modificada con folioFirma: {}", modifiedCadenaOriginal);
            } else {
                logger.warn("No se encontró el tag '|HASH|' en la cadena original para insertar folioFirma.");
                modifiedCadenaOriginal = initialCadenaOriginal; // Si no se encuentra, usamos la original
            }
        } else {
            modifiedCadenaOriginal = initialCadenaOriginal; // Si no hay folioFirma, usamos la original
        }
        // --- Fin de la lógica de modificación ---

        // 'modifiedCadenaOriginal' ahora es efectivamente final y puede ser usada en las lambdas.

        // Preparar la solicitud para el microservicio de acuses
        CadenaOriginalRequestDto requestDto = new CadenaOriginalRequestDto();
        requestDto.setCadenaOriginal(modifiedCadenaOriginal); // Usamos la variable final/effectively final
        requestDto.setRfc(ndtPlantillaDato.getDesRfc());


        if (nombreCompleto != null) {
            requestDto.setNombreRazonSocial(nombreCompleto);
        }
        if (curp != null) {
            requestDto.setCurp(curp);
        }

        String urlGeneraSello = acusesMicroserviceUrl.trim() + "/generaSello";
        logger.info("Llamando al microservicio de acuses para generar sello en: {}", urlGeneraSello);

        return webClient.post()
            .uri(urlGeneraSello)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .bodyValue(requestDto)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> {
                logger.error("Error HTTP {} al obtener sello digital de mssideimss-acuses.", response.statusCode());
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                        "Error al generar el sello digital (" + response.statusCode().value() + "): " + errorBody
                    )));
            })
            .bodyToMono(SelloResponseDto.class)
            .flatMap(selloResponseDto -> {
                if (selloResponseDto.getCodigo() == 0 && selloResponseDto.getSello() != null && !selloResponseDto.getSello().isEmpty()) {
                    logger.info("Sello digital obtenido exitosamente.");
                    String selloDigitalIMSS = selloResponseDto.getSello();

                    // Insertar el sello en el JSON de datos
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        // Al crear el rootNode a partir de 'datosJson', obtenemos una copia.
                        // Luego modificamos esta copia.
                        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(datosJson);
                        rootNode.put("selloDigitalIMSS", selloDigitalIMSS);

                        // Es crucial actualizar la cadenaOriginal dentro del JSON con la versión modificada
                        rootNode.put("cadenaOriginal", modifiedCadenaOriginal); // Usamos la variable final/effectively final

                        ndtPlantillaDato.setDesDatos(objectMapper.writeValueAsString(rootNode));
                        logger.info("Sello digital y cadenaOriginal (si fue modificada) insertados en desDatos. Datos actualizados: {}", ndtPlantillaDato.getDesDatos());
                    } catch (Exception e) {
                        logger.error("Error al insertar el sello digital/actualizar cadenaOriginal en el JSON de datos: {}", e.getMessage(), e);
                        return Mono.error(new RuntimeException("Error al actualizar los datos con el sello digital."));
                    }

                    // Guardar la plantilla con los datos actualizados
                    logger.info("Guardando NdtPlantillaDato con sello digital.");
                    return Mono.just(ndtPlantillaDatoRepository.save(ndtPlantillaDato));
                } else {
                    logger.error("El microservicio de acuses devolvió un error al generar el sello: {} - {}", selloResponseDto.getCodigo(), selloResponseDto.getMensaje());
                    return Mono.error(new RuntimeException("Ocurrio un error, por favor intente mas tarde: " + selloResponseDto.getMensaje()));
                }
            })
            .onErrorResume(e -> {
                logger.error("Fallo completo al obtener el sello o guardar la plantilla: {}", e.getMessage(), e);
                return Mono.error(new RuntimeException("Ocurrio un error, por favor intente mas tarde: " + e.getMessage()));
            });
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