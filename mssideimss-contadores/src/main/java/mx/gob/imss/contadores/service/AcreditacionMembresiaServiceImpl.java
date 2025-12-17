package mx.gob.imss.contadores.service;

 
 

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import mx.gob.imss.contadores.entity.NdtContadorPublicoAut;
import mx.gob.imss.contadores.entity.NdtCpaAcreditacion;
import mx.gob.imss.contadores.entity.NdtCpaEstatus;
import mx.gob.imss.contadores.entity.NdtCpaTramite;
import mx.gob.imss.contadores.entity.NdtDocumentoProbatorio;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.entity.NdtR1DatosPersonales;
import mx.gob.imss.contadores.repository.NdtColegioRepository;
import mx.gob.imss.contadores.repository.NdtContadorPublicoAutRepository;
import mx.gob.imss.contadores.repository.NdtCpaAcreditacionRepository;
import mx.gob.imss.contadores.repository.NdtCpaEstatusRepository;
import mx.gob.imss.contadores.repository.NdtCpaTramiteRepository;
import mx.gob.imss.contadores.repository.NdtDocumentoProbatorioRepository;
import mx.gob.imss.contadores.repository.NdtPlantillaDatoRepository;
import mx.gob.imss.contadores.repository.NdtR1DatosPersonalesRepository;
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


    @Autowired
    private NdtContadorPublicoAutRepository contadorRepository;
    @Autowired
    private NdtCpaAcreditacionRepository acreditacionRepository;
    @Autowired
    private NdtR1DatosPersonalesRepository datosPersonalesRepository;
    @Autowired
    private NdtCpaEstatusRepository estatusRepository;

    @Autowired
    private NdtCpaTramiteRepository tramiteRepository;


    @Autowired
    private NdtColegioRepository colegioRepository;
    @Autowired
    private NdtDocumentoProbatorioRepository documentoProbatorioRepository;

 
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
                  //  return Mono.just(ndtPlantillaDatoRepository.save(ndtPlantillaDato));
                    // 1. Guardamos la Plantilla Dato (Tu código actual)
                    NdtPlantillaDato plantillaGuardada = ndtPlantillaDatoRepository.save(ndtPlantillaDato);
                    
                    // 2. AHORA GUARDAMOS EN TABLAS LEGACY
                    try {
                        guardarEnTablasLegado(plantillaGuardada);
                        logger.info("Datos replicados exitosamente en tablas Legacy Oracle");
                    } catch (Exception e) {
                        // Decidir si fallamos todo el proceso o solo logueamos el error
                        logger.error("Error al guardar en tablas legacy: {}", e.getMessage(), e);
                        // throw new RuntimeException("Error al sincronizar con sistema anterior"); 
                    }

                    return Mono.just(plantillaGuardada);


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


 


    /**
     * Método PRIVADO GENÉRICO que maneja toda la lógica de envío.
     * Recibe el asunto y el fragmento de HTML específico.
     */
    private Mono<String> procesarEnvioCorreo(String rfc, String nombreCompleto, String jwtToken, String asunto, String contenidoHtmlEspecifico) {
        logger.info("Iniciando proceso genérico de envío de correo para RFC: {}. Asunto: {}", rfc, asunto);

        return obtenerCorreoDeMediosContacto(rfc, jwtToken)
            .flatMap(correoDestino -> {
                if (correoDestino == null || correoDestino.isEmpty()) {
                    logger.warn("No se encontró correo para RFC: {}. Continuando.", rfc);
                    return Mono.just("No se encontró un correo electrónico. El proceso continuará.");
                }

                logger.info("Enviando correo a: {}", correoDestino);

                CorreoDto correoDto = new CorreoDto();
                correoDto.setRemitente("tramites.cpa@imss.gob.mx");
                // Para pruebas mantienes override, para prod usarías correoDestino
                //correoDto.setCorreoPara(Collections.singletonList("jaime.rodriguez@imss.gob.mx")); 
                correoDto.setCorreoPara(Collections.singletonList(correoDestino)); 
                correoDto.setAsunto(asunto);

                // Construcción del HTML completo usando el fragmento específico
                String cuerpoCorreoHtml = construirHtmlBase(nombreCompleto, rfc, contenidoHtmlEspecifico);
                correoDto.setCuerpoCorreo(cuerpoCorreoHtml);

                return webClient.post()
                    .uri(urlSendCorreoElectronico)
                    .bodyValue(correoDto)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response -> {
                        logger.error("Error HTTP {} al enviar correo a {}.", response.statusCode(), correoDestino);
                        return response.bodyToMono(String.class)
                            .flatMap(errorBody -> Mono.error(new RuntimeException(
                                "Fallo envío correo: " + response.statusCode().value() + " - " + errorBody)));
                    })
                    .toBodilessEntity()
                    .thenReturn("Correo enviado exitosamente.")
                    .onErrorResume(e -> {
                        String errorMsg = (e instanceof WebClientResponseException) 
                            ? "Fallo WebClient: " + ((WebClientResponseException) e).getStatusCode()
                            : "Fallo genérico: " + e.getMessage();
                        logger.error("Error enviando correo a {}: {}. Continuando.", correoDestino, errorMsg);
                        return Mono.just("Error en servicio de correo. El proceso continuará.");
                    });
            })
            .onErrorResume(e -> {
                logger.warn("Error al obtener medios de contacto para RFC {}: {}", rfc, e.getMessage());
                return Mono.just("No se pudo obtener correo electrónico. El proceso continuará.");
            });
    }

    /**
     * Construye la estructura HTML común para todos los correos.
     */
    private String construirHtmlBase(String nombre, String rfc, String contenidoEspecifico) {
        return String.format(
            "<!DOCTYPE html>" +
            "<html><head><meta charset=\"UTF-8\"></head><body>" +
          //  "<strong>Estimado(a) %s con RFC %s,</strong><br><br>" +
            "%s" + // Aquí se inyecta el contenido variable
            "<p style='margin-bottom: 15px; line-height: 1.5;'>" +
            "<strong>Por lo anterior, se anexa al presente el respectivo acuse de recibo.</strong>" +
            "</p>" +
            "<p style='margin-bottom: 15px; line-height: 1.5;'>" +
            "Asimismo, podrá dar seguimiento a su trámite en la siguiente liga: " +
            "<a href=\"http://agqa.imss.gob.mx/escritorio/web/publico\">http://agqa.imss.gob.mx/escritorio/web/publico</a>" +
            "</p><br>" +
            "<p style='font-size: 12px; color: #777;'>" +
            "Este es un correo automático. Por favor, no responda a esta dirección.</p>" +
            "</body></html>",
           contenidoEspecifico
        );
    }

    // --- MÉTODOS PÚBLICOS REFRACTORIZADOS (Ahora son de una sola línea lógica) ---

    @Override
    public Mono<String> enviarCorreoAcreditacion(String rfc, String nombreCompleto, String jwtToken) {
        String contenido = "<p style='margin-bottom: 15px; line-height: 1.5;'>" +
                "Se le informa que la presentación de su constancia de acreditación de evaluación en materia de la " +
                "Ley del Seguro Social y sus reglamentos y su constancia de ser integrante o miembro de un colegio o " +
                "asociación de profesionales de la contaduría pública, han sido recibidas.</p>";
        
        return procesarEnvioCorreo(rfc, nombreCompleto, jwtToken, "Constancia de acreditación/membresía", contenido);
    }

    @Override
    public Mono<String> enviarCorreoSolicitudBaja(String rfc, String nombreCompleto, String jwtToken) {
        // Nota: Usé el texto estándar de baja, ajusta si el texto original era intencional
        String contenido = "<p style='margin-bottom: 15px; line-height: 1.5;'>" +
                "Se le informa que su solicitud de baja en el registro de contadores públicos autorizados ha sido recibida.</p>";
                
        return procesarEnvioCorreo(rfc, nombreCompleto, jwtToken,  "SOLICITUD DE BAJA", contenido);
    }

    @Override
    public Mono<String> enviarCorreoModificacionDatosContacto(String rfc, String nombreCompleto, String jwtToken) {
        String contenido = "<p>Se le informa que el aviso de modificación de datos en el registro de contadores públicos autorizados ha sido recibido.</p>" +
                           "<p><span class=\"label\">Datos modificados:</span> Datos personales.</p>";
                           
        return procesarEnvioCorreo(rfc, nombreCompleto, jwtToken,  "Aviso de modificación de datos en el registro de contadores públicos autorizados", contenido);
    }

    @Override
    public Mono<String> enviarCorreoModificacionDatosDespacho(String rfc, String nombreCompleto, String jwtToken) {
        String contenido = "<p>Se le informa que el aviso de modificación de datos en el registro de contadores públicos autorizados ha sido recibido.</p>" +
                           "<p><span class=\"label\">Datos modificados:</span> Despacho.</p>";
                           
        return procesarEnvioCorreo(rfc, nombreCompleto, jwtToken,  "Aviso de modificación de datos en el registro de contadores públicos autorizados", contenido);
    }

    @Override
    public Mono<String> enviarCorreoModificacionDatosColegio(String rfc, String nombreCompleto, String jwtToken) {
        String contenido = "<p>Se le informa que el aviso de modificación de datos en el registro de contadores públicos autorizados ha sido recibido.</p>" +
                           "<p><span class=\"label\">Datos modificados:</span> Colegio o asociación.</p>";
                           
        return procesarEnvioCorreo(rfc, nombreCompleto, jwtToken, "Aviso de modificación de datos en el registro de contadores públicos autorizados", contenido);
    }

    // Helper: Obtener Correo 
    private Mono<String> obtenerCorreoDeMediosContacto(String rfc, String jwtToken) {
        String url = catalogosMicroserviceUrl.trim() + "/mediosContacto/" + rfc;
        return webClient.get().uri(url).header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken).retrieve()
            .bodyToMono(MediosContactoContadoresResponseDto.class)
            .map(response -> {
                if (response != null && response.getMedios() != null) {
                    for (MedioContactoContadoresDto medio : response.getMedios()) {
                        if ("1".equalsIgnoreCase(medio.getTipoContacto())) return medio.getDesFormaContacto();
                    }
                }
                return null;
            })
            .onErrorResume(e -> Mono.just(""));
    }



  /**
     * Método para extraer la CURP del JSON y buscar al contador para guardar en tablas viejas.
     */
    private void guardarEnTablasLegado(NdtPlantillaDato plantilla) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(plantilla.getDesDatos());
        
        // 1. Obtener CURP del JSON
        final String curp = rootNode.has("CURP") ? rootNode.get("CURP").asText() : null;

        if (curp == null || curp.isEmpty()) {
            logger.warn("No se encontró CURP en el JSON, no se puede guardar en Legacy.");
            return;
        }

      

        // 2. Buscar al Contador por CURP
        NdtContadorPublicoAut contador = contadorRepository.findByCurp(curp).orElseThrow(() -> new RuntimeException("Contador no encontrado en BD Legacy para CURP: " + curp));


        String tipoAcuse = plantilla.getDesTipoAcuse(); 

        // 3. Derivar al guardado específico (ACTUALIZADO CON TUS ENUMS)
        if ("ACREDITACION_MEMBRESIA".equalsIgnoreCase(tipoAcuse)) {
            
            guardarAcreditacionLegacy(contador, rootNode);
            
        } else if ("ACUSE_SOLICITUD_CAMBIO".equalsIgnoreCase(tipoAcuse)) {
            
            guardarModificacionLegacy(contador, rootNode);
            
        } else if ("ACUSE_SOLICITUD_BAJA".equalsIgnoreCase(tipoAcuse)) {
            
            guardarBajaLegacy(contador, rootNode);
            
        } else {
            logger.warn("El tipo de acuse '{}' no tiene lógica de guardado Legacy configurada.", tipoAcuse);
        }
    }

      private void guardarAcreditacionLegacy(NdtContadorPublicoAut contador, JsonNode json) {
        NdtCpaAcreditacion acreditacion = new NdtCpaAcreditacion();
        LocalDateTime fechaActual = LocalDateTime.now();
        String usuario = contador.getCurp(); // O el RFC
        
        acreditacion.setCveIdCpa(contador.getCveIdCpa());
        acreditacion.setFecRegistroAlta(fechaActual);
        acreditacion.setFecPresentacionAcreditacion(fechaActual);
        acreditacion.setCveIdUsuario(usuario);

        // --- 1. BUSCAR EL COLEGIO (Automático por RFC) ---
        // Como no viene en el JSON, lo buscamos en la BD usando el usuario del contador
        // Suponemos que el contador ya tiene un colegio activo asociado a su usuario
        colegioRepository.findByCveIdUsuarioAndFecRegistroBajaIsNull(contador.getCveIdUsuario())
            .ifPresent(colegio -> acreditacion.setCveIdColegio(colegio.getCveIdColegio()));

        // --- 2. MAPEO DE FECHAS (Nombres corregidos según tu Angular) ---
        
        if (json.has("fechaExpedicionAcreditacion")) {
            // Mapea a la columna FEC_ACREDITACION_CP
            acreditacion.setFecAcreditacionCp(parseFecha(json.get("fechaExpedicionAcreditacion").asText()));
        }

        if (json.has("fechaExpedicionMembresia")) {
            // Mapea a FEC_DOCUMENTO1 (Usualmente usado para fecha de constancia/membresía)
            acreditacion.setFecDocumento1(parseFecha(json.get("fechaExpedicionMembresia").asText()));
        }

        // --- 3. TIPO (0=Acreditación, 1=Membresía) ---
        // Si subió ambos archivos, el sistema legacy suele marcarlo como 0 o según regla.
        // Aquí lo dejamos en 0 por defecto como en tu insert.
        acreditacion.setIndAcredMembresia(0);
        
        // Guardamos la acreditación
        NdtCpaAcreditacion acreditacionGuardada = acreditacionRepository.save(acreditacion);

        // --- 4. GUARDAR DOCUMENTOS (PDFs) en NDT_DOCUMENTO_PROBATORIO ---
        // IDs de tipo de documento (Debes confirmar estos IDs en tu tabla NDC_TIPO_DOCUMENTO)
        // Ejemplo: 74 = Acreditación, 132 = Membresía
        Long TIPO_DOC_ACREDITACION = 74L; 
        Long TIPO_DOC_MEMBRESIA = 132L;   

        if (json.has("desPathHdfsAcreditacion")) {
            guardarDocumentoLegacy(contador.getCveIdCpa(), 
                                   json.get("desPathHdfsAcreditacion").asText(), 
                                   TIPO_DOC_ACREDITACION, 
                                   usuario);
        }

        if (json.has("desPathHdfsMembresia")) {
            guardarDocumentoLegacy(contador.getCveIdCpa(), 
                                   json.get("desPathHdfsMembresia").asText(), 
                                   TIPO_DOC_MEMBRESIA, 
                                   usuario);
        }
        
        logger.info("Acreditación y Documentos guardados en Legacy para CPA: {}", contador.getCveIdCpa());
    }

    // Método auxiliar para guardar documentos
    private void guardarDocumentoLegacy(Long idCpa, String url, Long tipoDoc, String usuario) {
        if(url == null || url.isEmpty()) return;

        NdtDocumentoProbatorio doc = new NdtDocumentoProbatorio();
        doc.setCveIdCpa(idCpa);
        doc.setUrlDocumentoProb(url); // Aquí va el path de HDFS o JSON de Bóveda
        doc.setCveIdDoctoProbPorTipo(tipoDoc);
        doc.setFecRegistroAlta(LocalDateTime.now());
        doc.setCveIdUsuario(usuario);
        
        documentoProbatorioRepository.save(doc);
    }

    /**
     * Método auxiliar para convertir String a LocalDateTime.
     * Soporta formato ISO (2025-12-17) y formato MX (17/12/2025)
     */
    private LocalDateTime parseFecha(String fechaStr) {
        if (fechaStr == null || fechaStr.isEmpty()) return null;
        try {
            // Intento 1: Formato DD/MM/YYYY (El que pusiste en tu ejemplo)
            DateTimeFormatter formatterMX = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return LocalDate.parse(fechaStr, formatterMX).atStartOfDay();
        } catch (Exception e1) {
            try {
                // Intento 2: Formato ISO YYYY-MM-DD (Estándar de JSON)
                return LocalDate.parse(fechaStr).atStartOfDay();
            } catch (Exception e2) {
                logger.warn("No se pudo parsear la fecha recibida: {}", fechaStr);
                return null;
            }
        }
    }

    /*
    
    115	NO ACTIVADO
1	SOLICITADO
2	RECHAZADO
3	ACTIVO
4	NO LOCALIZADO
5	1ERA. AMONESTACIÓN
6	2DA. AMONESTACIÓN
7	SUSPENSIÓN POR 1 AÑO
8	SUSPENSIÓN POR 2 AÑOS
9	SUSPENSIÓN POR 3 AÑOS
10	BAJA VOLUNTARIA
11	BAJA POR NO DICTAMINAR EN 5 AÑOS
12	SIN ACTIVACIÓN DE REGISTRO
13	BAJA POR FALLECIMIENTO
14	CANCELADO
101	NO ACTIVADO
102	NO ACTIVADO
103	NO ACTIVADO
104	NO ACTIVADO
105	NO ACTIVADO
106	NO ACTIVADO
107	NO ACTIVADO
108	NO ACTIVADO
109	NO ACTIVADO
110	NO ACTIVADO
111	NO ACTIVADO
112	NO ACTIVADO
113	NO ACTIVADO
114	NO ACTIVADO
15	3RA. AMONESTACIÓN
    */

   private void guardarBajaLegacy(NdtContadorPublicoAut contador, JsonNode json) {
        Long ID_ESTADO_BAJA = 10L; 
        LocalDateTime fechaActual = LocalDateTime.now();
        String usuario = contador.getCurp();
        String folioSolicitud = json.has("folioSolicitud") ? json.get("folioSolicitud").asText() : "S/F";

        // --- 1. Crear el TRÁMITE (El eslabón perdido) ---
        NdtCpaTramite tramite = new NdtCpaTramite();
        tramite.setCveIdCpa(contador.getCveIdCpa());
        tramite.setFecSolicitudMovimiento(fechaActual);
        tramite.setFecRegistroAlta(fechaActual);
        tramite.setCveIdUsuario(usuario);
        tramite.setNumTramiteNotaria(folioSolicitud); // Guardamos el UUID aquí para rastreo
        // tramite.setCveIdTramite(??); // Si tienes el ID de tipo de trámite "Baja" en catalogo, ponlo aquí.
        
        NdtCpaTramite tramiteGuardado = tramiteRepository.save(tramite);

        // --- 2. Guardar en Histórico (NDT_CPA_ESTATUS) ---
        NdtCpaEstatus estatus = new NdtCpaEstatus();
        estatus.setCveIdCpa(contador.getCveIdCpa());
        estatus.setCveIdEstadoCpa(ID_ESTADO_BAJA);
        estatus.setFecBaja(fechaActual);
        estatus.setFecRegistroAlta(fechaActual);
        estatus.setFecRegistroActualizado(fechaActual);
        estatus.setCveIdUsuario(usuario);
        
        // VINCULAMOS EL ESTATUS CON EL TRÁMITE
        estatus.setCveIdCpaTramite(tramiteGuardado.getCveIdCpaTramite()); 

        if (json.has("motivoBaja")) {
            String motivo = json.get("motivoBaja").asText();
            if (motivo != null && motivo.length() > 3100) motivo = motivo.substring(0, 3100);
            estatus.setDesComentarios(motivo);
        } else {
            estatus.setDesComentarios("Solicitud de Baja desde Portal Digital");
        }
        
        estatusRepository.save(estatus);

        // --- 3. Actualizar Maestro (NDT_CONTADOR_PUBLICO_AUT) ---
        contador.setCveIdEstadoCpa(ID_ESTADO_BAJA);
        contador.setFecRegistroBaja(fechaActual);
        contador.setFecRegistroActualizado(fechaActual);
        contador.setCveIdUsuario(usuario);
        
        contadorRepository.save(contador);
        
        logger.info("BAJA COMPLETA LEGACY (Tramite+Estatus+Maestro) para CPA: {}", contador.getCveIdCpa());
    }



/**
     * Registra la solicitud de modificación de datos (R1) en la tabla legacy.
     */
    private void guardarModificacionLegacy(NdtContadorPublicoAut contador, JsonNode json) {
        NdtR1DatosPersonales r1 = new NdtR1DatosPersonales();
        LocalDateTime fechaActual = LocalDateTime.now();

        // 1. Datos de Auditoría y Relación
        r1.setCveIdCpa(contador.getCveIdCpa());
        r1.setFecRegistroAlta(fechaActual);
        r1.setCveIdUsuario(contador.getCurp()); // Usuario que modifica

        // 2. Extraer datos del JSON (Basado en tu log)
        // El log mostraba una estructura: "datosContactoDto": { "correoElectronico1": "...", "cedulaprofesional": "..." }
        
        if (json.has("datosContactoDto")) {
            JsonNode contacto = json.get("datosContactoDto");

            // Cédula Profesional
            if (contacto.has("cedulaprofesional")) {
                r1.setCedulaProfesional(contacto.get("cedulaprofesional").asText());
            }
            
            // Correo (Usamos correoElectronico1 como principal)
            if (contacto.has("correoElectronico1")) {
                r1.setCorreoImss(contacto.get("correoElectronico1").asText());
            }
            
            // Teléfono
            if (contacto.has("telefono1")) {
                r1.setTelefonoImss(contacto.get("telefono1").asText());
            }
        }

        // Título (A veces viene en datosPersonalesDto o en raíz, ajusta según necesidad)
        if (json.has("datosPersonalesDto") && json.get("datosPersonalesDto").has("titulo")) {
             r1.setDesTituloExpedidoPor(json.get("datosPersonalesDto").get("titulo").asText());
        }

        // 3. Datos Obligatorios de Base de Datos (Claves Foráneas)
        // ⚠️ IMPORTANTE: La tabla NDT_R1_DATOS_PERSONALES requiere Subdelegación y Domicilio Fiscal.
        // Si no vienen en el JSON, usamos los del registro actual del contador o valores por defecto para que no falle el insert.
        // En tus INSERTS de ejemplo usaban 146 para subdelegación.
        
        // Intenta obtener del JSON, si no, usa valores por defecto o null (si la BD lo permite)
        if (json.has("datosPersonalesDto") && json.get("datosPersonalesDto").has("idSubdelegacion")) {
            r1.setCveIdSubdelegacion(json.get("datosPersonalesDto").get("idSubdelegacion").asLong());
        } else {
            r1.setCveIdSubdelegacion(146L); // Valor por defecto o recuperar del CPA actual si es posible
        }
        
        // ID Domicilio Fiscal (Requerido por BD Legacy)
        // Idealmente deberías buscar el ID del domicilio actual del contador. 
        // Por ahora ponemos el mismo ID del CPA como se vio en algunos de tus inserts antiguos, o 0.
        r1.setCveIdPfdomFiscal(contador.getCveIdPersona()); 

        // Guardar en BD
        datosPersonalesRepository.save(r1);
        logger.info("Modificación (R1) guardada en Legacy para CPA: {}", contador.getCveIdCpa());
    }    
    
}
 

