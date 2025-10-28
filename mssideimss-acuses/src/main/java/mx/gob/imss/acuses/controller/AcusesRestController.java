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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

 
import java.net.URLEncoder; 
 
import org.springframework.http.HttpHeaders;  
import org.springframework.http.MediaType;

import mx.gob.imss.acuses.dto.AcuseConfig;
import mx.gob.imss.acuses.dto.CadenaOriginalRequestDto;
import mx.gob.imss.acuses.dto.DecargarAcuseDto;
import mx.gob.imss.acuses.dto.PlantillaDatoDto;
import mx.gob.imss.acuses.dto.RequestFirmaDto;
import mx.gob.imss.acuses.dto.SelloResponseDto;
import mx.gob.imss.acuses.enums.TipoAcuse;
import mx.gob.imss.acuses.service.AcuseConfigService;
import mx.gob.imss.acuses.service.AcuseService;
import mx.gob.imss.acuses.service.SelloService;

import org.json.JSONObject;


@Controller
@CrossOrigin("*") 
@RequestMapping("/mssideimss-acuses/v1")
public class AcusesRestController {
	private final static Logger logger = LoggerFactory.getLogger(AcusesRestController.class);

	@Autowired
	private AcuseService acuseService;


    @Autowired  
    private AcuseConfigService acuseConfigService;


    @Autowired
    private SelloService selloService; 
  

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



    /**
     * endpoint para obtener la configuración de un tipo de acuse específico
     * como una lista plana de parámetros para JasperReports.
     * @param tipoAcuse El nombre del tipo de acuse como String (ej. "ACREDITACION_MEMBRESIA").
     * @return Un Map<String, String> con la configuración detallada del tipo de acuse
     *         donde las claves son los nombres de los parámetros y los valores son sus String.
     */
    @GetMapping("/getAcuseConfig")
    public ResponseEntity<Map<String, String>> getAcuseConfig(@RequestParam("tipoAcuse") String tipoAcuse) {
        logger.info("Recibida solicitud para obtener configuración del acuse tipo: {}", tipoAcuse);
        
        try {
            TipoAcuse tipoAcuseIdentificado = TipoAcuse.valueOf(tipoAcuse.toUpperCase());
            AcuseConfig config = acuseConfigService.getConfigForType(tipoAcuseIdentificado);
            
            if (config != null) {
                Map<String, String> flatParams = new HashMap<>();
                
                // Añadir los campos directos de AcuseConfig
                if (config.getNomDocumento() != null) {
                    flatParams.put("nomDocumento", config.getNomDocumento());
                }
                if (config.getDesVersion() != null) {
                    flatParams.put("desVersion", config.getDesVersion());
                }
                
                // Desanidar los imagePaths
                if (config.getImagePaths() != null) {
                    config.getImagePaths().forEach((key, value) -> {
                        flatParams.put(key, value);
                    });
                }
                
                return new ResponseEntity<>(flatParams, HttpStatus.OK);
            } else {
                logger.warn("No se encontró configuración para el tipo de acuse: {}", tipoAcuse);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Tipo de acuse inválido: {}. Error: {}", tipoAcuse, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error inesperado al obtener la configuración del acuse para tipo {}: {}", tipoAcuse, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
 
 
	
    @PostMapping("/descargarAcuse")  
    public ResponseEntity<byte[]> descargarAcuse(@RequestBody PlantillaDatoDto plantillaDatoDto) {
        logger.info("Recibida solicitud para descargar preview de acuse con DTO: " + plantillaDatoDto.toString());                

        String urlDocumento = plantillaDatoDto.getUrlDocumento(); // Extrae urlDocumento del body

         logger.info("descargarAcuse: {}", urlDocumento);

        if (urlDocumento == null || urlDocumento.isEmpty()) {
            logger.error("urlDocumento no proporcionada en el cuerpo de la solicitud POST.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        DecargarAcuseDto decargarAcuseDto = acuseService.consultaAcuseByUrlDocumento(urlDocumento);

        if (decargarAcuseDto.getCodigo() != 0 || decargarAcuseDto.getDocumento() == null || decargarAcuseDto.getDocumento().isEmpty()) {
            logger.error("Error al obtener el documento o documento vacío. Mensaje: " + decargarAcuseDto.getMensaje());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            String base64Content = decargarAcuseDto.getDocumento().split(",")[1];
            byte[] pdfBytes = Base64.getDecoder().decode(base64Content);
            String fileName = decargarAcuseDto.getNombreDocumento();
            if (fileName == null || fileName.isEmpty()) {
                fileName = "acuse.pdf"; // Nombre por defecto 
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
            logger.error("Error al decodificar la cadena Base64 del documento: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error al codificar el nombre del archivo: " + e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error inesperado al descargar el acuse: " + e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
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
        String desFolio = "SIDEIMSS"+ "FOLIOSSSSS";
        logger.info("generaRequestJSONFirmaAcuse desFolio: " + desFolio );
        JSONObject jsonWidget = new JSONObject();
        String requestFirmaFiel = null; 

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "MX"));
            SimpleDateFormat sdfhora = new SimpleDateFormat("HH:mm:ss", new Locale("es", "MX"));
            String fecha = sdf.format(fechaActual);
             String hora = sdfhora.format(fechaActual);

            // La cadena original se arma completamente en el backend
            String cadenaOriginal = "||VERSIÓN DEL ACUSE|1.0|INVOCANTE|" + rfcUsuario + "|FOLIO DEL ACUSE|"+desFolio+ "|FECHA|"+ fecha+ "|HORA|"+ hora + "|RFC|"+rfcUsuario+ "|CURP|"+ rfcUsuario+ "|HASH|"+ rfcUsuario+ "|ACTO|Acreditación o Membresía||";
            logger.info("cadenaOriginal: " + cadenaOriginal );
            
            // Lógica para armar el JSON del widget de firma
            jsonWidget.put("operacion","firmaCMS");
            jsonWidget.put("aplicacion","GENERICO_ID_OP");
            jsonWidget.put("rfc",rfcUsuario);
            jsonWidget.put("acuse","GENERICO_ACUSE");
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






  /**
     * Método POST para generar el sello a partir de una cadena original.
     * Recibe un objeto CadenaOriginalRequestDto y devuelve un SelloResponseDto.
     * @param requestDto Objeto que contiene la cadenaOriginal.
     * @return ResponseEntity con el objeto SelloResponseDto (sello, codigo, mensaje).
     */
    @PostMapping("/generaSello")
    public ResponseEntity<SelloResponseDto> generaSello(@RequestBody CadenaOriginalRequestDto cadenaOriginalRequestDto) {
        logger.info("Recibida solicitud para generar sello con cadena original: {}", cadenaOriginalRequestDto.getCadenaOriginal());

        SelloResponseDto response = new SelloResponseDto();

        if (cadenaOriginalRequestDto.getCadenaOriginal() == null || cadenaOriginalRequestDto.getCadenaOriginal().trim().isEmpty()) {
            response.setCodigo(1); // Error
            response.setMensaje("La cadenaOriginal no puede estar vacía.");
            logger.warn("CadenaOriginal vacía en la solicitud de sello.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            String selloGenerado = selloService.generarSelloDigital(cadenaOriginalRequestDto);
            response.setSello(selloGenerado);
            response.setCodigo(0); // Correcto
            response.setMensaje("Sello generado exitosamente.");
            logger.info("Sello generado exitosamente para la cadena original.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al generar el sello para la cadena original: {}. Error: {}", cadenaOriginalRequestDto.getCadenaOriginal(), e.getMessage(), e);
            response.setCodigo(1); // Error
            response.setMensaje("Error al generar el sello: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }    



}