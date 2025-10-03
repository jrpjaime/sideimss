package mx.gob.imss.documentos.controller;

 
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.gob.imss.documentos.dto.DocumentoIndividualDto;
import mx.gob.imss.documentos.service.CargaDocumentoService;
 


@RestController   
@CrossOrigin("*") 
@RequestMapping("/mssideimss-documentos/v1") 
public class DocumentosRestController {
	private final static Logger logger = LoggerFactory.getLogger(DocumentosRestController.class);
 
	@Autowired // Inyecta tu servicio aquí
	private CargaDocumentoService cargaDocumentoService;


 
    @GetMapping("/info")
	public ResponseEntity<List<String>> info() {
		logger.info("........................mssideimss-documentos info..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-documentos");
		list.add("20251002");
		list.add("Documentos");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}


	@GetMapping("/list")
	public ResponseEntity<List<String>> list() {
		logger.info("........................mssideimss-documentos list..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-documentos");
		list.add("20251002");
		list.add("Documentos");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}


 
 
    /**
     * Endpoint para cargar un documento individual en Hadoop.
     * Recibe un DocumentoIndividualVO en el cuerpo de la solicitud.
     * Devuelve el objeto con los códigos de confirmación (codigo y mensaje).
     *
     * @param documentoIndividualVO Objeto DocumentoIndividualVO con la información del documento a cargar.
     * @return ResponseEntity con el DocumentoIndividualVO actualizado y el estado HTTP.
     */
 
    @PostMapping("/cargarDocumento")
    public ResponseEntity<DocumentoIndividualDto> cargarDocumento(@RequestBody DocumentoIndividualDto documentoIndividualVO) {
        logger.info("------------- Inicio cargarDocumento en Controller -------------");
        logger.debug("Documento recibido: {}", documentoIndividualVO); // Usar debug para datos sensibles

        try {

            DocumentoIndividualDto resultado = cargaDocumentoService.cargaDocumentoHadoop(documentoIndividualVO);
            
            logger.info("Documento cargado exitosamente. Código: {}, Mensaje: {}", resultado.getCodigo(), resultado.getMensaje());
            return new ResponseEntity<>(resultado, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // Error en los datos de entrada (Base64 inválido, campo nulo, etc.)
            logger.error("Error de argumentos al cargar el documento: {}", e.getMessage(), e);
            documentoIndividualVO.setCodigo(1); // O un código de error específico para argumentos inválidos
            documentoIndividualVO.setMensaje("Error de datos de entrada: ");
            return new ResponseEntity<>(documentoIndividualVO, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (ParseException e) {
            // Error en el formato de la fecha
            logger.error("Error de formato de fecha al cargar el documento: {}", e.getMessage(), e);
            documentoIndividualVO.setCodigo(2); // Código de error específico para formato de fecha
            documentoIndividualVO.setMensaje("Error en el formato de la fecha");
            return new ResponseEntity<>(documentoIndividualVO, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (IOException e) {
            // Error de comunicación o I/O con Hadoop
            logger.error("Error de I/O al interactuar con Hadoop: {}", e.getMessage(), e);
            documentoIndividualVO.setCodigo(3); // Código de error específico para I/O con Hadoop
            documentoIndividualVO.setMensaje("Error de conexión o I/O con el sistema de almacenamiento" );
            return new ResponseEntity<>(documentoIndividualVO, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        } catch (Exception e) {
            // Cualquier otra excepción inesperada
            logger.error("Error inesperado al cargar el documento: {}", e.getMessage(), e);
            documentoIndividualVO.setCodigo(99); // Código de error genérico
            documentoIndividualVO.setMensaje("Error interno del servidor al cargar el documento");
            return new ResponseEntity<>(documentoIndividualVO, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        } finally {
            logger.info("------------- Fin cargarDocumento en Controller -------------");
        }
    }	 

 
 

 
 
 

}