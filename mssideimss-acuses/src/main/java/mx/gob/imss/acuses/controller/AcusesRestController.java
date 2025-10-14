package mx.gob.imss.acuses.controller;

 
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;  
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
 
import java.net.URLEncoder; 
 
import org.springframework.http.HttpHeaders;  
import org.springframework.http.MediaType;


import mx.gob.imss.acuses.dto.DecargarAcuseDto;
import mx.gob.imss.acuses.dto.PlantillaDatoDto;
import mx.gob.imss.acuses.dto.RequestFirmaDto;
import mx.gob.imss.acuses.service.AcuseService;

 import org.json.JSONObject;


@Controller
@CrossOrigin("*") 
@RequestMapping("/mssideimss-acuses/v1")
public class AcusesRestController {
	private final static Logger logger = LoggerFactory.getLogger(AcusesRestController.class);

	@Autowired
	private AcuseService acuseService;
  

    public static final String FORMATO_dd_MM_yyyy_HH_mm_ss = "dd/MM/yyyy HH:mm:ss";
 
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
 
 
 

   
    @PostMapping("/descargarAcusePreview")
    public ResponseEntity<byte[]> descargarAcusePreview(@RequestBody PlantillaDatoDto plantillaDatoDto) {
        logger.info("Recibida solicitud para descargar preview de acuse con DTO: " + plantillaDatoDto.toString());
        
        if (plantillaDatoDto.getTipoAcuse() == null  ) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        DecargarAcuseDto decargarAcuseDto = acuseService.consultaAcuseByPlantillaDato(plantillaDatoDto);

        logger.info("getMensaje: " + decargarAcuseDto.getMensaje());

        if (decargarAcuseDto.getCodigo() != 0 || decargarAcuseDto.getDocumento() == null || decargarAcuseDto.getDocumento().isEmpty()) {
            logger.error("Error al obtener el documento o documento vacío para preview. Mensaje: " + decargarAcuseDto.getMensaje());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Retorna 404 Not Found si no se encuentra o hay error
        }
        logger.info(" String base64Content = " );
        try {
            String base64Content = decargarAcuseDto.getDocumento().split(",")[1];
            byte[] pdfBytes = Base64.getDecoder().decode(base64Content);

            String fileName = decargarAcuseDto.getNombreDocumento();
            if (fileName == null || fileName.isEmpty()) {
                fileName = "preview_acuse.pdf"; // Nombre por defecto para la previsualización
            }
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("inline", encodedFileName); // "inline" para que el navegador lo muestre en lugar de descargarlo
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.error("Error al decodificar la cadena Base64 del documento de preview: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error al codificar el nombre del archivo de preview: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error inesperado al descargar el preview del acuse: " + e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
 
    /**
     * Nuevo servicio para generar el request para la firma desde el backend.
     * Recibe los datos necesarios del frontend y devuelve la cadena original y el JSON de firma.
     * @param requestFirmaDto Objeto con los datos necesarios (ej. rfcUsuario)
     * @return Map con la cadena original y el JSON de petición para el widget.
     */
    @PostMapping(value = {"/generaRequestJSONFirmaAcuse"}) // Cambiado a POST
    public @ResponseBody Map<String, ? extends Object> generaRequestJSONFirmaAcuse(
            @RequestBody RequestFirmaDto requestFirmaDto){ // Recibe el DTO
        logger.info("generaRequestJSONFirmaAcuse - Inicio");
        Map<String , Object> result = new HashMap<String,Object>();
        
        String rfcUsuario = requestFirmaDto.getRfcUsuario(); // Obtiene RFC del DTO
        logger.info("generaRequestJSONFirmaAcuse rfcUsuario: " + rfcUsuario);
        
        if (rfcUsuario == null || rfcUsuario.isEmpty()) {
            result.put("error", Boolean.TRUE);
            result.put("mensaje", "RFC de usuario es requerido para generar la petición de firma.");
            return result;
        }

        Date fechaActual = new Date();
        
        // Genera un folio nuevo (asumiendo que utileriasService ya existe)
        String desFolio = "BI"+ "FOLIOSSSSS";
        logger.info("generaRequestJSONFirmaAcuse desFolio: " + desFolio );
        JSONObject jsonWidget = new JSONObject();
        String requestFirmaFiel = null; 

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", new Locale("es", "MX"));
            String fechafolio = sdf.format(fechaActual);

            // La cadena original se arma completamente en el backend
            String cadenaOriginal = "|Folio del acuse:"+desFolio+"|Rfc:"+rfcUsuario+"|Tipo de trámite:Acuse|Fecha de elaboración:"+fechafolio+"|";
            logger.info("cadenaOriginal: " + cadenaOriginal );
            
            // Lógica para armar el JSON del widget de firma
            jsonWidget.put("operacion","firmaCMS");
            jsonWidget.put("aplicacion","buzonTributario");
            jsonWidget.put("rfc",rfcUsuario);
            jsonWidget.put("acuse","BZN_ACUSE_NOT");
            jsonWidget.put("cad_original",cadenaOriginal);
            jsonWidget.put("salida","cert,rfc,curp,rfc_rl,curp_rl,vigIni,vigFin,acuse,cadori,folio,firmas");
            jsonWidget.put("desFolio",desFolio);

            requestFirmaFiel = jsonWidget.toString();
            
            // Reemplazo de caracteres especiales si es necesario (el frontend ya hace esto, pero es bueno tenerlo centralizado)
            requestFirmaFiel = requestFirmaFiel.replaceAll("ñ", "\\u00d1").replaceAll("Ñ", "\\u00D1");


            result.put("cad_original", cadenaOriginal);
            result.put("peticionJSON", requestFirmaFiel);
            result.put("error", Boolean.FALSE);
            result.put("mensaje", "Petición de firma generada exitosamente.");

        }catch (Exception e){
            logger.error("Error al generar JSON de firma: " + e.getMessage(), e);
            result.put("peticionJSON", requestFirmaFiel); // Podría ser null en caso de error temprano
            result.put("error", Boolean.TRUE);
            result.put("mensaje", "Error interno al generar la petición de firma: " + e.getMessage());
        }

        logger.info("generaRequestJSONFirmaAcuse - Fin");
        return result;
    }

}