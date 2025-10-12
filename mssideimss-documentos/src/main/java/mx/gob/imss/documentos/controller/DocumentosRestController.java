package mx.gob.imss.documentos.controller;

 
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import java.util.Base64; // Importar Base64
import java.nio.charset.StandardCharsets;
import org.springframework.http.MediaType; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import mx.gob.imss.documentos.dto.DocumentoIndividualDto;
import mx.gob.imss.documentos.dto.DownloadFileDto;
import mx.gob.imss.documentos.service.CargaDocumentoService;
 
import org.springframework.core.io.Resource;  
import org.springframework.http.HttpHeaders;
import org.apache.hadoop.fs.Path;
 

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
            documentoIndividualVO.setMensaje("Error de datos. ");
            return new ResponseEntity<>(documentoIndividualVO, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (ParseException e) {
            // Error en el formato de la fecha
            logger.error("Error de formato de fecha al cargar el documento: {}", e.getMessage(), e);
            documentoIndividualVO.setCodigo(2); // Código de error específico para formato de fecha
            documentoIndividualVO.setMensaje("Error en el formato de la fecha.");
            return new ResponseEntity<>(documentoIndividualVO, HttpStatus.BAD_REQUEST); // 400 Bad Request
        } catch (IOException e) {
            // Error de comunicación o I/O con Hadoop
            logger.error("Error de I/O al interactuar con Hadoop: {}", e.getMessage(), e);
            documentoIndividualVO.setCodigo(3); // Código de error específico para I/O con Hadoop
            documentoIndividualVO.setMensaje("Error en el sistema de almacenamiento." );
            return new ResponseEntity<>(documentoIndividualVO, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        } catch (Exception e) {
            // Cualquier otra excepción inesperada
            logger.error("Error inesperado al cargar el documento: {}", e.getMessage(), e);
            documentoIndividualVO.setCodigo(99); // Código de error genérico
            documentoIndividualVO.setMensaje("Error al cargar el documento.");
            return new ResponseEntity<>(documentoIndividualVO, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
        } finally {
            logger.info("------------- Fin cargarDocumento en Controller -------------");
        }
    }	 

 
 
 
    /**
     * Endpoint para descargar un documento almacenado en Hadoop.
     * Recibe el fullHdfsPathBase64 del documento en la URL.
     * Delega la lógica de obtención del Resource y nombre del archivo al servicio.
     *
     * @param fullHdfsPathBase64 La ruta completa del archivo en HDFS, codificada en Base64.
     * @return ResponseEntity con el Resource del documento y los encabezados adecuados para descarga.
     */
    @GetMapping("/descargarDocumento")
    public ResponseEntity<Resource> descargarDocumento(@RequestParam String fullHdfsPathBase64) {
        logger.info("------------- Inicio descargarDocumento en Controller -------------");
        try {
            // Llamada al nuevo método del servicio que maneja toda la lógica de descarga
            DownloadFileDto downloadFileDto = cargaDocumentoService.downloadDocumentoHdfs(fullHdfsPathBase64);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + downloadFileDto.getFilename() + "\"");
            // Usa el mediaType del DTO si lo has inferido, de lo contrario usa el genérico
            headers.add(HttpHeaders.CONTENT_TYPE, downloadFileDto.getMediaType() != null && !downloadFileDto.getMediaType().isEmpty()  ? downloadFileDto.getMediaType() : MediaType.APPLICATION_OCTET_STREAM_VALUE);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(downloadFileDto.getResource());

        } catch (IllegalArgumentException e) {
            logger.error("Error de argumentos al descargar el documento: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Documento inválido.", e);
        } catch (IOException e) {
            logger.error("Error de I/O al descargar el documento: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al acceder al documento.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al descargar el documento: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar la descarga.", e);
        } finally {
            logger.info("------------- Fin descargarDocumento en Controller -------------");
        }
    }



    /**
     * Endpoint para eliminar un documento de Hadoop.
     * Recibe el fullHdfsPathBase64 del documento en la URL.
     * Devuelve una respuesta vacía con estado 204 No Content si la eliminación fue exitosa.
     *
     * @param fullHdfsPathBase64 La ruta completa del archivo en HDFS, codificada en Base64.
     * @return ResponseEntity vacía y estado HTTP.
     */
    @DeleteMapping("/eliminarDocumento")
    public ResponseEntity<Void> deleteDocumento(@RequestParam String fullHdfsPathBase64) {
        logger.info("------------- Inicio deleteDocumento en Controller -------------");
        try {
            cargaDocumentoService.deleteDocumentoHdfs(fullHdfsPathBase64);
            logger.info("Documento eliminado exitosamente.");
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content

        } catch (IllegalArgumentException e) {
            logger.error("Error de argumentos al eliminar el documento: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Documento inválido para eliminación.", e);
        } catch (IOException e) {
            logger.error("Error de I/O al eliminar el documento: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al acceder al documento para eliminación.", e);
        } catch (Exception e) {
            logger.error("Error inesperado al eliminar el documento: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar la eliminación.", e);
        } finally {
            logger.info("------------- Fin deleteDocumento en Controller -------------");
        }
    }



        /**
     * Endpoint para cargar un documento individual en Hadoop utilizando MultipartFile.
     * Recibe el archivo como MultipartFile y los metadatos como parámetros de la solicitud.
     * Devuelve un objeto DocumentoIndividualDto con el path de HDFS en Base64 y los códigos de confirmación.
     *
     * @param archivoRecibido El archivo a subir.
     * @param desRfc RFC del documento.
     * @param nomArchivo Nombre original del archivo (puede ser sobrescrito por el nombre del MultipartFile si es nulo/vacío).
     * @param desPath Subdirectorio adicional dentro de la estructura RFC (opcional).
     * @param fechaActual Fecha de carga en formato dd/MM/yyyy (opcional, si es nulo se usa la fecha actual).
     * @return ResponseEntity con el DocumentoIndividualDto actualizado y el estado HTTP.
     */
    @PostMapping(value = "/cargarDocumento", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentoIndividualDto> cargarDocumento(
            @RequestParam("archivo") MultipartFile archivoRecibido,
            @RequestParam("desRfc") String desRfc,
            @RequestParam(value = "nomArchivo", required = false) String nomArchivo,
            @RequestParam(value = "desPath", required = false) String desPath,
            @RequestParam(value = "fechaActual", required = false) String fechaActual) {

        logger.info("------------- Inicio cargarArchivoMultipart en Controller -------------");
        logger.info("Archivo recibido: {}", archivoRecibido.getOriginalFilename());
        logger.debug("Metadatos - RFC: {}, NomArchivo: {}, DesPath: {}, FechaActual: {}", desRfc, nomArchivo, desPath, fechaActual);

        DocumentoIndividualDto responseDto = new DocumentoIndividualDto(); // Objeto para la respuesta

        try {
            // Llama al nuevo método del servicio que acepta MultipartFile
            DocumentoIndividualDto resultado = cargaDocumentoService.cargaDocumentoHadoop(
                    archivoRecibido, desRfc, nomArchivo, desPath, fechaActual);

            logger.info("Archivo cargado exitosamente en HDFS. Código: {}, Mensaje: {}", resultado.getCodigo(), resultado.getMensaje());
            return new ResponseEntity<>(resultado, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.error("Error de argumentos al cargar el archivo (MultipartFile): {}", e.getMessage(), e);
            responseDto.setCodigo(1);
            responseDto.setMensaje("Error de datos.");
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (ParseException e) {
            logger.error("Error de formato de fecha al cargar el archivo (MultipartFile): {}", e.getMessage(), e);
            responseDto.setCodigo(2);
            responseDto.setMensaje("Error en el formato de la fecha." );
            return new ResponseEntity<>(responseDto, HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            logger.error("Error de I/O al interactuar con Hadoop (MultipartFile): {}", e.getMessage(), e);
            responseDto.setCodigo(3);
            responseDto.setMensaje("Error en el sistema de almacenamiento.");
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error inesperado al cargar el archivo (MultipartFile): {}", e.getMessage(), e);
            responseDto.setCodigo(99);
            responseDto.setMensaje("Error al cargar el archivo." );
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            logger.info("------------- Fin cargarArchivoMultipart en Controller -------------");
        }
    }


}