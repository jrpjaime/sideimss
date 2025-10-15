package mx.gob.imss.contadores.controller;

 
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import mx.gob.imss.contadores.dto.AcreditacionMenbresiaResponseDto;
import mx.gob.imss.contadores.dto.DocumentoIndividualDto;
import mx.gob.imss.contadores.dto.DocumentoIndividualResponseDto;
import mx.gob.imss.contadores.dto.PlantillaDatoDto;
import mx.gob.imss.contadores.service.AcreditacionMembresiaService;
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
    private UtileriasService utileriasService;
 
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


        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" );  
        logger.info("plantillaDatoDto.getDatosJson():"+ plantillaDatoDto.getDatosJson());  
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::" );  
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaActualFormateada = fechaActual.format(formatter);
 

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
 
        AcreditacionMenbresiaResponseDto responseDto = new AcreditacionMenbresiaResponseDto();
        responseDto.setFechaActual(fechaActualFormateada);
 
        responseDto.setCodigo(0);
        responseDto.setMensaje("Operación realizada exitosamente."); 
        logger.info("Operación realizada exitosamente. " );
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }


}