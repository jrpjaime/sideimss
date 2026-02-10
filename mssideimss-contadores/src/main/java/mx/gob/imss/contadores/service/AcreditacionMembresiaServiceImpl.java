package mx.gob.imss.contadores.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import mx.gob.imss.contadores.dto.*;
import mx.gob.imss.contadores.entity.*;
import mx.gob.imss.contadores.repository.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

/**
 * Servicio profesional para la gestión de Acreditación y Membresía.
 * Mantiene la integridad del modelo Legacy y gestiona notificaciones con adjuntos.
 */
@Service("acreditacionMembresiaService")
@RequiredArgsConstructor
public class AcreditacionMembresiaServiceImpl implements AcreditacionMembresiaService {

    private static final Logger logger = LogManager.getLogger(AcreditacionMembresiaServiceImpl.class);

    // Inyección de Repositorios (Funcionalidad Completa)
    private final NdtCpaTramiteEstadoRepository tramiteEstadoRepository;
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

    @Value("${serviciosdigitales.url.correo}")
    private String urlSendCorreoElectronico;
    @Value("${sideimss.catalogos.microservice.url}")
    private String catalogosMicroserviceUrl;
    @Value("${sideimss.acuses.microservice.url}")
    private String acusesMicroserviceUrl;
    @Value("${documentos.microservice.url}")
    private String documentosMicroserviceUrl;

    /**
     * Sube documentos al almacenamiento HDFS.
     */
    @Override
    public Mono<DocumentoIndividualDto> cargarDocumentoAlmacenamiento(DocumentoIndividualDto documento, String jwtToken) {
        return webClient.post()
            .uri(documentosMicroserviceUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .bodyValue(documento)
            .retrieve()
            .bodyToMono(DocumentoIndividualDto.class)
            .onErrorResume(e -> Mono.just(new DocumentoIndividualDto()));
    }

    /**
     * Persiste la plantilla de datos inicial.
     */
    @Override
    public NdtPlantillaDato guardarPlantillaDato(NdtPlantillaDato plantillaDato) {
        return ndtPlantillaDatoRepository.save(plantillaDato);
    }

    /**
     * Flujo de Sello Digital y Persistencia Legacy.
     */
    @Override
    public Mono<NdtPlantillaDato> obtenerSelloYGuardarPlantilla(NdtPlantillaDato ndtPlantillaDato, String jwtToken) {
        return Mono.just(ndtPlantillaDato).<NdtPlantillaDato>flatMap(dato -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(dato.getDesDatos());
                String initialCadena = rootNode.path("cadenaOriginal").asText("");
                String folioFirma = rootNode.path("folioFirma").asText("");

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
                    .bodyToMono(SelloResponseDto.class)
                    .flatMap(selloDto -> {
                        return Mono.fromCallable(() -> {
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
                                    status.setRollbackOnly();
                                    throw new RuntimeException(ex.getMessage());
                                }
                            });
                        }).subscribeOn(Schedulers.boundedElastic())
                        .flatMap(guardada -> dispararEmailAutomatico(guardada, jwtToken).thenReturn(guardada));
                    });
            } catch (Exception e) {
                return Mono.error(new RuntimeException(e.getMessage()));
            }
        });
    }

/**
 * Descarga el PDF del acuse y dispara el envío del correo electrónico.
 * Se utiliza un Map para la petición de descarga para asegurar compatibilidad 
 * con el microservicio de Acuses (evitando el error 404 por DTO incompleto).
 */
private Mono<String> dispararEmailAutomatico(NdtPlantillaDato plantilla, String jwtToken) {
    try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(plantilla.getDesDatos());
        
        String rfc = plantilla.getDesRfc();
        String nombre = root.path("nombreCompleto").asText("Contador Público");
        String tipoAcuse = plantilla.getDesTipoAcuse();
        String folio = root.path("numTramiteNotaria").asText("S-F");

        // Configuración de textos del correo (tu lógica actual se mantiene)
        String asuntoMsg = "Aviso de Trámite SIDEIMSS";
        String htmlMsg = "<p>Su trámite ha sido recibido satisfactoriamente.</p>";
        if ("ACREDITACION_MEMBRESIA".equalsIgnoreCase(tipoAcuse)) {
            asuntoMsg = "Constancia de acreditación/membresía";
            htmlMsg = "<p>Se le informa que la presentación de sus constancias han sido recibidas.</p>";
        } else if ("ACUSE_SOLICITUD_BAJA".equalsIgnoreCase(tipoAcuse)) {
            asuntoMsg = "SOLICITUD DE BAJA";
            htmlMsg = "<p>Su solicitud de baja ha sido recibida satisfactoriamente.</p>";
        }

        final String asuntoFinal = asuntoMsg;
        final String htmlFinal = htmlMsg;

        // --- SOLUCIÓN: GENERAR EL PDF USANDO EL JSON ---
        // En lugar de buscar por URL (que da 404), usamos el endpoint de Preview
        // que genera los bytes directamente del JSON que ya tenemos.
        
        ObjectNode requestGenerate = mapper.createObjectNode();
        requestGenerate.put("nomDocumento", root.path("nomDocumento").asText());
        requestGenerate.put("desVersion", root.path("desVersion").asText());
        requestGenerate.put("tipoAcuse", tipoAcuse);
        requestGenerate.put("datosJson", plantilla.getDesDatos()); // Aquí va todo el JSON con firmas y sellos

        return webClient.post()
            .uri(acusesMicroserviceUrl.trim() + "/descargarAcusePreview") // Cambiado a Preview
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestGenerate) 
            .retrieve()
            .bodyToMono(byte[].class) 
            .flatMap(pdfBytes -> {
                String nombreFichero = "Acuse_" + folio.replace("-", "_") + ".pdf";
                logger.info("PDF generado exitosamente para adjuntar al correo de {}", rfc);
                return procesarEnvioCorreo(rfc, nombre, jwtToken, asuntoFinal, htmlFinal, pdfBytes, nombreFichero);
            })
            .onErrorResume(e -> {
                logger.error("Error al generar PDF para el correo (enviando sin adjunto): {}", e.getMessage());
                return procesarEnvioCorreo(rfc, nombre, jwtToken, asuntoFinal, htmlFinal);
            });

    } catch (Exception e) {
        logger.error("Error crítico preparando envío de correo: {}", e.getMessage());
        return Mono.just("Error");
    }
}









    /**
     * Sincronización con el modelo Legacy R1, R2, R3.
     */
    private void sincronizarConLegacy(NdtPlantillaDato plantilla, JsonNode rootNode) throws Exception {
        String curp = extraerCurp(rootNode);
        if (curp == null) return;
        NdtContadorPublicoAut contador = contadorRepository.findByCurp(curp).orElseThrow(() -> new RuntimeException("CPA no hallado"));
        String folio = rootNode.path("numTramiteNotaria").asText("S/F");
        String idPlantilla = plantilla.getCveIdPlantillaDato().toString();

        if ("ACREDITACION_MEMBRESIA".equalsIgnoreCase(plantilla.getDesTipoAcuse())) {
            guardarAcreditacionLegacy(contador, rootNode, folio, idPlantilla);
        } else if ("ACUSE_SOLICITUD_CAMBIO".equalsIgnoreCase(plantilla.getDesTipoAcuse())) {
            guardarModificacionLegacy(contador, rootNode, folio, idPlantilla);
        } else if ("ACUSE_SOLICITUD_BAJA".equalsIgnoreCase(plantilla.getDesTipoAcuse())) {
            guardarBajaLegacy(contador, rootNode, folio, idPlantilla);
        }
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
        acred.setCveIdUsuario(usr);
        r3ColegioRepository.findRegistroActivoByCpa(contador.getCveIdCpa()).ifPresent(r3 -> acred.setCveIdColegio(r3.getCveIdColegio()));
        
        if (json.has("fechaExpedicionAcreditacion")) acred.setFecAcreditacionCp(parseFecha(json.get("fechaExpedicionAcreditacion").asText()));
        if (json.has("fechaExpedicionMembresia")) acred.setFecPresentacionAcreditacion(parseFecha(json.get("fechaExpedicionMembresia").asText()));
        acreditacionRepository.save(acred);

        if (json.has("desPathHdfsAcreditacion")) guardarDocumentoLegacy(contador.getCveIdCpa(), json.get("desPathHdfsAcreditacion").asText(), 133L, usr);
        if (json.has("desPathHdfsMembresia")) guardarDocumentoLegacy(contador.getCveIdCpa(), json.get("desPathHdfsMembresia").asText(), 132L, usr);
    }

    private void guardarBajaLegacy(NdtContadorPublicoAut contador, JsonNode json, String folio, String idPlantilla) {
        LocalDateTime ahora = LocalDateTime.now();
        String usr = contador.getCurp();
        String motivo = json.path("motivoBaja").asText("");
        if (motivo.length() > 3100) motivo = motivo.substring(0, 3100);

        NdtCpaTramite tr = new NdtCpaTramite();
        tr.setCveIdCpa(contador.getCveIdCpa());
        tr.setFecSolicitudMovimiento(ahora);
        tr.setFecRegistroAlta(ahora);
        tr.setCveIdUsuario(usr);
        tr.setNumTramiteNotaria(json.path("folioSolicitud").asText(folio));
        tr.setUrlAcuseNotaria(idPlantilla);
        NdtCpaTramite trG = tramiteRepository.save(tr);

        NdtCpaTramiteEstado te = new NdtCpaTramiteEstado();
        te.setCveIdCpaTramite(trG.getCveIdCpaTramite());
        te.setCveIdEstadoTramite(1L);
        te.setObservaciones(motivo);
        te.setFecRegistroAlta(ahora);
        te.setCveIdUsuario(usr);
        tramiteEstadoRepository.save(te);

        NdtCpaEstatus est = new NdtCpaEstatus();
        est.setCveIdCpa(contador.getCveIdCpa());
        est.setCveIdEstadoCpa(3L);
        est.setFecRegistroAlta(ahora);
        est.setCveIdUsuario(usr);
        est.setCveIdCpaTramite(trG.getCveIdCpaTramite());
        est.setDesComentarios(motivo);
        estatusRepository.save(est);

        contador.setCveIdEstadoCpa(3L);
        contadorRepository.save(contador);
    }

    private void guardarModificacionLegacy(NdtContadorPublicoAut contador, JsonNode json, String folio, String idPlantilla) {
        LocalDateTime ahora = LocalDateTime.now();
        String usr = contador.getCurp();
        NdtCpaTramite tr = new NdtCpaTramite();
        tr.setCveIdCpa(contador.getCveIdCpa());
        tr.setFecRegistroAlta(ahora);
        tr.setCveIdUsuario(usr);
        tr.setNumTramiteNotaria(folio);
        tr.setUrlAcuseNotaria(idPlantilla);
        NdtCpaTramite trG = tramiteRepository.save(tr);

        String tipo = json.path("tipoSolicitud").asText("").toUpperCase();
        JsonNode state = json.get("state");
        if ("CONTACTO".equals(tipo)) sincronizarR1(contador, state, trG, usr);
        else if ("DESPACHO".equals(tipo)) sincronizarR2(contador, state, trG, usr);
        else if ("COLEGIO".equals(tipo)) sincronizarR3(contador, state, json, trG, usr);
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
        LocalDateTime ahora = LocalDateTime.now();
        NdtR2Despacho ant = r2DespachoRepository.findRegistroActivoByCpa(contador.getCveIdCpa()).orElse(null);
        NdtR2Despacho r2New = new NdtR2Despacho();
        r2New.setCveIdCpa(contador.getCveIdCpa());
        r2New.setCveIdCpaTramite(tr.getCveIdCpaTramite());
        r2New.setFecRegistroAlta(ahora);
        r2New.setCveIdUsuario(usr);
        if (state != null) {
            long tipoSoc = state.path("selectedTipoSociedad").asLong();
            r2New.setIndTipoCpa(tipoSoc);
            if (tipoSoc == 1) {
                String rfc = state.path("nuevoRfcDespacho").asText().trim().toUpperCase();
                String sql = "SELECT CVE_ID_DESPACHO FROM (SELECT D.CVE_ID_DESPACHO FROM MGPBDTU9X.NDT_DESPACHOS D INNER JOIN MGPBDTU9X.DIT_PERSONA_MORAL PM ON D.CVE_ID_PERSONA_MORAL = PM.CVE_ID_PERSONA_MORAL WHERE PM.RFC = :rfc AND D.FEC_REGISTRO_BAJA IS NULL ORDER BY D.FEC_REGISTRO_ALTA DESC) WHERE ROWNUM = 1";
                try {
                    Long id = ((Number) entityManager.createNativeQuery(sql).setParameter("rfc", rfc).getSingleResult()).longValue();
                    r2New.setCveIdDespacho(id);
                } catch (Exception e) { logger.error("Despacho no hallado"); }
                r2New.setCargoQueDesempena(state.path("selectedCargoDesempena").asText(null));
            } else {
                r2New.setIndCuentaconTrab("Si".equalsIgnoreCase(state.path("tieneTrabajadores").asText()) ? "1" : "0");
                r2New.setNumTrabajadores(state.path("numeroTrabajadores").asInt(0));
            }
            NdtR2Despacho g = r2DespachoRepository.save(r2New);
            String tel = state.path("telefonoFijoDespacho").asText(null);
            if (tel != null && !tel.isEmpty()) vincularR2Contacto(g.getCveIdR2Despacho(), tel, usr);
            if (ant != null) { ant.setFecRegistroBaja(ahora); r2DespachoRepository.save(ant); }
        }
    }

    private void sincronizarR3(NdtContadorPublicoAut contador, JsonNode state, JsonNode root, NdtCpaTramite tr, String usr) {
        LocalDateTime ahora = LocalDateTime.now();
        NdtR3Colegio ant = r3ColegioRepository.findRegistroActivoByCpa(contador.getCveIdCpa()).orElse(null);
        NdtR3Colegio r3New = new NdtR3Colegio();
        r3New.setCveIdCpa(contador.getCveIdCpa());
        r3New.setCveIdCpaTramite(tr.getCveIdCpaTramite());
        r3New.setFecRegistroAlta(ahora);
        r3New.setCveIdUsuario(usr);
        if (root != null && root.has("desPathHdfsConstancia")) {
            guardarDocumentoLegacy(contador.getCveIdCpa(), root.get("desPathHdfsConstancia").asText(), 132L, usr);
        }
        if (state != null && state.has("nuevoRfcColegio")) {
            String rfc = state.get("nuevoRfcColegio").asText().trim().toUpperCase();
            String sql = "SELECT CVE_ID_COLEGIO FROM (SELECT C.CVE_ID_COLEGIO FROM MGPBDTU9X.NDT_COLEGIO C INNER JOIN MGPBDTU9X.DIT_PERSONA_MORAL PM ON C.CVE_ID_PERSONA_MORAL = PM.CVE_ID_PERSONA_MORAL WHERE PM.RFC = :rfc AND C.FEC_REGISTRO_BAJA IS NULL ORDER BY C.FEC_REGISTRO_ALTA DESC) WHERE ROWNUM = 1";
            try {
                Long id = ((Number) entityManager.createNativeQuery(sql).setParameter("rfc", rfc).getSingleResult()).longValue();
                r3New.setCveIdColegio(id);
            } catch (Exception e) { logger.error("Colegio no hallado"); }
        }
        r3ColegioRepository.save(r3New);
        if (ant != null) { ant.setFecRegistroBaja(ahora); r3ColegioRepository.save(ant); }
    }

    private void vincularR1Contacto(Long idR1, String val, Long tipo, String usr) {
        if (val == null || val.trim().isEmpty()) return;
        NdtFormaContacto fc = new NdtFormaContacto();
        fc.setDesFormaContacto(val); fc.setCveIdTipoContacto(tipo); fc.setFecRegistroAlta(LocalDateTime.now());
        NdtFormaContacto g = formaContactoRepository.save(fc);
        NdtR1FormaContacto rel = new NdtR1FormaContacto();
        rel.setCveIdR1DatosPersonales(idR1); rel.setCveIdFormaContacto(g.getCveIdFormaContacto());
        rel.setFecRegistroAlta(LocalDateTime.now()); rel.setCveIdUsuario(usr);
        r1FormaContactoRepository.save(rel);
    }

    private void vincularR2Contacto(Long idR2, String tel, String usr) {
        NdtFormaContacto fc = new NdtFormaContacto();
        fc.setDesFormaContacto(tel); fc.setCveIdTipoContacto(2L); fc.setFecRegistroAlta(LocalDateTime.now());
        NdtFormaContacto g = formaContactoRepository.save(fc);
        NdtR2FormaContacto rel = new NdtR2FormaContacto();
        rel.setCveIdR2Despacho(idR2); rel.setCveIdFormaContacto(g.getCveIdFormaContacto());
        rel.setFecRegistroAlta(LocalDateTime.now()); rel.setCveIdUsuario(usr);
        r2FormaContactoRepository.save(rel);
    }

    private void guardarDocumentoLegacy(Long idCpa, String url, Long tipo, String usr) {
        NdtDocumentoProbatorio doc = new NdtDocumentoProbatorio();
        doc.setCveIdCpa(idCpa); doc.setUrlDocumentoProb(url); doc.setCveIdDoctoProbPorTipo(tipo);
        doc.setFecRegistroAlta(LocalDateTime.now()); doc.setCveIdUsuario(usr);
        documentoProbatorioRepository.save(doc);
    }

    private String aplicarLogicaHash(String cadena, String folio) {
        if (folio == null || folio.isEmpty() || !cadena.contains("|HASH|")) return cadena;
        int idx = cadena.indexOf("|HASH|"); int start = idx + 6; int end = cadena.indexOf("|", start);
        String old = (end != -1) ? cadena.substring(start, end) : cadena.substring(start);
        return cadena.replace("|HASH|" + old, "|HASH|" + folio + "|" + old);
    }

    private LocalDateTime parseFecha(String f) {
        if (f == null || f.isEmpty()) return null;
        try { return LocalDate.parse(f, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay(); }
        catch (Exception e) { 
            try { return LocalDate.parse(f, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay(); }
            catch (Exception e2) { return null; }
        }
    }

    private String extraerCurp(JsonNode root) {
        if (root.has("curp")) return root.get("curp").asText();
        if (root.has("CURP")) return root.get("CURP").asText();
        return null;
    }

    private Mono<String> procesarEnvioCorreo(String rfc, String nom, String token, String asunto, String html) {
        return procesarEnvioCorreo(rfc, nom, token, asunto, html, null, null);
    }

private Mono<String> procesarEnvioCorreo(String rfc, String nom, String token, String asunto, String html, byte[] pdf, String nomF) {
    return obtenerCorreoDeMediosContacto(rfc, token).flatMap(mail -> {
        String destino = (mail != null && !mail.isEmpty()) ? mail : "jrpjaime@gmail.com"; 
        
        Map<String, Object> correoBody = new HashMap<>();
        correoBody.put("remitente", "tramites.cpa@imss.gob.mx");
        correoBody.put("correoPara", Collections.singletonList(destino));
        correoBody.put("asunto", asunto);
        
        // --- SEGURIDAD: CUERPO LIMPIO SIN LINKS ---
        correoBody.put("cuerpoCorreo", construirHtmlBase(nom, rfc, html));

        if (pdf != null && pdf.length > 0) {
            Map<String, String> adjunto = new HashMap<>();
            
            // Estructura más compatible en Stage: nombre y archivo
            adjunto.put("nombre", nomF); 
            adjunto.put("archivo", Base64.getEncoder().encodeToString(pdf));

            correoBody.put("adjuntos", Collections.singletonList(adjunto));
            logger.info("Adjuntando PDF limpio de links para: {}", destino);
        }

        return webClient.post()
            .uri(urlSendCorreoElectronico)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(correoBody)
            .retrieve()
            .onStatus(HttpStatusCode::isError, response -> 
                response.bodyToMono(String.class).flatMap(errorBody -> {
                    logger.error("ERROR CORREOS (Posible bloqueo por adjunto): {}", errorBody);
                    return Mono.error(new RuntimeException(errorBody));
                })
            )
            .toBodilessEntity()
            .thenReturn("Ok")
            .doOnSuccess(s -> logger.info("¡EXITO! Correo enviado satisfactoriamente."))
            .onErrorResume(e -> {
                logger.error("FALLA FINAL: {}", e.getMessage());
                // Si falla con adjunto, intentar enviar SIN adjunto para que el usuario reciba el aviso
                logger.warn("Intentando enviar correo de respaldo sin adjunto...");
                return procesarEnvioCorreoSinAdjunto(rfc, nom, token, asunto, html);
            });
    });
}

// Método de respaldo para que el usuario no se quede sin notificación si el adjunto es bloqueado
private Mono<String> procesarEnvioCorreoSinAdjunto(String rfc, String nom, String token, String asunto, String html) {
    CorreoDto dto = new CorreoDto();
    dto.setRemitente("tramites.cpa@imss.gob.mx");
    dto.setCorreoPara(Collections.singletonList("jrpjaime@gmail.com")); // o el mail del contador
    dto.setAsunto(asunto );
    dto.setCuerpoCorreo(construirHtmlBase(nom, rfc, html + "<p><b>Nota:</b> Favor de descargar el acuse desde el portal.</p>"));
    
    return webClient.post()
        .uri(urlSendCorreoElectronico)
        .bodyValue(dto)
        .retrieve()
        .toBodilessEntity()
        .thenReturn("Ok")
        .onErrorResume(e -> Mono.just("Error"));
}
private String construirHtmlBase(String n, String r, String h) {
    // Eliminamos enlaces directos a servidores internos (IPs o dominios QA)
    // para evitar que los filtros de seguridad rechacen el adjunto.
    return "<html><body style='font-family: Arial, sans-serif; color: #333;'>" +
           "<div style='border-left: 4px solid #006341; padding-left: 15px;'>" +
           "<h3>Estimado(a) " + n + "</h3>" +
           h + 
           "<p>Se adjunta a este mensaje el acuse oficial de su trámite en formato PDF.</p>" +
           "<br><p style='font-size: 12px; color: #666;'>Este es un mensaje automático, por favor no responda a esta dirección.</p>" +
           "</div></body></html>";
}

    private Mono<String> obtenerCorreoDeMediosContacto(String rfc, String token) {
        return webClient.get().uri(catalogosMicroserviceUrl.trim() + "/mediosContacto/" + rfc)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token).retrieve()
            .bodyToMono(MediosContactoContadoresResponseDto.class)
            .map(res -> res.getMedios().stream().filter(m -> "1".equals(m.getTipoContacto())).findFirst().map(MedioContactoContadoresDto::getDesFormaContacto).orElse(""))
            .onErrorResume(e -> Mono.just(""));
    }

    @Override public Mono<String> enviarCorreoAcreditacion(String r, String n, String t) { return procesarEnvioCorreo(r, n, t, "Acreditación", "Recibido"); }
    @Override public Mono<String> enviarCorreoSolicitudBaja(String r, String n, String t) { return procesarEnvioCorreo(r, n, t, "Baja", "Recibido"); }
    @Override public Mono<String> enviarCorreoModificacionDatosContacto(String r, String n, String t) { return procesarEnvioCorreo(r, n, t, "Contacto", "Recibido"); }
    @Override public Mono<String> enviarCorreoModificacionDatosDespacho(String r, String n, String t) { return procesarEnvioCorreo(r, n, t, "Despacho", "Recibido"); }
    @Override public Mono<String> enviarCorreoModificacionDatosColegio(String r, String n, String t) { return procesarEnvioCorreo(r, n, t, "Colegio", "Recibido"); }
}