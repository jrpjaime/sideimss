package mx.gob.imss.contadores.controller;

 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.jsonwebtoken.Claims;
import mx.gob.imss.contadores.dto.AcreditacionMenbresiaResponseDto;
import mx.gob.imss.contadores.dto.PlantillaDatoDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.service.AcreditacionMembresiaService;
import mx.gob.imss.contadores.service.JwtUtilService;
  
import org.springframework.security.core.Authentication;  
import java.util.Map;

@Controller
@CrossOrigin("*") 
@RequestMapping("/mssideimss-contadores/v1")
public class ContadoresRestController {
	private final static Logger logger = LoggerFactory.getLogger(ContadoresRestController.class);
  
    @Autowired
    private AcreditacionMembresiaService acreditacionMembresiaService;

    @Autowired
    private  JwtUtilService jwtUtilService;
 
    @GetMapping("/info")
	public ResponseEntity<List<String>> info() {
		logger.info("........................mssideimss-contadores info..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-contadores");
		list.add("20251002");
		list.add("Contadores");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}


	@GetMapping("/list")
	public ResponseEntity<List<String>> list() {
		logger.info("........................mssideimss-contadores list..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-contadores");
		list.add("20251002");
		list.add("Contadores");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}

 
 
    








   @PostMapping("/acreditacionMembresia")
    public ResponseEntity<AcreditacionMenbresiaResponseDto> acreditacionMembresia(@RequestBody PlantillaDatoDto plantillaDatoDto) {
        logger.info("Recibiendo datos de acreditación y membresía acreditacionMembresia:");
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        logger.info("plantillaDatoDto.getDatosJson():" + plantillaDatoDto.getDatosJson());
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaActualFormateada = fechaActual.format(formatter);

        // Obtención del JWT Token y manejo de errores
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = null;
        String rfc = null;
        String nombreCompleto = null;

        if (authentication != null && authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            jwtToken = (String) details.get("jwt");
        }

        if (jwtToken == null) {
            logger.warn("No se pudo obtener el token JWT del SecurityContext.");
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("No se pudo obtener el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Claims claims = jwtUtilService.extractAllClaims(jwtToken);
            rfc = (String) claims.get("rfc");
            String nombre = (String) claims.get("nombre");
            String primerApellido = (String) claims.get("primerApellido");   
            String segundoApellido = (String) claims.get("segundoApellido");
            nombreCompleto = String.format("%s %s %s", nombre, primerApellido, segundoApellido != null ? segundoApellido : "").trim();
            logger.info("RFC extraído del token JWT: {}", rfc);
        } catch (Exception e) {
            logger.error("Error al extraer datos (RFC/Nombre) del token JWT: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("Error al procesar el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Crear instancia de NdtPlantillaDato
        NdtPlantillaDato ndtPlantillaDato = new NdtPlantillaDato();
        ndtPlantillaDato.setDesRfc(rfc);
        ndtPlantillaDato.setNomDocumento(plantillaDatoDto.getNomDocumento());
        ndtPlantillaDato.setDesPathVersion(plantillaDatoDto.getDesVersion());
        ndtPlantillaDato.setDesDatos(plantillaDatoDto.getDatosJson());
        ndtPlantillaDato.setDesTipoAcuse(plantillaDatoDto.getTipoAcuse().name());
        ndtPlantillaDato.setFecRegistro(fechaActual);
       
        String urlDocumentoBase64 = null;
        String urlDocumento = null;
        String mensajeCorreo = "No se intentó enviar el correo."; // Mensaje por defecto

        try {
            // **1. INTENTO DE ENVÍO DE CORREO (no es bloqueante para el guardado):**
            logger.info("Iniciando el intento de envío del correo de notificación.");
            // Utilizamos .block() aquí para esperar el resultado del Mono<String>
            // El error si el correo no se envía no se propaga como una excepción aquí.
            mensajeCorreo = acreditacionMembresiaService.enviarCorreoAcreditacion(rfc, nombreCompleto, jwtToken).block();
            logger.info("Resultado del intento de envío de correo: {}", mensajeCorreo);

            // **2. GUARDADO DE INFORMACIÓN (Este sí debe ser exitoso):**
            logger.info("Iniciando el guardado de la plantilla de datos.");
            NdtPlantillaDato plantillaGuardada = acreditacionMembresiaService.guardarPlantillaDato(ndtPlantillaDato);
            urlDocumento = rfc + "|" + plantillaGuardada.getCveIdPlantillaDato().toString();
            urlDocumentoBase64 = Base64.getEncoder().encodeToString(urlDocumento.getBytes("UTF-8"));
            logger.info("Plantilla de datos guardada exitosamente con ID: {}", plantillaGuardada.getCveIdPlantillaDato());

        } catch (Exception e) {
            // Si el guardado falla, entonces sí devolvemos un error 500
            logger.error("Fallo durante el guardado de datos: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorDto.setMensaje("Error al guardar la plantilla de datos. Por favor, intente más tarde.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        AcreditacionMenbresiaResponseDto responseDto = new AcreditacionMenbresiaResponseDto();
        responseDto.setFechaActual(fechaActualFormateada);
        responseDto.setCodigo(0);
        // Puedes combinar el mensaje del correo con el de éxito de la operación principal
        responseDto.setMensaje("Operación realizada exitosamente. " + mensajeCorreo);
        responseDto.setUrlDocumento(urlDocumentoBase64);
        logger.info("Operación realizada exitosamente.");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}