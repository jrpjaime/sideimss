package mx.gob.imss.acuses.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import mx.gob.imss.acuses.dto.AcuseConfig;
import mx.gob.imss.acuses.dto.DecargarAcuseDto;
import mx.gob.imss.acuses.dto.PlantillaDatoDto;
import mx.gob.imss.acuses.enums.TipoAcuse;
import mx.gob.imss.acuses.model.NdtPlantillaDato;
import mx.gob.imss.acuses.repository.NdtPlantillaDatoRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64; // Importar Base64
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

 

@Service("acuseService")
public class AcuseServiceImpl implements AcuseService {
	private static final Logger logger = LogManager.getLogger(AcuseServiceImpl.class); 


	@Autowired
	private UtileriasService utileriasService;


     @Autowired
    private NdtPlantillaDatoRepository ndtPlantillaDatoRepository;


    @Autowired
    private AcuseConfigService acuseConfigService; // Inyecta el nuevo servicio

     @Override
    @Transactional
    public DecargarAcuseDto consultaAcuseByUrlDocumento(String urlDocumento) {
        DecargarAcuseDto decargarAcuseDto = new DecargarAcuseDto();
        decargarAcuseDto.setCodigo(1);
        decargarAcuseDto.setMensaje("Error en acuse.");

        try {
            // Decodificar la URL Base64
            //String decodedUrl = new String(Base64.getUrlDecoder().decode(urlDocumento), "UTF-8");
            //logger.info("URL decodificada: {}", decodedUrl);

            // Desencriptar la cadena (si aplica, según tu UtileriasService)
            String filename = utileriasService.desencriptar(urlDocumento); // Aquí se usa la URL decodificada, no la base64
            logger.info("Filename después de desencriptar: {}", filename);


            StringTokenizer tokens = new StringTokenizer(filename, "|");
            int nDatos = tokens.countTokens();

            if (nDatos < 2) { // Debe haber al menos RFC y cveIdPlantillaDato
                throw new IllegalArgumentException("Formato de URL de documento inválido: Faltan datos.");
            }

            String[] datos = new String[nDatos];
            Integer i = 0;
            while (tokens.hasMoreTokens()) {
                String str = tokens.nextToken();
                datos[i] = str;
                logger.info("Token: {}", str);
                i++;
            }

            String rfc = datos[0];
            logger.info("RFC: {}", rfc);
            Long cveIdNdtPlantillaDato = Long.parseLong(datos[1]); 
            logger.info("cveIdNdtPlantillaDato: {}", cveIdNdtPlantillaDato);

            // Buscar en el nuevo repositorio
            NdtPlantillaDato ndtPlantillaDato = ndtPlantillaDatoRepository.findById(cveIdNdtPlantillaDato)
                    .orElseThrow(() -> new RuntimeException("NdtPlantillaDato no encontrada con ID: " + cveIdNdtPlantillaDato));
            logger.info("NdtPlantillaDato encontrada. desDatos: {}", ndtPlantillaDato.getDesDatos()); 
     

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> desDatosMap = objectMapper.readValue(ndtPlantillaDato.getDesDatos(), new TypeReference<Map<String, Object>>() {});

            String desVersionDelJson = (String) desDatosMap.get("desVersion");
            String nomDocumentoDelJson = (String) desDatosMap.get("nomDocumento");

            if (desVersionDelJson == null || nomDocumentoDelJson == null) {
                logger.error("El JSON en desDatos no contiene 'desVersion' o 'nomDocumento'.");
                throw new RuntimeException("El JSON de la plantilla de datos debe contener 'desVersion' y 'nomDocumento'.");
            }

 
            TipoAcuse tipoAcuseDeterminado = TipoAcuse.valueOf(ndtPlantillaDato.getDesTipoAcuse().toUpperCase()); 

            PlantillaDatoDto plantillaDatoDto = new PlantillaDatoDto();
            plantillaDatoDto.setCveIdPlantillaDatos(ndtPlantillaDato.getCveIdPlantillaDato());  
            plantillaDatoDto.setDatosJson(ndtPlantillaDato.getDesDatos());
            plantillaDatoDto.setTipoAcuse(tipoAcuseDeterminado);
         
            plantillaDatoDto.setDesVersion(desVersionDelJson); 
            plantillaDatoDto.setNomDocumento(nomDocumentoDelJson);  

            // Generar el acuse y obtener los bytes del PDF, usando el DTO
            byte[] pdfBytes = generarAcuseconDatosJSON(plantillaDatoDto);

            String nombreDocumento = nomDocumentoDelJson;
             logger.info("Nombre documento: {}", nombreDocumento);
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            decargarAcuseDto.setDocumento("data:application/pdf;base64," + pdfBase64);
            decargarAcuseDto.setNombreDocumento(nombreDocumento + ".pdf");
            decargarAcuseDto.setCodigo(0);
            decargarAcuseDto.setMensaje("Acuse generado y codificado exitosamente.");

        } catch (IllegalArgumentException e) {
            logger.error("Error en consultaAcuseByUrlDocumento (Argumento Inválido): {}", e.getMessage());
            decargarAcuseDto.setCodigo(1);
            decargarAcuseDto.setMensaje("Error al procesar el acuse: " + e.getMessage());
        } catch (RuntimeException e) { // Captura el error de "no encontrada" y de "no se encontró configuración"
            logger.error("Error en consultaAcuseByUrlDocumento (Runtime): {}", e.getMessage());
            decargarAcuseDto.setCodigo(1);
            decargarAcuseDto.setMensaje("Error al procesar el acuse: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado en consultaAcuseByUrlDocumento: {}", e.getMessage(), e);
            decargarAcuseDto.setCodigo(1);
            decargarAcuseDto.setMensaje("Error inesperado al procesar el acuse: " + e.getMessage());
        }
        return decargarAcuseDto;
    }



private byte[] generarAcuseconDatosJSON(PlantillaDatoDto plantillaDatoDto) throws JRException, java.io.IOException, Exception {
        logger.info("generarAcuseconDatosJSON Iniciando generación de acuse con datos JSON desde PlantillaDatoDto...");

        String datosJSON = plantillaDatoDto.getDatosJson();
        logger.info("Datos JSON recibidos: {}", datosJSON);



    ObjectMapper objectMapper = new ObjectMapper(); // Ya tienes uno, puedes reutilizarlo o crear uno nuevo aquí para el log
/*    
    try {
        Object json = objectMapper.readValue(datosJSON, Object.class); // Leer como Object para manejar array o mapa
        String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        logger.info("Datos JSON recibidos (formateados): \n{}", prettyJson);
    } catch (Exception e) {
        logger.error("Error al formatear y loggear el JSON: {}", e.getMessage());
        logger.info("Datos JSON recibidos (sin formatear): {}", datosJSON); // Imprimir sin formatear si falla
    }

*/

       
        Map<String, Object> allDataMap = new HashMap<>(); // Para almacenar todos los datos del JSON
        String desVersion = null; // Variable para almacenar desVersion

        // Primero, intentar parsear el JSON para obtener desVersion y otros datos
        try {
            // Intentar como objeto único para extraer desVersion y otros parámetros
            allDataMap = objectMapper.readValue(datosJSON, new TypeReference<Map<String, Object>>() {});
            if (allDataMap.containsKey("desVersion")) {
                desVersion = (String) allDataMap.get("desVersion");
                logger.info("desVersion obtenida del JSON: {}", desVersion);
            } else {
                logger.error("El JSON no contiene el campo 'desVersion'. Es necesario para la plantilla.");
                throw new JRException("El campo 'desVersion' es requerido en el JSON para identificar la plantilla.");
            }
        } catch (Exception e) {
       
            logger.error("Error al intentar parsear el JSON para obtener 'desVersion' inicial: {}", e.getMessage());
            throw new JRException("Error al procesar el JSON para la plantilla: " + e.getMessage());
        }

        // Usar desVersion obtenida del JSON
        String desVersionPlantillaPath = desVersion.replace("\\", "/") + ".jasper"; 

        logger.info("Plantilla Jasper a usar: {}", desVersionPlantillaPath);

        InputStream jasperStream = this.getClass().getClassLoader().getResourceAsStream(desVersionPlantillaPath);
        if (jasperStream == null) {
            logger.error("No se encontró la plantilla Jasper: {}", desVersionPlantillaPath);
            throw new JRException("No se encontró la plantilla Jasper: " + desVersionPlantillaPath);
        }

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

        JRDataSource dataSource;
        Map<String, Object> parameters = new HashMap<>();
        String cadenaOriginal = null;

        // Ahora, determinar si el JSON es una lista o un objeto único para la fuente de datos
        try {
            List<Map<String, Object>> dataList = objectMapper.readValue(datosJSON, new TypeReference<List<Map<String, Object>>>(){});
            dataSource = new JRBeanCollectionDataSource(dataList);
            logger.info("JSON interpretado como lista de objetos para el dataSource.");
            // Si es una lista, los parámetros se toman del primer elemento o se manejan de otra forma.
            // Para este caso, vamos a tomar los parámetros del allDataMap que ya procesamos inicialmente
            parameters.putAll(allDataMap); 

        } catch (Exception e) {
            logger.info("JSON interpretado como objeto único para el dataSource.");
            // Si es un objeto único, allDataMap ya contiene todos los datos
            parameters.putAll(allDataMap);
            dataSource = new JREmptyDataSource(1);
        }

        // Buscar 'cadenaOriginal' en los parámetros
        if (parameters.containsKey("cadenaOriginal")) {
            Object value = parameters.get("cadenaOriginal");
            if (value instanceof String) {
                cadenaOriginal = (String) value;
                logger.info("Cadena Original obtenida del JSON: {}", cadenaOriginal);
            } else {
                logger.warn("El valor de 'cadenaOriginal' no es un String en el JSON.");
            }
        }
 
        
  
        if (cadenaOriginal != null) {
            InputStream qrImage = utileriasService.generaQRImageInputStream(cadenaOriginal);
            parameters.put("qrcode", qrImage);
            logger.info("Parámetro 'qrcode' (InputStream) añadido para la Cadena Original.");
        }
        
        // --- INICIO DE LA VERIFICACIÓN DE PARÁMETROS ---
        logger.info("Verificando los parámetros que se enviarán a JasperReports:");
        parameters.forEach((key, value) -> {
            logger.info("  Parámetro: {} = {}", key, (value instanceof InputStream) ? "InputStream (no imprimible directamente)" : value);
        });
        logger.info("Fin de la verificación de parámetros.");
        // --- FIN DE LA VERIFICACIÓN DE PARÁMETROS ---

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

        SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);

        SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
        exportConfig.setMetadataAuthor("IMSS");

        exporter.setConfiguration(reportConfig);
        exporter.setConfiguration(exportConfig);
        exporter.exportReport();

        logger.info("Acuse generado exitosamente.");
        return baos.toByteArray();
    }


    
    @Override
    public DecargarAcuseDto consultaAcuseByPlantillaDato(PlantillaDatoDto plantillaDatoDto) {
        logger.info("Procesando consultaAcuseByPlantillaDato sin conexión a BD...");

        DecargarAcuseDto decargarAcuseDto = new DecargarAcuseDto();
        decargarAcuseDto.setCodigo(1); // Por defecto error

        try { 
            // Aquí el PlantillaDatoDto ya debería tener el tipoAcuse
            byte[] pdfBytes = generarAcuseconDatosJSON(plantillaDatoDto);

            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            decargarAcuseDto.setDocumento("data:application/pdf;base64," + pdfBase64);
            // Usar el nomDocumento de la configuración centralizada
            decargarAcuseDto.setNombreDocumento(plantillaDatoDto.getNomDocumento() + ".pdf");
            decargarAcuseDto.setCodigo(0);
            decargarAcuseDto.setMensaje("Acuse generado exitosamente sin conexión a BD.");

        } catch (Exception e) {
            logger.error("Error al generar el acuse: {}", e.getMessage(), e);
            decargarAcuseDto.setMensaje("Error al generar acuse: " + e.getMessage());
        }

        return decargarAcuseDto;
    }



    /**
     * endpoint para obtener la configuración de un tipo de acuse específico.
     * @param tipoAcuseString El nombre del tipo de acuse como String (ej. "ACREDITACION_MEMBRESIA").
     * @return Un objeto AcuseConfig con la configuración detallada del tipo de acuse.
     */
    @GetMapping("/configuracionAcuse")
    public ResponseEntity<AcuseConfig> getAcuseConfig(@RequestParam("tipoAcuse") String tipoAcuse) {
        logger.info("Recibida solicitud para obtener configuración del acuse tipo: {}", tipoAcuse);
        
        try {
            TipoAcuse tipoAcuseIdentificado = TipoAcuse.valueOf(tipoAcuse.toUpperCase());
            AcuseConfig config = acuseConfigService.getConfigForType(tipoAcuseIdentificado);
            
            if (config != null) {
                return new ResponseEntity<>(config, HttpStatus.OK);
            } else {
                logger.warn("No se encontró configuración para el tipo de acuse: {}", tipoAcuse);
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (IllegalArgumentException e) {
            logger.error("Tipo de acuse inválido: {}. Error: {}", tipoAcuse, e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Retorna 400 si el enum no es válido
        } catch (Exception e) {
            logger.error("Error inesperado al obtener la configuración del acuse para tipo {}: {}", tipoAcuse, e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}