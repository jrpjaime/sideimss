package mx.gob.imss.contadores.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import mx.gob.imss.contadores.dto.CadenaOriginalRequestDto;
import mx.gob.imss.contadores.dto.ColegioContadorDto;
import mx.gob.imss.contadores.dto.DatosContactoDto;
import mx.gob.imss.contadores.dto.DatosPersonalesDto;
import mx.gob.imss.contadores.dto.DomicilioFiscalDto;
import mx.gob.imss.contadores.dto.SelloResponseDto;
import mx.gob.imss.contadores.dto.SolicitudBajaDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.repository.NdtPlantillaDatoRepository;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;  

 
@Service("contadorPublicoAutorizadoService")
public class ContadorPublicoAutorizadoServiceImpl implements ContadorPublicoAutorizadoService {
    
    private static final Logger logger = LogManager.getLogger(ContadorPublicoAutorizadoServiceImpl.class);


        @Autowired
    private NdtPlantillaDatoRepository ndtPlantillaDatoRepository; // Necesitas inyectar el repositorio

    private final WebClient webClient;

    @Value("${sideimss.acuses.microservice.url}")
    private String acusesMicroserviceUrl;

    public ContadorPublicoAutorizadoServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public SolicitudBajaDto getDatosContador(String rfc) {
        logger.info("Iniciando consulta de datos del contador para RFC: {}", rfc);

        // --- SIMULACIÓN DE CONSULTA SQL ---
        /* * Aquí iría la lógica real de la base de datos, por ejemplo:
         * * String sql = "SELECT * FROM TBL_CONTADORES C "
         * + "JOIN TBL_DOMICILIOS D ON C.ID_DOMICILIO = D.ID_DOMICILIO "
         * + "WHERE C.RFC = ?";
         * * // Implementación de acceso a datos (JdbcTemplate, JPA, etc.)
         * Map<String, Object> resultadoDB = jdbcTemplate.queryForMap(sql, rfc);
         * * // Luego mapearías 'resultadoDB' a los DTOs.
         */
        
        // --- SIMULACIÓN CON DATOS DUMMY (MOCK) ---
        
        DatosPersonalesDto datosPersonales = new DatosPersonalesDto(
            rfc, 
            "SEF900101XYZ",
            "SÁNCHEZ",
            "FERNÁNDEZ",
            "LAURA",
            "1234567890",
            "ACTIVO",
            "DEL NORTE",
            "SUBDELEGACIÓN ZACATENCO"
        );

        DomicilioFiscalDto domicilioFiscal = new DomicilioFiscalDto(
            "AV. SIEMPRE VIVA",
            "150",
            "Piso 1",
            "CALLE DE LA PAZ",
            "CALLE DE LA GUERRA",
            "COLONIA CENTRO",
            "CIUDAD DE MÉXICO",
            "CUAUHTÉMOC",
            "CIUDAD DE MÉXICO",
            "06000"
        );

        DatosContactoDto datosContacto = new DatosContactoDto(
            "laura.sanchez@contador.com.mx",
            "5512345678",
            "laura.contacto@otroemail.com",
            null, // Correo 3 vacío
            "5598765432"
        );

        SolicitudBajaDto solicitud = new SolicitudBajaDto(
            "20251101-0001", // Folio de ejemplo
            datosPersonales,
            domicilioFiscal,
            datosContacto,
            "BAJA POR CAMBIO DE FIRMA FISCAL"
        );

        logger.info("Datos dummy generados exitosamente para RFC: {}", rfc);
        return solicitud;
    }


      @Override
    public Mono<NdtPlantillaDato> obtenerSelloYGuardarPlantilla(NdtPlantillaDato ndtPlantillaDato, String jwtToken) {
        logger.info("Iniciando proceso para obtener sello digital y guardar plantilla (Solicitud Baja).");

        final String datosJson = ndtPlantillaDato.getDesDatos();
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
                modifiedCadenaOriginal = initialCadenaOriginal;
            }
        } else {
            modifiedCadenaOriginal = initialCadenaOriginal;
        }

        CadenaOriginalRequestDto requestDto = new CadenaOriginalRequestDto();
        requestDto.setCadenaOriginal(modifiedCadenaOriginal);
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
                logger.error("Error HTTP {} al obtener sello digital de mssideimss-acuses (Solicitud Baja).", response.statusCode());
                return response.bodyToMono(String.class)
                    .flatMap(errorBody -> Mono.error(new RuntimeException(
                        "Error al generar el sello digital para Solicitud Baja (" + response.statusCode().value() + "): " + errorBody
                    )));
            })
            .bodyToMono(SelloResponseDto.class)
            .flatMap(selloResponseDto -> {
                if (selloResponseDto.getCodigo() == 0 && selloResponseDto.getSello() != null && !selloResponseDto.getSello().isEmpty()) {
                    logger.info("Sello digital obtenido exitosamente para Solicitud Baja.");
                    String selloDigitalIMSS = selloResponseDto.getSello();

                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(datosJson);
                        rootNode.put("selloDigitalIMSS", selloDigitalIMSS);
                        rootNode.put("cadenaOriginal", modifiedCadenaOriginal);

                        ndtPlantillaDato.setDesDatos(objectMapper.writeValueAsString(rootNode));
                        logger.info("Sello digital y cadenaOriginal (Solicitud Baja) insertados en desDatos. Datos actualizados: {}", ndtPlantillaDato.getDesDatos());
                    } catch (Exception e) {
                        logger.error("Error al insertar el sello digital/actualizar cadenaOriginal en el JSON de datos (Solicitud Baja): {}", e.getMessage(), e);
                        return Mono.error(new RuntimeException("Error al actualizar los datos con el sello digital para Solicitud Baja."));
                    }

                    logger.info("Guardando NdtPlantillaDato con sello digital (Solicitud Baja).");
                    return Mono.just(ndtPlantillaDatoRepository.save(ndtPlantillaDato));
                } else {
                    logger.error("El microservicio de acuses devolvió un error al generar el sello para Solicitud Baja: {} - {}", selloResponseDto.getCodigo(), selloResponseDto.getMensaje());
                    return Mono.error(new RuntimeException("Ocurrió un error al generar el sello para Solicitud Baja, por favor intente más tarde: " + selloResponseDto.getMensaje()));
                }
            })
            .onErrorResume(e -> {
                logger.error("Fallo completo al obtener el sello o guardar la plantilla para Solicitud Baja: {}", e.getMessage(), e);
                return Mono.error(new RuntimeException("Ocurrió un error al procesar la solicitud de baja, por favor intente más tarde: " + e.getMessage()));
            });
    }





    /**
     * Método para consultar el colegio vinculado a un contador. 
     *
     * @param rfcContador El RFC del contador a consultar.
     * @return Un objeto ColegioContadorDto con el RFC y nombre/razón social del colegio.
     */
    @Override
    public ColegioContadorDto getColegioByRfcContador(String rfcContador) {
        logger.info("Iniciando consulta de colegio para RFC de contador: {}", rfcContador);

        // --- SIMULACIÓN DE LÓGICA DE NEGOCIO / CONSULTA A BASE DE DATOS ---
        //  aquí consulta    base de datos para obtener el colegio
        // vinculado al RFC del contador. Esto podría implicar JOINS entre tablas.
        // Por ahora, simularemos con datos dummy basados en el RFC.

        if (rfcContador != null && rfcContador.startsWith("MOG")) {
            logger.info("Se encontró un colegio dummy para RFC: {}", rfcContador);
            return new ColegioContadorDto("RFCCOLEGIO123", "Colegio Nacional de Contadores Públicos AC");
     
        } else {
            logger.warn("No se encontró un colegio simulado para RFC: {}", rfcContador);
            // Podrías devolver null, o un DTO con campos vacíos, o lanzar una excepción si no se encuentra.
            // Para este ejemplo, devolvemos un DTO con valores por defecto.
            return new ColegioContadorDto("N/A", "Colegio no encontrado");
        }
    }

}