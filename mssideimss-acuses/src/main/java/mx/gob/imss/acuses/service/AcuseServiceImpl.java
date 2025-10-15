package mx.gob.imss.acuses.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import mx.gob.imss.acuses.dto.AcuseConfig;
import mx.gob.imss.acuses.dto.DecargarAcuseDto;
import mx.gob.imss.acuses.dto.PlantillaDatoDto;
import mx.gob.imss.acuses.enums.TipoAcuse;
import mx.gob.imss.acuses.model.PlantillaDato;
import mx.gob.imss.acuses.repository.PlantillaDatosRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.engine.util.JRLoader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Base64; // Importar Base64
import java.util.Collections;
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
    public static final String FORMATO_dd_MM_yyyy_HH_mm_ss = "dd/MM/yyyy HH:mm:ss";


	@Autowired
	private UtileriasService utileriasService;


	@Autowired
	private PlantillaDatosRepository plantillaDatosRepository;


    @Autowired
    private AcuseConfigService acuseConfigService; // Inyecta el nuevo servicio

    @Override
    @Transactional
    public DecargarAcuseDto consultaAcuseByUrlDocumento(String urlDocumento) {
        DecargarAcuseDto decargarAcuseDto = new DecargarAcuseDto();
        decargarAcuseDto.setCodigo(1); 
        decargarAcuseDto.setMensaje("Error en acuse.");

        try {
            String filename = utileriasService.desencriptar(urlDocumento);
            logger.info("1filename: " + filename );
            StringTokenizer tokens = new StringTokenizer(filename, "|");
            int nDatos = tokens.countTokens();

            String[] datos = new String[nDatos];
            Integer i = 0;
            while(tokens.hasMoreTokens()){
                String str = tokens.nextToken();
                datos[i] = str;
                logger.info("str: " + str);
                i++;
            }

            String rfc = datos[0];
            logger.info("rfc: " + rfc);
            Long cveIdPlantillaDatos = Long.parseLong( datos[1]);
            logger.info("cveIdPlantillaDatos: " + cveIdPlantillaDatos);

            PlantillaDato plantillaDato = plantillaDatosRepository.findById(cveIdPlantillaDatos)
                                            .orElseThrow(() -> new RuntimeException("PlantillaDato no encontrada con ID: " + cveIdPlantillaDatos));
            logger.info("plantillaDato: " + plantillaDato.getDesDatos());

            // Aquí necesitarás inferir el TipoAcuse basado en el registro de PlantillaDato
            // Esto puede ser un nuevo campo en PlantillaDato o una lógica para determinarlo.
            // Por ahora, asumiré que PlantillaDato.getDesVersion() o similar te da una pista.
            // Lo ideal es tener un campo 'tipoAcuse' en la tabla PlantillaDato también.
            // Para el ejemplo, usaré un valor fijo, pero esto debe ser dinámico.
            // Podrías tener un método en PlantillaDato que devuelva TipoAcuse.
            // O, si `desVersion` tiene un prefijo claro, podrías parsearlo.
            // Ejemplo (necesitarás implementar la lógica real):
            // TipoAcuse tipoAcuse = inferirTipoAcuseDePlantillaDato(plantillaDato); 
            // AcuseConfig config = acuseConfigService.getConfigForType(tipoAcuse);
            
            // **IMPORTANTE:** Aquí necesitas una manera de obtener el TipoAcuse
            // desde la `PlantillaDato` que recuperaste de la base de datos.
            // Si no tienes un campo `tipoAcuse` en `PlantillaDato`, puedes:
            // 1. Añadirlo a la tabla `PlantillaDato` y a la entidad.
            // 2. Inferirlo de `plantillaDato.getDesVersion()` si sigue un patrón.
            // Por ejemplo, si `desVersion` contiene "acreditacionmenbresia", es `ACREDITACION_MEMBRESIA`.
            
            // Para este ejemplo, simularé la obtención del tipo de acuse:
            // Esto es solo un placeholder, la lógica real debe venir de tu modelo/BD.
            String desVersionPlantilla = plantillaDato.getDesVersion();
            mx.gob.imss.acuses.enums.TipoAcuse tipoAcuseDeterminado;
            if (desVersionPlantilla.contains("acreditacionmenbresia")) {
                tipoAcuseDeterminado = mx.gob.imss.acuses.enums.TipoAcuse.ACREDITACION_MEMBRESIA;
            } else if (desVersionPlantilla.contains("solicitudes\\cambio")) {
                tipoAcuseDeterminado = mx.gob.imss.acuses.enums.TipoAcuse.ACUSE_SOLICITUD_CAMBIO;
            } else {
                tipoAcuseDeterminado = mx.gob.imss.acuses.enums.TipoAcuse.DEFAULT; // O lanza un error
            }
            // Fin del placeholder.

            AcuseConfig config = acuseConfigService.getConfigForType(tipoAcuseDeterminado);

        
            PlantillaDatoDto plantillaDatoDto = new PlantillaDatoDto();
            plantillaDatoDto.setCveIdPlantillaDatos(plantillaDato.getCveIdPlantillaDatos());
        
            plantillaDatoDto.setDatosJson(plantillaDato.getDesDatos());
            plantillaDatoDto.setTipoAcuse(tipoAcuseDeterminado); 

            // Generar el acuse y obtener los bytes del PDF, usando el DTO
            byte[] pdfBytes = generarAcuseconDatosJSON(plantillaDatoDto);

            String nombreDocumento = config.getNomDocumento(); // Usar el nombre del documento de la configuración
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            decargarAcuseDto.setDocumento("data:application/pdf;base64," + pdfBase64);
            decargarAcuseDto.setNombreDocumento(nombreDocumento + ".pdf");
            decargarAcuseDto.setCodigo(0);
            decargarAcuseDto.setMensaje("Acuse generado y codificado exitosamente.");

        } catch (Exception e) {
            logger.error("Error en consultaAcuseByUrlDocumento: {}", e.getMessage(), e);
            decargarAcuseDto.setCodigo(1);
            decargarAcuseDto.setMensaje("Error al procesar el acuse: " + e.getMessage());
        }
        return decargarAcuseDto;
    }


private byte[] generarAcuseconDatosJSON(PlantillaDatoDto plantillaDatoDto) throws JRException, java.io.IOException, Exception {
        logger.info("Iniciando generación de acuse con datos JSON desde PlantillaDatoDto...");

        String datosJSON = plantillaDatoDto.getDatosJson();
        logger.info("Datos JSON recibidos: {}", datosJSON);

        ObjectMapper objectMapper = new ObjectMapper();
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

        SimpleDateFormat formatter = new SimpleDateFormat(FORMATO_dd_MM_yyyy_HH_mm_ss);
        
        // Obtener la fecha actual formateada
        String fechaActual = formatter.format(new Date());

        parameters.put("fecha", fechaActual);

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