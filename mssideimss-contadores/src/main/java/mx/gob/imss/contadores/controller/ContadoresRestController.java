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

/*
     @PostMapping("/acreditacionMembresia")
    public ResponseEntity<String> recibirAcreditacionMembresia(
            @RequestParam("fechaExpedicionAcreditacion") LocalDate fechaExpedicionAcreditacion,
            @RequestParam("fechaExpedicionMembresia") LocalDate fechaExpedicionMembresia,
            @RequestParam("archivoUno") MultipartFile archivoUno,
            @RequestParam("archivoDos") MultipartFile archivoDos) {

        logger.info("Recibiendo datos de acreditación y membresía:");
        logger.info("Fecha Expedición Acreditación: {}", fechaExpedicionAcreditacion);
        logger.info("Fecha Expedición Membresía: {}", fechaExpedicionMembresia);
        logger.info("Archivo Uno: {} (tamaño: {} bytes)", archivoUno.getOriginalFilename(), archivoUno.getSize());
        logger.info("Archivo Dos: {} (tamaño: {} bytes)", archivoDos.getOriginalFilename(), archivoDos.getSize());

        // Aquí puedes agregar la lógica para guardar los archivos
        // Por ejemplo, guardar en un sistema de archivos, S3, etc.
        try {
            // Ejemplo de cómo podrías guardar los archivos (simplemente para demostrar)
            // String pathUno = "ruta/a/guardar/" + archivoUno.getOriginalFilename();
            // archivoUno.transferTo(new File(pathUno));
            // String pathDos = "ruta/a/guardar/" + archivoDos.getOriginalFilename();
            // archivoDos.transferTo(new File(pathDos));

            // Simplemente para la demostración, retornamos éxito
            return new ResponseEntity<>("Datos y archivos recibidos exitosamente.", HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al procesar los archivos: {}", e.getMessage());
            return new ResponseEntity<>("Error al procesar los archivos.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
	 */

 
 
    @PostMapping("/acreditacionMembresia")
    public Mono<ResponseEntity<String>> acreditacionMembresia(
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

        // Formatearla como yyyyMM
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
        String fechaFormateadaActual = fechaActual.format(formatter);




        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String jwtToken = null;
        WebAuthenticationDetails originalRequestDetails = null; // Para recuperar los detalles originales si los necesitas

        if (authentication != null && authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            jwtToken = (String) details.get("jwt");

            // Si necesitas los detalles originales de la solicitud (IP, sessionId, etc.)
            Object rawOriginalDetails = details.get("originalDetails");
            if (rawOriginalDetails instanceof WebAuthenticationDetails) {
                originalRequestDetails = (WebAuthenticationDetails) rawOriginalDetails;
                logger.info("Detalles originales de la solicitud (IP remota): " + originalRequestDetails.getRemoteAddress());
            }
        }


        if (jwtToken != null) {
            logger.info("JWT del SecurityContext obtenido con éxito.");
           
        } else {
            logger.warn("No se pudo obtener el token JWT del SecurityContext. La llamada al servicio de documentos podría fallar.");
        }

        final String finalJwtToken = jwtToken; // Necesario para usar en lambdas





        // La conversión a Base64  
        Mono<String> archivoUnoBase64Mono = utileriasService.convertMultipartFileToBase64(archivoUno, "archivoUno");
        Mono<String> archivoDosBase64Mono = utileriasService.convertMultipartFileToBase64(archivoDos, "archivoDos");

        // Combinar los resultados de la conversión a Base64
        return Mono.zip(archivoUnoBase64Mono, archivoDosBase64Mono)
            .flatMap(tuple -> {
                String archivoUnoBase64 = tuple.getT1();
                String archivoDosBase64 = tuple.getT2();

                // Crear DTO para el archivo de Acreditación
                DocumentoIndividualDto docAcreditacion = new DocumentoIndividualDto();
                docAcreditacion.setNomArchivo(archivoUno.getOriginalFilename());
                docAcreditacion.setDocumentoBase64(archivoUnoBase64);
                docAcreditacion.setDesRfc("RFC_DEL_USUARIO"); // Rellenar con RFC real
                docAcreditacion.setDesPath("/acreditaciones/" + fechaFormateadaActual + "/"); // Ruta lógica en Hadoop

                // Enviar archivo de Acreditación al microservicio de documentos
                Mono<DocumentoIndividualDto> resultadoAcreditacionMono = acreditacionMembresiaService.cargarDocumentoAlmacenamiento(docAcreditacion, finalJwtToken);

                // Crear DTO para el archivo de Membresía
                DocumentoIndividualDto docMembresia = new DocumentoIndividualDto();
                docMembresia.setNomArchivo(archivoDos.getOriginalFilename());
                docMembresia.setDocumentoBase64(archivoDosBase64);
                docMembresia.setDesRfc("RFC_DEL_USUARIO"); // Rellenar con RFC real
                docMembresia.setDesPath("/membresias/" + fechaFormateadaActual + "/"); // Ruta lógica en Hadoop

                // Enviar archivo de Membresía al microservicio de documentos
                Mono<DocumentoIndividualDto> resultadoMembresiaMono = acreditacionMembresiaService.cargarDocumentoAlmacenamiento(docMembresia, finalJwtToken);

                // Combinar los resultados de ambos documentos
                return Mono.zip(resultadoAcreditacionMono, resultadoMembresiaMono)
                    .flatMap(resultsTuple -> {
                        DocumentoIndividualDto resultadoAcreditacion = resultsTuple.getT1();
                        DocumentoIndividualDto resultadoMembresia = resultsTuple.getT2();

                        if (resultadoAcreditacion.getCodigo() != null && resultadoAcreditacion.getCodigo() != 0) {
                            logger.error("Fallo al cargar archivo de acreditación: {} - {}", resultadoAcreditacion.getCodigo(), resultadoAcreditacion.getMensaje());
                            return Mono.just(new ResponseEntity<>("Error al cargar el archivo de acreditación: " + resultadoAcreditacion.getMensaje(), HttpStatus.INTERNAL_SERVER_ERROR));
                        }
                        if (resultadoMembresia.getCodigo() != null && resultadoMembresia.getCodigo() != 0) {
                            logger.error("Fallo al cargar archivo de membresía: {} - {}", resultadoMembresia.getCodigo(), resultadoMembresia.getMensaje());
                            return Mono.just(new ResponseEntity<>("Error al cargar el archivo de membresía: " + resultadoMembresia.getMensaje(), HttpStatus.INTERNAL_SERVER_ERROR));
                        }

                        // Ambos documentos cargados exitosamente
                        logger.info("Ambos documentos cargados exitosamente.");
                        logger.info("Path HDFS Acreditación: {}", resultadoAcreditacion.getDesPathHdfs());
                        logger.info("Path HDFS Membresía: {}", resultadoMembresia.getDesPathHdfs());

                        // Aquí es donde asociarías los desPathHdfs y las fechas a tu entidad de solicitud en tu base de datos.
                        // Puedes crear un método en tu servicio para manejar esta persistencia.
                        // Ejemplo:
                        // acreditacionMembresiaService.guardarSolicitud(fechaExpedicionAcreditacion, fechaExpedicionMembresia,
                        //                                            resultadoAcreditacion.getDesPathHdfs(), resultadoMembresia.getDesPathHdfs());

                        return Mono.just(new ResponseEntity<>("Datos y archivos recibidos y procesados exitosamente. Paths HDFS: Acreditación=" + resultadoAcreditacion.getDesPathHdfs() + ", Membresía=" + resultadoMembresia.getDesPathHdfs(), HttpStatus.OK));
                    })
                    .onErrorResume(e -> {
                        logger.error("Error al procesar la carga de documentos: {}", e.getMessage(), e);
                        return Mono.just(new ResponseEntity<>("Error interno del servidor al procesar la carga de documentos: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
                    });
            })
            .onErrorResume(e -> {
                logger.error("Error de I/O al convertir archivos a Base64: {}", e.getMessage(), e);
                return Mono.just(new ResponseEntity<>("Error al procesar los archivos de entrada: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
            });
    }
 
 
 

}