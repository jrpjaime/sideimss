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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import io.jsonwebtoken.Claims;
import mx.gob.imss.contadores.dto.AcreditacionMenbresiaResponseDto;
import mx.gob.imss.contadores.dto.DocumentoIndividualDto;
import mx.gob.imss.contadores.dto.DocumentoIndividualResponseDto;
import mx.gob.imss.contadores.dto.PlantillaDatoDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.enums.TipoAcuse;
import mx.gob.imss.contadores.service.AcreditacionMembresiaService;
import mx.gob.imss.contadores.service.JwtUtilService;
import mx.gob.imss.contadores.service.UtileriasService;
import reactor.core.publisher.Mono;
  
import org.springframework.security.core.Authentication;  
import org.springframework.security.web.authentication.WebAuthenticationDetails;
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
        // DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaActualFormateada = fechaActual.format(formatter);
       // String fechaactualvista=fechaActual.format(formatter2);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = null;
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

        String urlDocumentoBase64= null;
        String urlDocumento= null;
        String rfc = null;
        try {
            // Extraer todos los claims del token
            Claims claims = jwtUtilService.extractAllClaims(jwtToken);
            // Obtener el RFC del claim "rfc"
            rfc = (String) claims.get("rfc");
            logger.info("RFC extraído del token JWT: {}", rfc);
        } catch (Exception e) {
            logger.error("Error al extraer RFC del token JWT: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("Error al procesar el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Crear una instancia de NdtPlantillaDato y asignar el JSON
        NdtPlantillaDato ndtPlantillaDato = new NdtPlantillaDato();

        
        ndtPlantillaDato.setDesRfc(rfc);
        ndtPlantillaDato.setNomDocumento(plantillaDatoDto.getNomDocumento());
        ndtPlantillaDato.setDesPathVersion(plantillaDatoDto.getDesVersion());
        ndtPlantillaDato.setDesDatos(plantillaDatoDto.getDatosJson());
        ndtPlantillaDato.setDesTipoAcuse(plantillaDatoDto.getTipoAcuse().name());
        ndtPlantillaDato.setFecRegistro(fechaActual);
        

        try {
            // Llamar al servicio para guardar la plantilla de datos
            NdtPlantillaDato plantillaGuardada = acreditacionMembresiaService.guardarPlantillaDato(ndtPlantillaDato);

            urlDocumento= rfc+ "|" +  plantillaGuardada.getCveIdPlantillaDato().toString() +"";
            urlDocumentoBase64 = Base64.getEncoder().encodeToString(urlDocumento.getBytes("UTF-8"));
            logger.info("Plantilla de datos guardada exitosamente con ID: {}", plantillaGuardada.getCveIdPlantillaDato());
        } catch (Exception e) {
            logger.error("Error al guardar la plantilla de datos: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("Error al procesar y guardar los datos: " + e.getMessage());
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        AcreditacionMenbresiaResponseDto responseDto = new AcreditacionMenbresiaResponseDto();
        responseDto.setFechaActual(fechaActualFormateada);
        responseDto.setCodigo(0);
        responseDto.setMensaje("Operación realizada exitosamente."); 
        responseDto.setUrlDocumento(urlDocumentoBase64);
        logger.info("Operación realizada exitosamente. ");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}