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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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

 

    @GetMapping("/info")
    public ResponseEntity<List<String>> info() {
        logger.info("........................mssideimss-acuses info..............................");
        List<String> list = new ArrayList<>(); // Mejor práctica: usar el diamante operador <>
        list.add("mssideimss-acuses");
        list.add("20251007");
        list.add("Acuses");
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> list() {
        logger.info("........................mssideimss-acuses list..............................");
        List<String> list = new ArrayList<>(); // Mejor práctica: usar el diamante operador <>
        list.add("mssideimss-acuses");
        list.add("20251007");
        list.add("Acuses");
        return new ResponseEntity<>(list, HttpStatus.OK);
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
                    config.getImagePaths().forEach(flatParams::put); 
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
        logger.info("Recibida solicitud para descargar preview de acuse con DTO: {}", plantillaDatoDto);  

        String urlDocumento = plantillaDatoDto.getUrlDocumento();

        if (urlDocumento == null || urlDocumento.isEmpty()) {
            logger.error("urlDocumento no proporcionada en el cuerpo de la solicitud POST.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        DecargarAcuseDto decargarAcuseDto = acuseService.consultaAcuseByUrlDocumento(urlDocumento);

        if (decargarAcuseDto == null || decargarAcuseDto.getCodigo() != 0 || decargarAcuseDto.getDocumento() == null || decargarAcuseDto.getDocumento().isEmpty()) {
            logger.error("Error al obtener el documento o documento vacío. Mensaje: {}", decargarAcuseDto != null ? decargarAcuseDto.getMensaje() : "DTO de descarga es nulo");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return buildPdfResponse(decargarAcuseDto, false);  
    }

    @PostMapping("/descargarAcusePreview")
    public ResponseEntity<byte[]> descargarAcusePreview(@RequestBody PlantillaDatoDto plantillaDatoDto) {
        logger.info("Recibida solicitud para descargar preview de acuse con DTO: {}", plantillaDatoDto);


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Habilitar pretty print

        try {
            String plantillaDatoDtoJson = objectMapper.writeValueAsString(plantillaDatoDto);
            logger.info("PlantillaDatoDto recibido (JSON): \n{}", plantillaDatoDtoJson);
        } catch (Exception e) {
            logger.error("Error al serializar PlantillaDatoDto a JSON para log: {}", e.getMessage());
            logger.info("PlantillaDatoDto recibido (toString por defecto): {}", plantillaDatoDto); // Fallback
        }

        if (plantillaDatoDto.getTipoAcuse() == null) {
            logger.error("Tipo de acuse no proporcionado para la previsualización.");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        DecargarAcuseDto decargarAcuseDto = acuseService.consultaAcuseByPlantillaDato(plantillaDatoDto);

        if (decargarAcuseDto == null || decargarAcuseDto.getCodigo() != 0 || decargarAcuseDto.getDocumento() == null || decargarAcuseDto.getDocumento().isEmpty()) {
            logger.error("Error al obtener el documento o documento vacío para preview. Mensaje: {}", decargarAcuseDto != null ? decargarAcuseDto.getMensaje() : "DTO de descarga es nulo");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return buildPdfResponse(decargarAcuseDto, true);  
    }

    /**
     * Método auxiliar para construir la respuesta HTTP para la descarga/previsualización de PDF.
     * @param decargarAcuseDto DTO con los datos del documento.
     * @param isPreview Indica si es una previsualización (true) o descarga (false).
     * @return ResponseEntity con el PDF.
     */
    private ResponseEntity<byte[]> buildPdfResponse(DecargarAcuseDto decargarAcuseDto, boolean isPreview) {
        try {
            // Asegurarse de que el prefijo "data:application/pdf;base64," no esté presente
            String base64Content = decargarAcuseDto.getDocumento();
            if (base64Content.contains(",")) {
                base64Content = base64Content.split(",")[1];
            }
            byte[] pdfBytes = Base64.getDecoder().decode(base64Content);

            String fileName = decargarAcuseDto.getNombreDocumento();
            if (fileName == null || fileName.isEmpty()) {
                fileName = isPreview ? "preview_acuse.pdf" : "acuse.pdf";
            }
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                fileName += ".pdf";
            }
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replace("+", "%20");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // "inline" para mostrar en navegador, "attachment" para forzar descarga
            String contentDisposition = isPreview ? "inline" : "attachment";
            headers.setContentDispositionFormData(contentDisposition, encodedFileName);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            logger.error("Error al decodificar la cadena Base64 del documento: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error al codificar el nombre del archivo: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error inesperado al construir la respuesta del PDF: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Servicio para generar el request para la firma desde el backend.
     * Recibe los datos necesarios del frontend y devuelve la cadena original y el JSON de firma.
     * @param requestFirmaDto Objeto con los datos necesarios (ej. rfcUsuario)
     * @return Map con la cadena original y el JSON de petición para el widget.
     */
    @PostMapping(value = {"/generaRequestJSONFirmaAcuse"})
    public @ResponseBody Map<String, ? extends Object> generaRequestJSONFirmaAcuse(
            @RequestBody RequestFirmaDto requestFirmaDto) {
        logger.info("generaRequestJSONFirmaAcuse - Inicio");
        Map<String, Object> result = new HashMap<>();

        String rfcUsuario = requestFirmaDto.getRfcUsuario();
        logger.info("generaRequestJSONFirmaAcuse rfcUsuario: {}", rfcUsuario);

        if (rfcUsuario == null || rfcUsuario.isEmpty()) {
            result.put("error", Boolean.TRUE);
            result.put("mensaje", "RFC de usuario es requerido para generar la petición de firma.");
            return result;
        }

        Date fechaActual = new Date(); // Captura la fecha y hora 

        String desFolio = requestFirmaDto.getDesFolio();
        logger.info("generaRequestJSONFirmaAcuse desFolio: {}", desFolio);
        JSONObject jsonWidget = new JSONObject();
        String requestFirmaFiel = null;

        try {
            // Formateadores de fecha inicializados una vez para esta operación
            SimpleDateFormat sdfAcuse = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy, HH:mm:ss", new Locale("es", "MX"));
            SimpleDateFormat sdfFechaCadena = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "MX"));
            SimpleDateFormat sdfHoraCadena = new SimpleDateFormat("HH:mm:ss", new Locale("es", "MX"));

            
            String fechaParaAcuse = sdfAcuse.format(fechaActual);
            String fechaParaCadenaOriginal = sdfFechaCadena.format(fechaActual);
            String horaParaCadenaOriginal = sdfHoraCadena.format(fechaActual);

            // La cadena original se arma completamente en el backend
            String cadenaOriginal = "||VERSIÓN DEL ACUSE|1.0|INVOCANTE|" + requestFirmaDto.getNombreCompleto() +
                    "|FOLIO DEL ACUSE|" + desFolio +
                    "|FECHA|" + fechaParaCadenaOriginal +
                    "|HORA|" + horaParaCadenaOriginal +
                    "|RFC|" + rfcUsuario +
                    "|CURP|" + requestFirmaDto.getDesCurp() +
                    "|HASH|" + desFolio +
                    "|ACTO|"+requestFirmaDto.getActo() + "||";
            logger.info("cadenaOriginal: {}", cadenaOriginal);

            // Lógica para armar el JSON del widget de firma
            jsonWidget.put("operacion", "firmaCMS");
            jsonWidget.put("aplicacion", "GENERICO_ID_OP");
            jsonWidget.put("rfc", rfcUsuario);
            jsonWidget.put("acuse", "GENERICO_ACUSE");
            jsonWidget.put("cad_original", cadenaOriginal);
            jsonWidget.put("salida", "cert,rfc,curp,rfc_rl,curp_rl,vigIni,vigFin,acuse,cadori,folio,firmas");
            jsonWidget.put("desFolio", desFolio);
             

            requestFirmaFiel = jsonWidget.toString();

            // Reemplazo de caracteres especiales si es necesario (el frontend ya hace esto, pero es bueno tenerlo centralizado)
            // Considerar el uso de StringEscapeUtils de Apache Commons Lang si hay muchos reemplazos.
            requestFirmaFiel = requestFirmaFiel.replaceAll("ñ", "\\u00d1").replaceAll("Ñ", "\\u00D1");

            result.put("fechaParaAcuse", fechaParaAcuse);
            result.put("cad_original", cadenaOriginal);
            result.put("peticionJSON", requestFirmaFiel);
            result.put("error", Boolean.FALSE);
            result.put("mensaje", "Petición de firma generada exitosamente.");

        } catch (Exception e) {
            logger.error("Error al generar JSON de firma: {}. Causa: {}", e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "Desconocida", e);
            result.put("peticionJSON", requestFirmaFiel);
            result.put("error", Boolean.TRUE);
            result.put("mensaje", "Error interno al generar la petición de firma: " + e.getMessage());
        }

        logger.info("generaRequestJSONFirmaAcuse - Fin");
        return result;
    }


    /**
     * Método POST para generar el sello a partir de una cadena original.
     * Recibe un objeto CadenaOriginalRequestDto y devuelve un SelloResponseDto.
     * @param cadenaOriginalRequestDto Objeto que contiene la cadenaOriginal.
     * @return ResponseEntity con el objeto SelloResponseDto (sello, codigo, mensaje).
     */
    @PostMapping("/generaSello")
    public ResponseEntity<SelloResponseDto> generaSello(@RequestBody CadenaOriginalRequestDto cadenaOriginalRequestDto) {
        logger.info("Recibida solicitud para generar sello."); // Evita loguear toda la cadena original, puede ser muy larga.

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
            logger.info("Sello generado exitosamente."); // No loguear el sello, puede ser información sensible.
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al generar el sello. Mensaje: {}. Causa: {}", e.getMessage(), e.getCause() != null ? e.getCause().getMessage() : "Desconocida", e);
            response.setCodigo(1); // Error
            response.setMensaje("Error al generar el sello: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}