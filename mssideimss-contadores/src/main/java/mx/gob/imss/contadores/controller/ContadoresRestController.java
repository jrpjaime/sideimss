package mx.gob.imss.contadores.controller;

 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

 


@Controller
@CrossOrigin("*") 
@RequestMapping("/mssideimss-contadores/v1")
public class ContadoresRestController {
	private final static Logger logger = LoggerFactory.getLogger(ContadoresRestController.class);
  
 
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
 
	 

 
 

 
 
 

}