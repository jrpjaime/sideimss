package mx.gob.imss.contadores.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.EntityManager;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import mx.gob.imss.contadores.dto.*;
import mx.gob.imss.contadores.entity.*;
import mx.gob.imss.contadores.repository.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service("acreditacionMembresiaService")
@RequiredArgsConstructor
public class AcreditacionMembresiaServiceImpl implements AcreditacionMembresiaService {

    private static final Logger logger = LogManager.getLogger(AcreditacionMembresiaServiceImpl.class);

    @Value("${serviciosdigitales.url.correo}")
    private String urlSendCorreoElectronico;

    @Value("${sideimss.catalogos.microservice.url}")
    private String catalogosMicroserviceUrl;

    @Value("${sideimss.acuses.microservice.url}")
    private String acusesMicroserviceUrl;

    @Value("${documentos.microservice.url}")
    private String documentosMicroserviceUrl;

    private final NdtPlantillaDatoRepository ndtPlantillaDatoRepository;
    private final NdtContadorPublicoAutRepository contadorRepository;
    private final NdtCpaAcreditacionRepository acreditacionRepository;
    private final NdtR1DatosPersonalesRepository datosPersonalesRepository;
    private final NdtCpaEstatusRepository estatusRepository;
    private final NdtCpaTramiteRepository tramiteRepository;
    private final NdtDocumentoProbatorioRepository documentoProbatorioRepository;
    private final NdtR2DespachoRepository r2DespachoRepository;
    private final NdtR3ColegioRepository r3ColegioRepository;
    private final NdtR2FormaContactoRepository r2FormaContactoRepository;
    private final NdtFormaContactoRepository formaContactoRepository;
    private final NdtR1FormaContactoRepository r1FormaContactoRepository;
    
    private final WebClient webClient;
    private final TransactionTemplate transactionTemplate;

    private final EntityManager entityManager;

    @Override
    public Mono<DocumentoIndividualDto> cargarDocumentoAlmacenamiento(DocumentoIndividualDto documento, String jwtToken) {
        logger.info("Enviando documento '{}' al microservicio de documentos.", documento.getNomArchivo());
        return webClient.post()
            .uri(documentosMicroserviceUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .bodyValue(documento)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> response.bodyToMono(String.class)
                .flatMap(errorBody -> Mono.error(new RuntimeException("Error microservicio documentos: " + errorBody))))
            .bodyToMono(DocumentoIndividualDto.class)
            .onErrorResume(e -> {
                logger.error("Fallo al cargar documento: {}", e.getMessage());
                DocumentoIndividualDto errorDto = new DocumentoIndividualDto();
                errorDto.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
                errorDto.setMensaje("Fallo en procesamiento de documento: " + e.getMessage());
                return Mono.just(errorDto);
            });
    }

    @Override
    public NdtPlantillaDato guardarPlantillaDato(NdtPlantillaDato plantillaDato) {
        return ndtPlantillaDatoRepository.save(plantillaDato);
    }

    @Override
    public Mono<NdtPlantillaDato> obtenerSelloYGuardarPlantilla(NdtPlantillaDato ndtPlantillaDato, String jwtToken) {
        logger.info("Iniciando proceso reactivo de sellado, sincronización Legacy y Envío de Correo Final.");

        return Mono.just(ndtPlantillaDato).<NdtPlantillaDato>flatMap(dato -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(dato.getDesDatos());
                
                String initialCadena = rootNode.path("cadenaOriginal").asText("");
                String folioFirma = rootNode.path("folioFirma").asText("");

                if (initialCadena.isEmpty()) {
                    return Mono.<NdtPlantillaDato>error(new RuntimeException("Error: La cadenaOriginal no existe en los datos."));
                }

                String modifiedCadena = aplicarLogicaHash(initialCadena, folioFirma);

                CadenaOriginalRequestDto requestDto = new CadenaOriginalRequestDto();
                requestDto.setCadenaOriginal(modifiedCadena);
                requestDto.setRfc(dato.getDesRfc());
                requestDto.setNombreRazonSocial(rootNode.path("nombreCompleto").asText(null));
                requestDto.setCurp(rootNode.path("curp").asText(null));

                return webClient.post()
                    .uri(acusesMicroserviceUrl.trim() + "/generaSello")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .bodyValue(requestDto)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, resp -> resp.bodyToMono(String.class)
                        .flatMap(err -> Mono.error(new RuntimeException("Error Sello: " + err))))
                    .bodyToMono(SelloResponseDto.class)
                    .flatMap(selloDto -> {
                        if (selloDto.getCodigo() != 0 || selloDto.getSello() == null) {
                            return Mono.<NdtPlantillaDato>error(new RuntimeException("Error en sello: " + selloDto.getMensaje()));
                        }

                        // 1. PRIMERO: PERSISTENCIA (Si esto falla, hay rollback y NO hay correo)
                        return Mono.<NdtPlantillaDato>fromCallable(() -> {
                            return transactionTemplate.execute(status -> {
                                try {
                                    ObjectNode nodeFinal = (ObjectNode) objectMapper.readTree(dato.getDesDatos());
                                    nodeFinal.put("selloDigitalIMSS", selloDto.getSello());
                                    nodeFinal.put("cadenaOriginal", modifiedCadena);
                                    dato.setDesDatos(objectMapper.writeValueAsString(nodeFinal));

                                    NdtPlantillaDato guardada = ndtPlantillaDatoRepository.save(dato);
                                    sincronizarConLegacy(guardada, nodeFinal);

                                    return guardada;
                                } catch (Exception ex) {
                                    logger.error("Error en persistencia, Rollback activado: {}", ex.getMessage());
                                    status.setRollbackOnly();
                                    throw new RuntimeException(ex.getMessage());
                                }
                            });
                        })
                        .subscribeOn(Schedulers.boundedElastic())
                        // 2. SEGUNDO: ENVÍO DE CORREO (Solo si la persistencia fue exitosa)
                        .flatMap(guardada -> {
                            logger.info("Persistencia exitosa. Iniciando envío de correo de confirmación.");
                            return dispararEmailAutomatico(guardada, jwtToken)
                                   .thenReturn(guardada); // Retornamos la entidad guardada al final
                        });
                    });
            } catch (Exception e) {
                return Mono.<NdtPlantillaDato>error(new RuntimeException("Error procesamiento: " + e.getMessage()));
            }
        })
        .onErrorResume(e -> {
            logger.error("Fallo crítico en el trámite (No se envió correo): {}", e.getMessage());
            return Mono.error(new RuntimeException("No se pudo completar el trámite: " + e.getMessage()));
        });
    }

    /**
     * Identifica el tipo de trámite y dispara el correo correspondiente.
     */
    private Mono<String> dispararEmailAutomatico(NdtPlantillaDato plantilla, String jwtToken) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(plantilla.getDesDatos());
            String rfc = plantilla.getDesRfc();
            String nombre = root.path("nombreCompleto").asText("Contador Público");
            String tipoAcuse = plantilla.getDesTipoAcuse();

            if ("ACREDITACION_MEMBRESIA".equalsIgnoreCase(tipoAcuse)) {
                return enviarCorreoAcreditacion(rfc, nombre, jwtToken);
            } else if ("ACUSE_SOLICITUD_BAJA".equalsIgnoreCase(tipoAcuse)) {
                return enviarCorreoSolicitudBaja(rfc, nombre, jwtToken);
            } else if ("ACUSE_SOLICITUD_CAMBIO".equalsIgnoreCase(tipoAcuse)) {
                String subTipo = root.path("tipoSolicitud").asText("").toUpperCase();
                switch (subTipo) {
                    case "CONTACTO": return enviarCorreoModificacionDatosContacto(rfc, nombre, jwtToken);
                    case "DESPACHO": return enviarCorreoModificacionDatosDespacho(rfc, nombre, jwtToken);
                    case "COLEGIO":  return enviarCorreoModificacionDatosColegio(rfc, nombre, jwtToken);
                }
            }
            return Mono.just("Tipo de acuse no reconocido para correo.");
        } catch (Exception e) {
            logger.error("Error al preparar disparo de correo: {}", e.getMessage());
            return Mono.just("Error preparacion correo.");
        }
    }

    // --- LÓGICA DE SINCRONIZACIÓN LEGACY (Se mantiene igual) ---

    private void sincronizarConLegacy(NdtPlantillaDato plantilla, JsonNode rootNode) throws Exception {
        String curp = extraerCurp(rootNode);
        if (curp == null) return;

        NdtContadorPublicoAut contador = contadorRepository.findByCurp(curp)
            .orElseThrow(() -> new RuntimeException("Contador no encontrado: " + curp));

        String idPlantilla = plantilla.getCveIdPlantillaDato().toString();
        String folio = rootNode.path("numTramiteNotaria").asText(rootNode.path("folioFirma").asText("S/F"));
        String tipoAcuse = plantilla.getDesTipoAcuse();

        if ("ACREDITACION_MEMBRESIA".equalsIgnoreCase(tipoAcuse)) {
            guardarAcreditacionLegacy(contador, rootNode, folio, idPlantilla);
        } else if ("ACUSE_SOLICITUD_CAMBIO".equalsIgnoreCase(tipoAcuse)) {
            guardarModificacionLegacy(contador, rootNode, folio, idPlantilla);
        } else if ("ACUSE_SOLICITUD_BAJA".equalsIgnoreCase(tipoAcuse)) {
            guardarBajaLegacy(contador, rootNode, folio, idPlantilla);
        }
    }

    private String extraerCurp(JsonNode root) {
        if (root.has("curp")) return root.get("curp").asText();
        if (root.has("CURP")) return root.get("CURP").asText();
        if (root.has("datosPersonalesDto") && root.get("datosPersonalesDto").has("curp")) {
            return root.get("datosPersonalesDto").get("curp").asText();
        }
        return null;
    }

    private void guardarAcreditacionLegacy(NdtContadorPublicoAut contador, JsonNode json, String folio, String idPlantilla) {
        LocalDateTime ahora = LocalDateTime.now();
        String usr = contador.getCurp();

        NdtCpaTramite tr = new NdtCpaTramite();
        tr.setCveIdCpa(contador.getCveIdCpa());
        tr.setFecSolicitudMovimiento(ahora);
        tr.setFecRegistroAlta(ahora);
        tr.setCveIdUsuario(usr);
        tr.setNumTramiteNotaria(folio);
        tr.setUrlAcuseNotaria(idPlantilla);
        NdtCpaTramite trG = tramiteRepository.save(tr);

        NdtCpaAcreditacion acred = new NdtCpaAcreditacion();
        acred.setCveIdCpa(contador.getCveIdCpa());
        acred.setCveIdCpaTramite(trG.getCveIdCpaTramite());
        acred.setFecRegistroAlta(ahora);
        acred.setFecRegistroActualizado(ahora); 
        acred.setCveIdUsuario(usr);
        r3ColegioRepository.findByCveIdCpaAndFecRegistroBajaIsNull(contador.getCveIdCpa())
            .ifPresent(r3 -> acred.setCveIdColegio(r3.getCveIdColegio()));

        if (json.has("fechaExpedicionAcreditacion")) acred.setFecAcreditacionCp(parseFecha(json.get("fechaExpedicionAcreditacion").asText()));
        if (json.has("fechaExpedicionMembresia")) acred.setFecPresentacionAcreditacion(parseFecha(json.get("fechaExpedicionMembresia").asText()));
        acred.setIndAcredMembresia(null);
        acreditacionRepository.save(acred);

        if (json.has("desPathHdfsAcreditacion")) guardarDocumentoLegacy(contador.getCveIdCpa(), json.get("desPathHdfsAcreditacion").asText(), 133L, usr);
        if (json.has("desPathHdfsMembresia")) guardarDocumentoLegacy(contador.getCveIdCpa(), json.get("desPathHdfsMembresia").asText(), 132L, usr);
    }

    private void guardarBajaLegacy(NdtContadorPublicoAut contador, JsonNode json, String folio, String idPlantilla) {
        LocalDateTime ahora = LocalDateTime.now();
        String usr = contador.getCurp();

        NdtCpaTramite tr = new NdtCpaTramite();
        tr.setCveIdCpa(contador.getCveIdCpa());
        tr.setFecSolicitudMovimiento(ahora);
        tr.setFecRegistroAlta(ahora);
        tr.setCveIdUsuario(usr);
        tr.setNumTramiteNotaria(json.path("folioSolicitud").asText("S/F"));
        tr.setUrlAcuseNotaria(idPlantilla);
        NdtCpaTramite trG = tramiteRepository.save(tr);

        NdtCpaEstatus est = new NdtCpaEstatus();
        est.setCveIdCpa(contador.getCveIdCpa());
        est.setCveIdEstadoCpa(10L); 
        est.setFecBaja(ahora);
        est.setFecRegistroAlta(ahora);
        est.setCveIdUsuario(usr);
        est.setCveIdCpaTramite(trG.getCveIdCpaTramite());
        String mot = json.path("motivoBaja").asText("Baja desde Portal");
        est.setDesComentarios(mot.length() > 3100 ? mot.substring(0, 3100) : mot);
        estatusRepository.save(est);

        contador.setCveIdEstadoCpa(10L);
        contador.setFecRegistroBaja(ahora);
        contadorRepository.save(contador);
    }

    private void guardarModificacionLegacy(NdtContadorPublicoAut contador, JsonNode json, String folio, String idPlantilla) {
        LocalDateTime ahora = LocalDateTime.now();
        String usr = contador.getCurp();

        NdtCpaTramite tr = new NdtCpaTramite();
        tr.setCveIdCpa(contador.getCveIdCpa());
        tr.setFecSolicitudMovimiento(ahora);
        tr.setFecRegistroAlta(ahora);
        tr.setCveIdUsuario(usr);
        tr.setNumTramiteNotaria(folio);
        tr.setUrlAcuseNotaria(idPlantilla);
        NdtCpaTramite trG = tramiteRepository.save(tr);

        String tipo = json.path("tipoSolicitud").asText("").toUpperCase();
        JsonNode state = json.get("state");

        switch (tipo) {
            case "CONTACTO": sincronizarR1(contador, state, trG, usr); break;
            case "DESPACHO": sincronizarR2(contador, state, trG, usr); break;
            case "COLEGIO": sincronizarR3(contador, state, json, trG, usr); break;
        }
    }

    private void sincronizarR1(NdtContadorPublicoAut contador, JsonNode state, NdtCpaTramite tr, String usr) {
        LocalDateTime ahora = LocalDateTime.now();
        NdtR1DatosPersonales ant = datosPersonalesRepository.findRegistroActivoByCpa(contador.getCveIdCpa()).orElse(null);

        NdtR1DatosPersonales r1New = new NdtR1DatosPersonales();
        r1New.setCveIdCpa(contador.getCveIdCpa());
        r1New.setCveIdCpaTramite(tr.getCveIdCpaTramite());
        r1New.setFecRegistroAlta(ahora);
        r1New.setCveIdUsuario(usr);

        if (ant != null) {
            r1New.setCedulaProfesional(ant.getCedulaProfesional());
            r1New.setFecExpedicionCedprof(ant.getFecExpedicionCedprof());
            r1New.setCveIdSubdelegacion(ant.getCveIdSubdelegacion());
            r1New.setCveIdPfdomFiscal(ant.getCveIdPfdomFiscal());
            ant.setFecRegistroBaja(ahora);
            datosPersonalesRepository.save(ant);
        }

        NdtR1DatosPersonales g = datosPersonalesRepository.save(r1New);
        if (state != null) {
            vincularR1Contacto(g.getCveIdR1DatosPersonales(), state.path("nuevoCorreoElectronico2").asText(null), 1L, usr);
            vincularR1Contacto(g.getCveIdR1DatosPersonales(), state.path("nuevoCorreoElectronico3").asText(null), 1L, usr);
            vincularR1Contacto(g.getCveIdR1DatosPersonales(), state.path("nuevoTelefono2").asText(null), 2L, usr);
        }
    }

private void sincronizarR2(NdtContadorPublicoAut contador, JsonNode state, NdtCpaTramite tr, String usr) {
    logger.info(">>> [INICIO] Sincronizar R2 para Despacho (Persona Moral)");
    LocalDateTime ahora = LocalDateTime.now();

    // 1. Obtener registro previo para darlo de baja
    NdtR2Despacho ant = r2DespachoRepository.findRegistroActivoByCpa(contador.getCveIdCpa()).orElse(null);

    NdtR2Despacho r2New = new NdtR2Despacho();
    r2New.setCveIdCpa(contador.getCveIdCpa());
    r2New.setCveIdCpaTramite(tr.getCveIdCpaTramite());
    r2New.setFecRegistroAlta(ahora);
    r2New.setCveIdUsuario(usr);

    if (state != null) {
        long tipoSoc = state.path("selectedTipoSociedad").asLong();
        r2New.setIndTipoCpa(tipoSoc);

        if (tipoSoc == 1) { // ES UN DESPACHO
            String rfcDespacho = state.path("nuevoRfcDespacho").asText().trim().toUpperCase();
            logger.info(">>> Buscando Despacho con RFC: {}", rfcDespacho);
            
            // SQL optimizado usando solo las tablas que confirmaste que tienes
            String sqlLookup = "SELECT D.CVE_ID_DESPACHO " +
                               "FROM MGPBDTU9X.DIT_PERSONA_MORAL PM " +
                               "INNER JOIN MGPBDTU9X.NDT_DESPACHOS D ON D.CVE_ID_PERSONA_MORAL = PM.CVE_ID_PERSONA_MORAL " +
                               "WHERE PM.RFC = :rfc AND D.FEC_REGISTRO_BAJA IS NULL";

            try {
                // Ejecutamos la consulta para obtener el ID del Despacho
                Object result = entityManager.createNativeQuery(sqlLookup)
                        .setParameter("rfc", rfcDespacho)
                        .getSingleResult();

                // Si llega aquí, es porque encontró uno
                Long idDespacho = ((Number) result).longValue();
                r2New.setCveIdDespacho(idDespacho);
                logger.info(">>> Despacho encontrado ID: {}", idDespacho);
            } catch (jakarta.persistence.NoResultException e) {
                logger.error(">>> ERROR: No existe un registro en NDT_DESPACHOS para el RFC: {}", rfcDespacho);
                // Si no existe, podrías decidir si heredar el anterior o lanzar error
                 throw new RuntimeException("El despacho solicitado con RFC " + rfcDespacho + " no es válido o no se encuentra registrado.");
            } catch (Exception e) {
                logger.error(">>> Error en búsqueda de Despacho: {}", e.getMessage());
                throw new RuntimeException("Error al validar la información del despacho: " + e.getMessage());
            }

            r2New.setCargoQueDesempena(state.path("selectedCargoDesempena").asText(null));
            
            // IMPORTANTE: Como no tienes la tabla de domicilio PM, seteamos null o heredamos el anterior
            r2New.setCveIdPmdomFiscal(null); 

            NdtR2Despacho guardado = r2DespachoRepository.save(r2New);
            logger.info(">>> Registro R2 guardado exitosamente con ID: {}", guardado.getCveIdR2Despacho());

            // Vinculación de contacto (Teléfono)
            String tel = state.path("telefonoFijoDespacho").asText(null);
            if (tel != null && !tel.isEmpty()) {
                vincularR2Contacto(guardado.getCveIdR2Despacho(), tel, usr);
            }
        } else {
            // Lógica para Profesional Independiente (Tipo 2)
            r2New.setIndCuentaconTrab("Si".equalsIgnoreCase(state.path("tieneTrabajadores").asText()) ? "1" : "0");
            r2New.setNumTrabajadores(state.path("numeroTrabajadores").asInt(0));
            r2DespachoRepository.save(r2New);
            logger.info(">>> Guardado como Profesional Independiente.");
        }

        // 2. Dar de baja el registro anterior después de guardar el nuevo
        if (ant != null) {
            ant.setFecRegistroBaja(ahora);
            r2DespachoRepository.save(ant);
            logger.info(">>> Registro R2 anterior (ID: {}) marcado de baja.", ant.getCveIdR2Despacho());
        }
    }
}

private void sincronizarR3(NdtContadorPublicoAut contador, JsonNode state, JsonNode root, NdtCpaTramite tr, String usr) {
    logger.info(">>> [INICIO] sincronizarR3 - CPA ID: {}, Usuario: {}", contador.getCveIdCpa(), usr);
    LocalDateTime ahora = LocalDateTime.now();
    
    // 1. Buscar registro activo actual para pasarlo a historial (baja)
    NdtR3Colegio ant = r3ColegioRepository.findRegistroActivoByCpa(contador.getCveIdCpa()).orElse(null);
    logger.info(">>> Registro previo encontrado: {}", (ant != null ? "ID: " + ant.getCveIdR3Colegio() : "Ninguno"));

    // 2. Inicializar el nuevo registro de Colegio
    NdtR3Colegio r3New = new NdtR3Colegio();
    r3New.setCveIdCpa(contador.getCveIdCpa());
    r3New.setCveIdCpaTramite(tr.getCveIdCpaTramite());
    r3New.setFecRegistroAlta(ahora);
    r3New.setCveIdUsuario(usr);
    
    // Seteamos folios del trámite si están disponibles
    r3New.setNumTramiteNotaria(tr.getNumTramiteNotaria());
    r3New.setUrlAcuseNotaria(tr.getUrlAcuseNotaria());

    // 3. Procesar el documento (Constancia) si viene en el JSON
    if (root != null && root.has("desPathHdfsConstancia")) {
        String pathDoc = root.get("desPathHdfsConstancia").asText();
        logger.info(">>> Procesando constancia HDFS: {}", pathDoc);
        
        // Llamada al método que ahora retorna NdtDocumentoProbatorio
        NdtDocumentoProbatorio docGuardado = guardarDocumentoLegacy(contador.getCveIdCpa(), pathDoc, 132L, usr);
        
        if (docGuardado != null) {
            // Vinculamos el ID generado a la columna CVE_ID_DOCTO_PROBATORIO
            r3New.setCveIdDoctoProbatorio(docGuardado.getCveIdDoctoProbatorio());
            logger.info(">>> Documento vinculado con ID: {}", docGuardado.getCveIdDoctoProbatorio());
        }
    }

    // 4. Buscar el ID del nuevo Colegio por RFC (vía Persona Moral) 
    if (state != null && state.has("nuevoRfcColegio")) {
        String rfcColegio = state.get("nuevoRfcColegio").asText().trim().toUpperCase();
        
        // Consulta extendida para traer el ID del Colegio y el ID de su Domicilio Fiscal Activo
        String sqlColegio = "SELECT C.CVE_ID_COLEGIO, PDF.CVE_ID_PMDOM_FISCAL " +
                            "FROM MGPBDTU9X.NDT_COLEGIO C " +
                            "INNER JOIN MGPBDTU9X.DIT_PERSONA_MORAL PM ON C.CVE_ID_PERSONA_MORAL = PM.CVE_ID_PERSONA_MORAL " +
                            "LEFT JOIN MGPBDTU9X.DIT_PERSONAM_DOM_FISCAL PDF ON PM.CVE_ID_PERSONA_MORAL = PDF.CVE_ID_PERSONA_MORAL " +
                            "AND PDF.FEC_REGISTRO_BAJA IS NULL " + 
                            "WHERE PM.RFC = :rfc " +
                            "AND C.FEC_REGISTRO_BAJA IS NULL";

        try {
            Object[] result = (Object[]) entityManager.createNativeQuery(sqlColegio)
                    .setParameter("rfc", rfcColegio)
                    .getSingleResult();
            
            Long idColegio = ((Number) result[0]).longValue();
            Long idDomicilio = (result[1] != null) ? ((Number) result[1]).longValue() : null;

            r3New.setCveIdColegio(idColegio);
            

            if (idDomicilio == null) {
                logger.warn(">>> El Colegio {} existe pero no tiene un domicilio fiscal activo registrado.", rfcColegio);
                // Aquí decides si permites continuar con null o lanzas error
                throw new RuntimeException("El Colegio " + rfcColegio + " existe pero no tiene un domicilio fiscal activo.");
            }
            r3New.setCveIdPmdomFiscal(idDomicilio); 

            logger.info(">>> Vinculado a Colegio ID: {} con Domicilio ID: {}", idColegio, idDomicilio);

        } catch (jakarta.persistence.NoResultException e) {
            throw new RuntimeException("El Colegio con RFC " + rfcColegio + " no es válido o no está registrado.");
        } catch (Exception e) {
            throw new RuntimeException("Error al validar datos del Colegio: " + e.getMessage());
        }
    }else {
        // Si el trámite es de Colegio pero no trae el RFC nuevo, es un error de datos
        throw new RuntimeException("No se proporcionó el RFC del nuevo Colegio.");
    }


    // 6. Guardar el nuevo registro (Activo)
    r3ColegioRepository.save(r3New);
    logger.info(">>> Nuevo registro NDT_R3_COLEGIO guardado exitosamente.");

    // 7. Dar de baja el registro anterior (Histórico)
    if (ant != null) {
        ant.setFecRegistroBaja(ahora);
        r3ColegioRepository.save(ant);
        logger.info(">>> Registro anterior (ID: {}) marcado de BAJA.", ant.getCveIdR3Colegio());
    }
    
    logger.info(">>> [FIN] sincronizarR3 finalizado correctamente.");
}

    // --- MÉTODOS DE APOYO ---

    private String aplicarLogicaHash(String cadena, String folio) {
        if (folio == null || folio.isEmpty() || !cadena.contains("|HASH|")) return cadena;
        int idx = cadena.indexOf("|HASH|");
        int start = idx + 6;
        int end = cadena.indexOf("|", start);
        String oldHash = (end != -1) ? cadena.substring(start, end) : cadena.substring(start);
        return cadena.replace("|HASH|" + oldHash, "|HASH|" + folio + "|" + oldHash);
    }

    private void vincularR1Contacto(Long idR1, String val, Long tipo, String usr) {
        if (val == null || val.trim().isEmpty()) return;
        NdtFormaContacto fc = new NdtFormaContacto();
        fc.setDesFormaContacto(val);
        fc.setCveIdTipoContacto(tipo);
        fc.setFecRegistroAlta(LocalDateTime.now());
        NdtFormaContacto g = formaContactoRepository.save(fc);

        NdtR1FormaContacto rel = new NdtR1FormaContacto();
        rel.setCveIdR1DatosPersonales(idR1);
        rel.setCveIdFormaContacto(g.getCveIdFormaContacto());
        rel.setFecRegistroAlta(LocalDateTime.now());
        rel.setCveIdUsuario(usr);
        r1FormaContactoRepository.save(rel);
    }

    private void vincularR2Contacto(Long idR2, String tel, String usr) {
        NdtFormaContacto fc = new NdtFormaContacto();
        fc.setDesFormaContacto(tel);
        fc.setCveIdTipoContacto(2L);
        fc.setFecRegistroAlta(LocalDateTime.now());
        NdtFormaContacto g = formaContactoRepository.save(fc);

        NdtR2FormaContacto rel = new NdtR2FormaContacto();
        rel.setCveIdR2Despacho(idR2);
        rel.setCveIdFormaContacto(g.getCveIdFormaContacto());
        rel.setFecRegistroAlta(LocalDateTime.now());
        rel.setCveIdUsuario(usr);
        r2FormaContactoRepository.save(rel);
    }

 

    private NdtDocumentoProbatorio guardarDocumentoLegacy(Long idCpa, String url, Long tipo, String usr) {
    NdtDocumentoProbatorio doc = new NdtDocumentoProbatorio();
    doc.setCveIdCpa(idCpa);
    doc.setUrlDocumentoProb(url);
    doc.setCveIdDoctoProbPorTipo(tipo);
    doc.setFecRegistroAlta(LocalDateTime.now());
    doc.setCveIdUsuario(usr);
    
    // Al hacer save, Hibernate usa la secuencia que definiste y llena el campo cveIdDoctoProbatorio
    return documentoProbatorioRepository.save(doc);
}


    private LocalDateTime parseFecha(String f) {
        if (f == null || f.isEmpty()) return null;
        try { return LocalDate.parse(f, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay(); }
        catch (Exception e) { try { return LocalDate.parse(f).atStartOfDay(); } catch (Exception e2) { return null; } }
    }

    // --- MÉTODOS DE ENVÍO DE CORREO ---

    @Override
    public Mono<String> enviarCorreoAcreditacion(String rfc, String nom, String token) {
        return procesarEnvioCorreo(rfc, nom, token, "Constancia de acreditación/membresía", 
            "<p>Se le informa que la presentación de sus constancias de acreditación y membresía han sido recibidas.</p>");
    }

    @Override
    public Mono<String> enviarCorreoSolicitudBaja(String rfc, String nom, String token) {
        return procesarEnvioCorreo(rfc, nom, token, "SOLICITUD DE BAJA", 
            "<p>Se le informa que su solicitud de baja ha sido recibida satisfactoriamente.</p>");
    }

    @Override
    public Mono<String> enviarCorreoModificacionDatosContacto(String rfc, String nom, String token) {
        return procesarEnvioCorreo(rfc, nom, token, "Modificación de datos", 
            "<p>El aviso de modificación de datos personales (medios de contacto) ha sido recibido.</p>");
    }
    
    @Override public Mono<String> enviarCorreoModificacionDatosDespacho(String r, String n, String t) { 
        return procesarEnvioCorreo(r, n, t, "Modificación de datos", "<p>El aviso de modificación de datos del Despacho ha sido recibido.</p>"); 
    }
    @Override public Mono<String> enviarCorreoModificacionDatosColegio(String r, String n, String t) { 
        return procesarEnvioCorreo(r, n, t, "Modificación de datos", "<p>El aviso de modificación de datos del Colegio ha sido recibido.</p>"); 
    }

    private Mono<String> procesarEnvioCorreo(String rfc, String nom, String token, String asunto, String html) {
        return obtenerCorreoDeMediosContacto(rfc, token).flatMap(mail -> {
            if (mail == null || mail.isEmpty()) {
                logger.warn("No se puede enviar correo: destinatario vacío para RFC {}", rfc);
                return Mono.just("Sin destinatario");
            }
            CorreoDto dto = new CorreoDto();
            dto.setRemitente("tramites.cpa@imss.gob.mx");
            dto.setCorreoPara(Collections.singletonList(mail));
            dto.setAsunto(asunto);
            dto.setCuerpoCorreo(construirHtmlBase(nom, rfc, html));

            return webClient.post()
                .uri(urlSendCorreoElectronico)
                .bodyValue(dto)
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp -> resp.bodyToMono(String.class).flatMap(e -> Mono.error(new RuntimeException(e))))
                .toBodilessEntity()
                .thenReturn("Correo Enviado");
        }).onErrorResume(e -> {
            logger.error("Error al enviar correo (el tramite ya se guardo): {}", e.getMessage());
            return Mono.just("Continuando pese a error de correo.");
        });
    }

    private String construirHtmlBase(String n, String r, String h) {
        return "<html><body style='font-family: Arial, sans-serif;'>" +
               "<h3>Estimado(a) " + n + "</h3>" +
               h +
               "<br><p>Puede dar seguimiento a su trámite en: <a href='http://agqa.imss.gob.mx/escritorio/web/publico'>Portal SIDEIMSS</a></p>" +
               "<p style='font-size: 11px; color: #555;'>Este es un mensaje automático, por favor no responda.</p>" +
               "</body></html>";
    }

    private Mono<String> obtenerCorreoDeMediosContacto(String rfc, String token) {
        return webClient.get().uri(catalogosMicroserviceUrl.trim() + "/mediosContacto/" + rfc)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).retrieve()
            .bodyToMono(MediosContactoContadoresResponseDto.class)
            .map(res -> {
                if (res == null || res.getMedios() == null) return "";
                return res.getMedios().stream()
                    .filter(m -> "1".equals(m.getTipoContacto()))
                    .findFirst()
                    .map(MedioContactoContadoresDto::getDesFormaContacto)
                    .orElse("");
            })
            .onErrorResume(e -> Mono.just(""));
    }
}