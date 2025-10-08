package mx.gob.imss.acuses.controller;

 
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
 
import java.net.URLEncoder; 
 
import org.springframework.http.HttpHeaders;  
import org.springframework.http.MediaType;


import mx.gob.imss.acuses.dto.DecargarAcuseDto;
import mx.gob.imss.acuses.service.AcuseService;

 


@Controller
@CrossOrigin("*") 
@RequestMapping("/mssideimss-acuses/v1")
public class AcusesRestController {
	private final static Logger logger = LoggerFactory.getLogger(AcusesRestController.class);

	@Autowired
	private AcuseService acuseService;
  
 
    @GetMapping("/info")
	public ResponseEntity<List<String>> info() {
		logger.info("........................mssideimss-acuses info..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-acuses");
		list.add("20251007");
		list.add("Acuses");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}


	@GetMapping("/list")
	public ResponseEntity<List<String>> list() {
		logger.info("........................mssideimss-acuses list..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-acuses");
		list.add("20251007");
		list.add("Acuses");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}

 

	@RequestMapping("/descargarAcuse/{urlDocumento}")
	public ResponseEntity<byte[]> descargarAcuse (@PathVariable("urlDocumento")  String urlDocumento, HttpServletRequest request) {
 

        DecargarAcuseDto decargarAcuseDto = acuseService.consultaAcuseByUrlDocumento(urlDocumento); 
		
        if (decargarAcuseDto.getCodigo() != 0 || decargarAcuseDto.getDocumento() == null || decargarAcuseDto.getDocumento().isEmpty()) {
            logger.error("Error al obtener el documento o documento vacío. Mensaje: " + decargarAcuseDto.getMensaje());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found si no se encuentra o hay error
        }

        try {
            // El formato es "data:application/pdf;base64,CONTENIDO_BASE64"
            String base64Content = decargarAcuseDto.getDocumento().split(",")[1];
            byte[] pdfBytes = Base64.getDecoder().decode(base64Content);

            String fileName = decargarAcuseDto.getNombreDocumento();
            if (fileName == null || fileName.isEmpty()) {
                fileName = "documento.pdf"; // Nombre por defecto si no se proporciona
            }
            
            // Asegurarse de que el nombre del archivo incluya la extensión .pdf si no la tiene
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }

            // Codificar el nombre del archivo para asegurar que caracteres especiales se manejen correctamente
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // La cabecera Content-Disposition indica al navegador que debe descargar el archivo.
            // "attachment" fuerza la descarga, y "filename" sugiere el nombre del archivo.
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.error("Error al decodificar la cadena Base64 del documento: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna 400 Bad Request
        } catch (UnsupportedEncodingException e) {
            logger.error("Error al codificar el nombre del archivo: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500 Internal Server Error
        } catch (Exception e) {
            logger.error("Error inesperado al descargar el acuse: " + e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR); // Retorna 500 Internal Server Error
        }
	}
 
 

 
 
 

}