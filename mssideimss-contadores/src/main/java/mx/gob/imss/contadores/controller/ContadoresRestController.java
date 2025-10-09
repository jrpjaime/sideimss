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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import mx.gob.imss.contadores.dto.DocumentoIndividualDto;
import mx.gob.imss.contadores.dto.DocumentoIndividualResponseDto;
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
    public ResponseEntity<DocumentoIndividualResponseDto> acreditacionMembresia(
            @RequestParam("fechaExpedicionAcreditacion") LocalDate fechaExpedicionAcreditacion,
            @RequestParam("fechaExpedicionMembresia") LocalDate fechaExpedicionMembresia,
            @RequestParam("archivoUno") MultipartFile archivoUno,
            @RequestParam("archivoDos") MultipartFile archivoDos) {

        logger.info("Recibiendo datos de acreditación y membresía:");
        logger.info("Fecha Expedición Acreditación: {}", fechaExpedicionAcreditacion);
        logger.info("Fecha Expedición Membresía: {}", fechaExpedicionMembresia);
        logger.info("Archivo Uno: {} (tamaño: {} bytes)", archivoUno.getOriginalFilename(), archivoUno.getSize());
        logger.info("Archivo Dos: {} (tamaño: {} bytes)", archivoDos.getOriginalFilename(), archivoDos.getSize());

        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fechaActualFormateada = fechaActual.format(formatter);
        DateTimeFormatter pathFormatter = DateTimeFormatter.ofPattern("yyyyMM");
        String fechaPathFormateada = fechaActual.format(pathFormatter);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = null;
        if (authentication != null && authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            jwtToken = (String) details.get("jwt");
        }

        if (jwtToken == null) {
            logger.warn("No se pudo obtener el token JWT del SecurityContext.");
            DocumentoIndividualResponseDto errorDto = new DocumentoIndividualResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("No se pudo obtener el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String archivoUnoBase64;
        String archivoDosBase64;

        try {
            archivoUnoBase64 = utileriasService.convertMultipartFileToBase64(archivoUno, "archivoUno").block();
            archivoDosBase64 = utileriasService.convertMultipartFileToBase64(archivoDos, "archivoDos").block();
        } catch (Exception e) {
            logger.error("Error al procesar los archivos de entrada (Base64): {}", e.getMessage(), e);
            DocumentoIndividualResponseDto errorDto = new DocumentoIndividualResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("Error al procesar los archivos de entrada (Base64): " + e.getMessage());
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // DTO para Acreditación
        DocumentoIndividualDto docAcreditacion = new DocumentoIndividualDto();
        docAcreditacion.setNomArchivo(archivoUno.getOriginalFilename());
        docAcreditacion.setDocumentoBase64(archivoUnoBase64);
        docAcreditacion.setDesRfc("RFC_DEL_USUARIO"); // TODO: Rellenar con RFC real del usuario autenticado
        docAcreditacion.setDesPath("/acreditaciones/" + fechaPathFormateada + "/");

        // DTO para Membresía
        DocumentoIndividualDto docMembresia = new DocumentoIndividualDto();
        docMembresia.setNomArchivo(archivoDos.getOriginalFilename());
        docMembresia.setDocumentoBase64(archivoDosBase64);
        docMembresia.setDesRfc("RFC_DEL_USUARIO"); // TODO: Rellenar con RFC real del usuario autenticado
        docMembresia.setDesPath("/membresias/" + fechaPathFormateada + "/");

        DocumentoIndividualDto resAcreditacion;
        DocumentoIndividualDto resMembresia;

        try {
            resAcreditacion = acreditacionMembresiaService.cargarDocumentoAlmacenamiento(docAcreditacion, jwtToken).block();
            resMembresia = acreditacionMembresiaService.cargarDocumentoAlmacenamiento(docMembresia, jwtToken).block();
        } catch (Exception e) {
            logger.error("Error al procesar la carga de documentos al almacenamiento: {}", e.getMessage(), e);
            DocumentoIndividualResponseDto errorDto = new DocumentoIndividualResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("Error interno del servidor al procesar la carga de documentos: " + e.getMessage());
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        DocumentoIndividualResponseDto responseDto = new DocumentoIndividualResponseDto();
        responseDto.setFechaActual(fechaActualFormateada);
        // TODO: Aquí deberías obtener el RFC del usuario autenticado
        responseDto.setDesRfc("RFC_DEL_USUARIO");

        boolean acreditacionOk = resAcreditacion != null && resAcreditacion.getCodigo() != null && resAcreditacion.getCodigo() == 0;
        boolean membresiaOk = resMembresia != null && resMembresia.getCodigo() != null && resMembresia.getCodigo() == 0;

        if (acreditacionOk && membresiaOk) {
            responseDto.setCodigo(0);
            responseDto.setMensaje("Documentos cargados exitosamente.");
            responseDto.setDesPathHdfsAcreditacion(resAcreditacion.getDesPathHdfs());
            responseDto.setDesPathHdfsMembresia(resMembresia.getDesPathHdfs());
            logger.info("Ambos documentos cargados exitosamente. Acreditación HDFS: {}, Membresía HDFS: {}", resAcreditacion.getDesPathHdfs(), resMembresia.getDesPathHdfs());
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        } else {
            StringBuilder errorMessage = new StringBuilder("Fallo al cargar documentos: ");
            if (!acreditacionOk) {
                errorMessage.append("Acreditación (Código: ").append(resAcreditacion != null ? resAcreditacion.getCodigo() : "N/A").append(", Mensaje: ").append(resAcreditacion != null ? resAcreditacion.getMensaje() : "Error desconocido").append("). ");
                logger.error("Fallo al cargar archivo de acreditación: {} - {}", resAcreditacion != null ? resAcreditacion.getCodigo() : "N/A", resAcreditacion != null ? resAcreditacion.getMensaje() : "Error desconocido");
            }
            if (!membresiaOk) {
                errorMessage.append("Membresía (Código: ").append(resMembresia != null ? resMembresia.getCodigo() : "N/A").append(", Mensaje: ").append(resMembresia != null ? resMembresia.getMensaje() : "Error desconocido").append("). ");
                logger.error("Fallo al cargar archivo de membresía: {} - {}", resMembresia != null ? resMembresia.getCodigo() : "N/A", resMembresia != null ? resMembresia.getMensaje() : "Error desconocido");
            }
            responseDto.setCodigo(500);
            responseDto.setMensaje(errorMessage.toString().trim());
            responseDto.setDesPathHdfsAcreditacion(resAcreditacion != null ? resAcreditacion.getDesPathHdfs() : null);
            responseDto.setDesPathHdfsMembresia(resMembresia != null ? resMembresia.getDesPathHdfs() : null);
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}