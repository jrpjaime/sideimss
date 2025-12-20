package mx.gob.imss.contadores.service;


import java.util.List;

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

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import mx.gob.imss.contadores.dto.CadenaOriginalRequestDto;
import mx.gob.imss.contadores.dto.ColegioContadorDto;
import mx.gob.imss.contadores.dto.DatosContactoDto;
import mx.gob.imss.contadores.dto.DatosPersonalesDto;
import mx.gob.imss.contadores.dto.DomicilioFiscalDto;
import mx.gob.imss.contadores.dto.SelloResponseDto;
import mx.gob.imss.contadores.dto.SolicitudBajaDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.repository.NdtPatronDictamenRepository;
import mx.gob.imss.contadores.repository.NdtPlantillaDatoRepository;
import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;  

 
import jakarta.persistence.Query;

 
@Service("contadorPublicoAutorizadoService")
public class ContadorPublicoAutorizadoServiceImpl implements ContadorPublicoAutorizadoService {
    
    private static final Logger logger = LogManager.getLogger(ContadorPublicoAutorizadoServiceImpl.class);

 
    @Autowired
    private NdtPatronDictamenRepository ndtPatronDictamenRepository;
   
    @Autowired
    private NdtPlantillaDatoRepository ndtPlantillaDatoRepository;  

    private final WebClient webClient;

    @Value("${sideimss.acuses.microservice.url}")
    private String acusesMicroserviceUrl;

    public ContadorPublicoAutorizadoServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }



        @PersistenceContext
    private EntityManager entityManager;

    @Override
    public SolicitudBajaDto getDatosContador(String rfc) {
        logger.info("Consultando base de datos para RFC: {}", rfc);

        // --- 1. CONSULTA DATOS PERSONALES Y FISCALES ---
        String sqlFiscal = "SELECT DPER.RFC, DPER.CURP, DPER.NOM_PRIMER_APELLIDO, DPER.NOM_SEGUNDO_APELLIDO, DPER.NOM_NOMBRE, " +
                "DDP.NUM_REGISTRO_CPA, ECP.DES_ESTADO_CPA, DPC.DES_DELEG, SU.DES_SUBDELEGACION, " +
                "UPPER(DS.CALLE) as CALLE, UPPER(DS.NUM_EXTERIOR) as NUM_EXT, UPPER(DS.NUM_INTERIOR) as NUM_INT, " +
                "UPPER(DS.ENTRE_CALLE_1) as ENTRE1, UPPER(DS.ENTRE_CALLE_2) as ENTRE2, UPPER(DS.COLONIA) as COLONIA, " +
                "UPPER(DS.LOCALIDAD) as LOCALIDAD, UPPER(DS.MUNICIPIO) as MUNICIPIO, UPPER(DS.ENTIDAD_FEDERATIVA) as ENTIDAD, DS.CODIGO " +
                "FROM MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT DDP " +
                "INNER JOIN MGPBDTU9X.DIT_PERSONA DPER ON DPER.CVE_ID_PERSONA = DDP.CVE_ID_PERSONA " +
                "INNER JOIN MGPBDTU9X.NDT_R1_DATOS_PERSONALES RDP ON RDP.CVE_ID_CPA = DDP.CVE_ID_CPA " +
                "INNER JOIN MGPBDTU9X.DIT_PERSONAF_DOM_FISCAL PDF ON PDF.CVE_ID_PFDOM_FISCAL = RDP.CVE_ID_PFDOM_FISCAL " +
                "INNER JOIN MGPBDTU9X.DIT_DOMICILIO_SAT DS ON DS.CVE_ID_DOMICILIO = PDF.CVE_ID_DOMICILIO " +
                "INNER JOIN MGPBDTU9X.NDC_ESTADO_CPA ECP ON DDP.CVE_ID_ESTADO_CPA = ECP.CVE_ID_ESTADO_CPA " +
                "LEFT OUTER JOIN MGPBDTU9X.DIC_SUBDELEGACION SU ON RDP.CVE_ID_SUBDELEGACION = SU.CVE_ID_SUBDELEGACION " +
                "LEFT OUTER JOIN MGPBDTU9X.DIC_DELEGACION DPC ON SU.CVE_ID_DELEGACION = DPC.CVE_ID_DELEGACION " +
                "WHERE DPER.RFC = :rfc AND RDP.FEC_REGISTRO_BAJA IS NULL";

        Query qFiscal = entityManager.createNativeQuery(sqlFiscal);
        qFiscal.setParameter("rfc", rfc);
        Object[] resFiscal = (Object[]) qFiscal.getSingleResult();

        DatosPersonalesDto personales = new DatosPersonalesDto(
            (String) resFiscal[0], (String) resFiscal[1], (String) resFiscal[2], (String) resFiscal[3],
            (String) resFiscal[4], String.valueOf(resFiscal[5]), (String) resFiscal[6], (String) resFiscal[7], (String) resFiscal[8]
        );

        DomicilioFiscalDto domicilio = new DomicilioFiscalDto(
            (String) resFiscal[9], (String) resFiscal[10], (String) resFiscal[11], (String) resFiscal[12],
            (String) resFiscal[13], (String) resFiscal[14], (String) resFiscal[15], (String) resFiscal[16],
            (String) resFiscal[17], (String) resFiscal[18]
        );

        // --- 2. CONSULTA CONTACTO (IMSS DIGITAL Y SIDEIMSS) ---
        DatosContactoDto contacto = new DatosContactoDto();
        
        // IMSS Digital (Correo 1 y Tel 1)
        String sqlImssDig = "SELECT fc.cve_id_tipo_contacto, fc.des_forma_contacto FROM MGPBDTU9X.Dit_Persona_Fisica P " +
                "INNER JOIN MGPBDTU9X.Dit_Personaf_Contacto PFC on P.Cve_Id_Persona = Pfc.Cve_Id_Persona " +
                "INNER JOIN MGPBDTU9X.DIT_FORMA_CONTACTO FC ON PFC.CVE_ID_FORMA_CONTACTO = FC.CVE_ID_FORMA_CONTACTO " +
                "INNER JOIN MGPBDTU9X.DIT_LLAVE_PERSONA DP on DP.Cve_Id_Persona = Pfc.Cve_Id_Persona " +
                "WHERE DP.RFC = :rfc AND FC.FEC_REGISTRO_BAJA IS NULL AND FC.CVE_ID_TIPO_CONTACTO IN (1,2)";
        
        List<Object[]> resImssDig = entityManager.createNativeQuery(sqlImssDig).setParameter("rfc", rfc).getResultList();
        for (Object[] row : resImssDig) {
            int tipo = ((Number) row[0]).intValue();
            String valor = (String) row[1];
            
            // TIPO 1 es Correo, TIPO 2 es Teléfono
            if (tipo == 1) {
                contacto.setCorreoElectronico1(valor);
            } else if (tipo == 2) {
                contacto.setTelefono1(valor);
            }
        }

        // SIDEIMSS (Correo 2, 3, Tel 2 y Cédula)
        String sqlSideimss = "Select fc.cve_id_tipo_contacto, fc.des_forma_contacto, R.CEDULA_PROFESIONAL " +
                "FROM MGPBDTU9X.Ndt_Contador_Publico_Aut CPA " +
                "INNER JOIN MGPBDTU9X.DIT_LLAVE_PERSONA DP ON cpa.cve_id_persona = Dp.cve_id_persona " +
                "INNER JOIN MGPBDTU9X.NDT_R1_DATOS_PERSONALES R ON CPA.CVE_ID_CPA = R.CVE_ID_CPA " +
                "INNER JOIN MGPBDTU9X.NDT_R1_FORMACONTACTO F ON F.CVE_ID_R1_DATOS_PERSONALES = R.CVE_ID_R1_DATOS_PERSONALES " +
                "INNER JOIN MGPBDTU9X.NDT_FORMA_CONTACTO FC ON FC.CVE_ID_FORMA_CONTACTO = F.CVE_ID_FORMA_CONTACTO " +
                "WHERE DP.RFC = :rfc AND R.fec_registro_baja is null ORDER BY F.CVE_ID_FORMA_CONTACTO,  F.CVE_ID_R1_DATOS_PERSONALES DESC";

            List<Object[]> resSide = entityManager.createNativeQuery(sqlSideimss).setParameter("rfc", rfc).getResultList();
            int emailIdx = 0;

            for (Object[] row : resSide) {
                int tipo = ((Number) row[0]).intValue();
                String valor = (String) row[1];
                
                if (row[2] != null) {
                    contacto.setCedulaprofesional((String) row[2]);
                }
                
                // TIPO 1 = CORREO  
                if (tipo == 1) { 
                    emailIdx++;
                    if (emailIdx == 1) {
                        contacto.setCorreoElectronico2(valor);
                    } else if (emailIdx == 2) {
                        contacto.setCorreoElectronico3(valor);
                    }
                } 
                // TIPO 2 = TELÉFONO  
                else if (tipo == 2) { 
                    // Opcional: limpiar los pipes '|' si solo necesitas los números
                    if (valor != null) {
                        valor = valor.replace("|", ""); 
                    }
                    contacto.setTelefono2(valor);
                }
            }

        return new SolicitudBajaDto(null, personales, domicilio, contacto, null);
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






        @Override
    public ColegioContadorDto getColegioByRfcContador(String rfcContador) {
        String sql = "SELECT PM.RFC, PM.DENOMINACION_RAZON_SOCIAL " +
                "FROM MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT DDP " +
                "INNER JOIN MGPBDTU9X.DIT_PERSONA DPER ON DPER.CVE_ID_PERSONA = DDP.CVE_ID_PERSONA " +
                "INNER JOIN MGPBDTU9X.Ndt_R3_colegio RC ON RC.CVE_ID_CPA = DDP.CVE_ID_CPA " +
                "INNER JOIN MGPBDTU9X.Ndt_colegio C ON C.CVE_ID_COLEGIO = RC.CVE_ID_COLEGIO " +
                "INNER JOIN MGPBDTU9X.DIT_PERSONA_MORAL PM ON C.CVE_ID_PERSONA_MORAL = PM.CVE_ID_PERSONA_MORAL " +
                "WHERE DPER.RFC = :rfc AND RC.FEC_REGISTRO_BAJA IS NULL";
        
        try {
            Object[] row = (Object[]) entityManager.createNativeQuery(sql)
                    .setParameter("rfc", rfcContador)
                    .getSingleResult();
            return new ColegioContadorDto((String) row[0], (String) row[1]);
        } catch (Exception e) {
            return new ColegioContadorDto("N/A", "No se encontró colegio vinculado");
        }
    }


 @Override
public Boolean tieneDictamenEnProceso(Integer numRegistroCpa) {
    logger.info("Verificando existencia de dictamenes para CPA: {}", numRegistroCpa);
    
    // Ejecuta el query ligero
    int cantidad = ndtPatronDictamenRepository.countDictamenesPorRegistroCpa(numRegistroCpa);
    
    // Retorna true si encontró al menos 1
    return cantidad > 0;
}

}