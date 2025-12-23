package mx.gob.imss.contadores.service;


import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import mx.gob.imss.contadores.dto.*;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.repository.NdtPatronDictamenRepository; 
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import org.springframework.http.HttpHeaders;  
import org.springframework.transaction.annotation.Transactional;

@Service("contadorPublicoAutorizadoService")
@RequiredArgsConstructor
public class ContadorPublicoAutorizadoServiceImpl implements ContadorPublicoAutorizadoService {
    
    private static final Logger logger = LogManager.getLogger(ContadorPublicoAutorizadoServiceImpl.class);

    // Definimos todos como final para que RequiredArgsConstructor genere el constructor correctamente
    private final NdtPatronDictamenRepository ndtPatronDictamenRepository;
    
    @PersistenceContext
    private final EntityManager entityManager;
    
    private final WebClient.Builder webClientBuilder;
    private final PlantillaPersistenceService persistenceService; 

    @Value("${sideimss.acuses.microservice.url}")
    private String acusesMicroserviceUrl;

@Override
public SolicitudBajaDto getDatosContador(String rfc) {
    logger.info("Consultando datos maestros para RFC: {}", rfc);

     
    String sqlFiscal = "SELECT DPER.RFC as RFC, DPER.CURP as CURP, DPER.NOM_PRIMER_APELLIDO as APEPAT, " +
            "DPER.NOM_SEGUNDO_APELLIDO as APEMAT, DPER.NOM_NOMBRE as NOMBRE, DDP.NUM_REGISTRO_CPA as REGISTRO, " +
            "ECP.DES_ESTADO_CPA as ESTATUS, DPC.DES_DELEG as DELEGACION, SU.DES_SUBDELEGACION as SUBDELEGACION, " +
            "UPPER(DS.CALLE) as CALLE, UPPER(DS.NUM_EXTERIOR) as NEXT, UPPER(DS.NUM_INTERIOR) as NINT, " +  
            "UPPER(DS.ENTRE_CALLE_1) as E1, UPPER(DS.ENTRE_CALLE_2) as E2, UPPER(DS.COLONIA) as COL, " +
            "UPPER(DS.LOCALIDAD) as LOC, UPPER(DS.MUNICIPIO) as MUN, UPPER(DS.ENTIDAD_FEDERATIVA) as ENT, DS.CODIGO as CP " +
            "FROM MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT DDP " +
            "INNER JOIN MGPBDTU9X.DIT_PERSONA DPER ON DPER.CVE_ID_PERSONA = DDP.CVE_ID_PERSONA " +
            "INNER JOIN MGPBDTU9X.NDT_R1_DATOS_PERSONALES RDP ON RDP.CVE_ID_CPA = DDP.CVE_ID_CPA " +
            "INNER JOIN MGPBDTU9X.DIT_PERSONAF_DOM_FISCAL PDF ON PDF.CVE_ID_PFDOM_FISCAL = RDP.CVE_ID_PFDOM_FISCAL " +
            "INNER JOIN MGPBDTU9X.DIT_DOMICILIO_SAT DS ON DS.CVE_ID_DOMICILIO = PDF.CVE_ID_DOMICILIO " +
            "INNER JOIN MGPBDTU9X.NDC_ESTADO_CPA ECP ON DDP.CVE_ID_ESTADO_CPA = ECP.CVE_ID_ESTADO_CPA " +
            "LEFT JOIN MGPBDTU9X.DIC_SUBDELEGACION SU ON RDP.CVE_ID_SUBDELEGACION = SU.CVE_ID_SUBDELEGACION " +
            "LEFT JOIN MGPBDTU9X.DIC_DELEGACION DPC ON SU.CVE_ID_DELEGACION = DPC.CVE_ID_DELEGACION " +
            "WHERE DPER.RFC = :rfc AND RDP.FEC_REGISTRO_BAJA IS NULL";

    List<Tuple> fiscalResult = entityManager.createNativeQuery(sqlFiscal, Tuple.class)
            .setParameter("rfc", rfc)
            .getResultList();

    if (fiscalResult.isEmpty()) throw new RuntimeException("No se encontró el contador con RFC: " + rfc);
    Tuple row = fiscalResult.get(0);

    // 2. CORRECCIÓN DEL MAPEÓ: Usar alias en MAYÚSCULAS
    DatosPersonalesDto personales = new DatosPersonalesDto(
        row.get("RFC", String.class), 
        row.get("CURP", String.class), 
        row.get("APEPAT", String.class),
        row.get("APEMAT", String.class), 
        row.get("NOMBRE", String.class), 
        String.valueOf(row.get("REGISTRO")),
        row.get("ESTATUS", String.class), 
        row.get("DELEGACION", String.class), 
        row.get("SUBDELEGACION", String.class)
    );


    logger.info("VALOR DE E2 EN TUPLE: " + row.get("E2"));
    // Mapeo de Domicilio con los 10 campos del constructor
DomicilioFiscalDto domicilio = new DomicilioFiscalDto(
    row.get("CALLE") != null ? row.get("CALLE").toString().trim() : "",
    row.get("NEXT") != null ? row.get("NEXT").toString().trim() : "",
    row.get("NINT") != null ? row.get("NINT").toString().trim() : "",
    row.get("E1") != null ? row.get("E1").toString().trim() : "",
    row.get("E2") != null ? row.get("E2").toString().trim() : "",  
    row.get("COL") != null ? row.get("COL").toString().trim() : "",
    row.get("LOC") != null ? row.get("LOC").toString().trim() : "",
    row.get("MUN") != null ? row.get("MUN").toString().trim() : "",
    row.get("ENT") != null ? row.get("ENT").toString().trim() : "",
    row.get("CP") != null ? row.get("CP").toString().trim() : ""
);
 logger.info("domicilio: " + domicilio);
    DatosContactoDto contacto = obtenerContactosUnificados(rfc);

    return new SolicitudBajaDto(null, personales, domicilio, contacto, null);
}

    private DatosContactoDto obtenerContactosUnificados(String rfc) {
        DatosContactoDto dto = new DatosContactoDto();
        
        // 1. FUENTE: IMSS DIGITAL (Correo 1 y Tel 1)
        String sqlImss = "SELECT fc.cve_id_tipo_contacto as tipo, fc.des_forma_contacto as valor " +
                "FROM MGPBDTU9X.Dit_Personaf_Contacto PFC " +
                "INNER JOIN MGPBDTU9X.DIT_FORMA_CONTACTO FC ON PFC.CVE_ID_FORMA_CONTACTO = FC.CVE_ID_FORMA_CONTACTO " +
                "INNER JOIN MGPBDTU9X.DIT_LLAVE_PERSONA DP on DP.Cve_Id_Persona = Pfc.Cve_Id_Persona " +
                "WHERE DP.RFC = :rfc AND FC.FEC_REGISTRO_BAJA IS NULL AND FC.CVE_ID_TIPO_CONTACTO IN (1,2)";
        
        List<Tuple> resImss = entityManager.createNativeQuery(sqlImss, Tuple.class).setParameter("rfc", rfc).getResultList();
        for (Tuple t : resImss) {
            int tipo = ((Number) t.get("tipo")).intValue();
            if (tipo == 1){ 
                dto.setCorreoElectronico1(t.get("valor", String.class));
            } else if (tipo == 2) {
                dto.setTelefono1(t.get("valor", String.class));
            }
        }

        // 2. FUENTE: SIDEIMSS (Correo 2, 3 y Tel 2)
        String sqlSide = "Select fc.cve_id_tipo_contacto as tipo, fc.des_forma_contacto as valor, R.CEDULA_PROFESIONAL as cedula " +
                "FROM MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT CPA " +
                "INNER JOIN MGPBDTU9X.DIT_LLAVE_PERSONA DP ON cpa.cve_id_persona = Dp.cve_id_persona " +
                "INNER JOIN MGPBDTU9X.NDT_R1_DATOS_PERSONALES R ON CPA.CVE_ID_CPA = R.CVE_ID_CPA " +
                "INNER JOIN MGPBDTU9X.NDT_R1_FORMACONTACTO F ON F.CVE_ID_R1_DATOS_PERSONALES = R.CVE_ID_R1_DATOS_PERSONALES " +
                "INNER JOIN MGPBDTU9X.NDT_FORMA_CONTACTO FC ON FC.CVE_ID_FORMA_CONTACTO = F.CVE_ID_FORMA_CONTACTO " +
                "WHERE DP.RFC = :rfc AND R.fec_registro_baja is null ORDER BY F.CVE_ID_FORMA_CONTACTO ASC";

        List<Tuple> resSide = entityManager.createNativeQuery(sqlSide, Tuple.class).setParameter("rfc", rfc).getResultList();
        int emailCount = 0;
        for (Tuple t : resSide) {
            int tipo = ((Number) t.get("tipo")).intValue();
            String valor = t.get("valor", String.class);
            if (t.get("cedula") != null) dto.setCedulaprofesional(t.get("cedula", String.class));

            if (tipo == 1) { // Emails adicionales
                emailCount++;
                if (emailCount == 1) dto.setCorreoElectronico2(valor);
                else if (emailCount == 2) dto.setCorreoElectronico3(valor);
            } else if (tipo == 2) { // Teléfono adicional
                dto.setTelefono2(valor != null ? valor.replace("|", "") : "");
            }
        }
        return dto;
    }

    @Override
    public Mono<NdtPlantillaDato> obtenerSelloYGuardarPlantilla(NdtPlantillaDato ndtPlantillaDato, String jwtToken) {
        return Mono.fromCallable(() -> prepararRequest(ndtPlantillaDato))
            .subscribeOn(Schedulers.boundedElastic())
            .<NdtPlantillaDato>flatMap(request -> 
                llamarServicioSello(request, jwtToken)
                    .flatMap(selloDto -> 
                        Mono.fromCallable(() -> 
                            persistenceService.actualizarYGuardar(ndtPlantillaDato, selloDto.getSello(), request.getCadenaOriginal(), ndtPlantillaDato.getDesDatos())
                        ).subscribeOn(Schedulers.boundedElastic())
                    )
            )
            .onErrorMap(e -> new RuntimeException("Error en proceso de sellado: " + e.getMessage()));
    }

    private CadenaOriginalRequestDto prepararRequest(NdtPlantillaDato dato) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(dato.getDesDatos());
        String cadena = root.path("cadenaOriginal").asText();
        String folio = root.path("folioFirma").asText();

        if (cadena.isEmpty()) throw new RuntimeException("La cadena original es requerida para el sellado.");

        if (folio != null && !folio.isEmpty() && cadena.contains("|HASH|")) {
            int idx = cadena.indexOf("|HASH|");
            int start = idx + 6;
            int end = cadena.indexOf("|", start);
            String oldHash = (end != -1) ? cadena.substring(start, end) : cadena.substring(start);
            cadena = cadena.replace("|HASH|" + oldHash, "|HASH|" + folio + "|" + oldHash);
        }

        CadenaOriginalRequestDto req = new CadenaOriginalRequestDto();
        req.setCadenaOriginal(cadena);
        req.setRfc(dato.getDesRfc());
        req.setNombreRazonSocial(root.path("nombreCompleto").asText(null));
        req.setCurp(root.path("curp").asText(null));
        return req;
    }

    private Mono<SelloResponseDto> llamarServicioSello(CadenaOriginalRequestDto req, String token) {
        return webClientBuilder.build().post()
            .uri(acusesMicroserviceUrl.trim() + "/generaSello")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .bodyValue(req)
            .retrieve()
            .onStatus(HttpStatusCode::isError, r -> r.bodyToMono(String.class).flatMap(b -> Mono.error(new RuntimeException(b))))
            .bodyToMono(SelloResponseDto.class)
            .flatMap(res -> res.getCodigo() == 0 ? Mono.just(res) : Mono.error(new RuntimeException(res.getMensaje())));
    }

@Transactional(readOnly = true)
@Override
public ColegioContadorDto getColegioByRfcContador(String rfc) {
    logger.info("Consultando colegio para RFC: {}", rfc);

    // Usamos la lógica de tu query manual que ya probaste y funciona
    String sql = "SELECT PM.RFC as RFC_COLEGIO, PM.DENOMINACION_RAZON_SOCIAL as NOM_COLEGIO " +
                 "FROM MGPBDTU9X.NDT_CONTADOR_PUBLICO_AUT DDP " +
                 "INNER JOIN MGPBDTU9X.DIT_PERSONA DPER ON DPER.CVE_ID_PERSONA = DDP.CVE_ID_PERSONA " +
                 "INNER JOIN MGPBDTU9X.Ndt_R3_colegio RC ON RC.CVE_ID_CPA = DDP.CVE_ID_CPA " +
                 "INNER JOIN MGPBDTU9X.Ndt_colegio C ON C.CVE_ID_COLEGIO = RC.CVE_ID_COLEGIO " +
                 "INNER JOIN MGPBDTU9X.DIT_PERSONA_MORAL PM ON C.CVE_ID_PERSONA_MORAL = PM.CVE_ID_PERSONA_MORAL " +
                 "WHERE DPER.RFC = :rfc " +
                 "AND RC.FEC_REGISTRO_BAJA IS NULL " + 
                 "AND DPER.FEC_REGISTRO_BAJA IS NULL"; // Filtro extra de seguridad para la persona

    try {
        // Ejecutamos la query nativa usando Tuple
        List<Tuple> res = entityManager.createNativeQuery(sql, Tuple.class)
                .setParameter("rfc", rfc)
                .getResultList();

        if (res.isEmpty()) {
            logger.warn("No se encontró colegio activo para el RFC: {}", rfc);
            return new ColegioContadorDto("N/A", "Sin colegio vinculado");
        }

        // Tomamos el primer registro (en caso de que la duplicidad en DIT_PERSONA persista)
        Tuple row = res.get(0);
        
         
        String rfcCol = row.get("RFC_COLEGIO") != null ? row.get("RFC_COLEGIO").toString().trim() : "";
        String nomCol = row.get("NOM_COLEGIO") != null ? row.get("NOM_COLEGIO").toString().trim() : "";

        logger.info("Colegio recuperado: {} - {}", rfcCol, nomCol);
        
        return new ColegioContadorDto(rfcCol, nomCol);

    } catch (Exception e) {
        logger.error("Error crítico al obtener el colegio para RFC {}: {}", rfc, e.getMessage());
        return new ColegioContadorDto("N/A", "Error de conexión con la base de datos");
    }
}

    @Transactional(readOnly = true)
    @Override
    public Boolean tieneDictamenEnProceso(Integer numRegistroCpa) {
        return ndtPatronDictamenRepository.countDictamenesPorRegistroCpa(numRegistroCpa) > 0;
    }
}