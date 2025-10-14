package mx.gob.imss.acuses.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import mx.gob.imss.acuses.dto.AcuseConfig;
import mx.gob.imss.acuses.dto.DecargarAcuseDto;
import mx.gob.imss.acuses.dto.PlantillaDatoDto;
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
import org.springframework.stereotype.Service;

 

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

            // Convertir PlantillaDato a PlantillaDatoDto para el método actualizado
            PlantillaDatoDto plantillaDatoDto = new PlantillaDatoDto();
            plantillaDatoDto.setCveIdPlantillaDatos(plantillaDato.getCveIdPlantillaDatos());
            // Estos campos ahora vendrán de la configuración, no directamente del DTO/BD para `generarAcuseconDatosJSON`
            // plantillaDatoDto.setNomDocumento(plantillaDato.getNomDocumento()); // Ya no se usa directamente
            // plantillaDatoDto.setDesVersion(plantillaDato.getDesVersion()); // Ya no se usa directamente
            plantillaDatoDto.setDatosJson(plantillaDato.getDesDatos());
            plantillaDatoDto.setTipoAcuse(tipoAcuseDeterminado); // Asignar el tipo de acuse determinado

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

        // 1. Obtener la configuración específica del acuse
        AcuseConfig config = acuseConfigService.getConfigForType(plantillaDatoDto.getTipoAcuse());
        if (config == null) {
            logger.error("No se encontró configuración para el tipo de acuse: {}", plantillaDatoDto.getTipoAcuse());
            throw new JRException("No se encontró configuración para el tipo de acuse: " + plantillaDatoDto.getTipoAcuse());
        }

        String datosJSON = plantillaDatoDto.getDatosJson();
        // Usar desVersion de la configuración
        String desVersionPlantillaPath = config.getDesVersion().replace("\\", "/") + ".jasper"; 

        logger.info("Plantilla Jasper a usar: {}", desVersionPlantillaPath);
        logger.info("Datos JSON recibidos: {}", datosJSON);

        InputStream jasperStream = this.getClass().getClassLoader().getResourceAsStream(desVersionPlantillaPath);
        if (jasperStream == null) {
            logger.error("No se encontró la plantilla Jasper: {}", desVersionPlantillaPath);
            throw new JRException("No se encontró la plantilla Jasper: " + desVersionPlantillaPath);
        }

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

        ObjectMapper objectMapper = new ObjectMapper();
        JRDataSource dataSource;
        Map<String, Object> parameters = new HashMap<>();
        String cadenaOriginal = null;

        try {
            List<Map<String, Object>> dataList = objectMapper.readValue(datosJSON, new TypeReference<List<Map<String, Object>>>(){});
            dataSource = new JRBeanCollectionDataSource(dataList);
            logger.info("JSON interpretado como lista de objetos.");
        } catch (Exception e) {
            logger.info("JSON interpretado como objeto único.");
            Map<String, Object> dataMap = objectMapper.readValue(datosJSON, new TypeReference<Map<String, Object>>() {});

 

            if (dataMap.containsKey("cadenaOriginal")) {
                Object value = dataMap.get("cadenaOriginal"); // Obtiene el valor
                if (value instanceof String) {
                    cadenaOriginal = (String) value;
                    logger.info("Cadena Original obtenida del JSON  : {}", cadenaOriginal);
                } else {
                    logger.warn("El valor de 'cadenaOriginal' no es un String en el JSON.");
                }
            }



            parameters.putAll(dataMap);
            dataSource = new JREmptyDataSource(1);
        }


        SimpleDateFormat formatter = new SimpleDateFormat(FORMATO_dd_MM_yyyy_HH_mm_ss);
        
        // Obtener la fecha actual formateada
        String fechaActual = formatter.format(new Date());

        parameters.put("fecha", fechaActual);
       // parameters.put("vistaPrevia", "SI");


        if (cadenaOriginal != null) {
            InputStream qrImage = utileriasService.generaQRImageInputStream(cadenaOriginal);

 
            parameters.put("qrcode", qrImage);
            logger.info("Parámetro 'cadenaOriginal' añadido a JasperReports." + cadenaOriginal);
        }




        // 2. Añadir los parámetros de imágenes y otros textos desde la configuración
        config.getImagePaths().forEach((key, value) -> 
            parameters.put(key, value.replace("\\", "/"))
        );
        logger.info("Parámetros de configuración añadidos al reporte: {}", config.getImagePaths());

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

        logger.info("✅ Acuse generado exitosamente.");
        return baos.toByteArray();
    }

    @Override
    public DecargarAcuseDto consultaAcuseByPlantillaDato(PlantillaDatoDto plantillaDatoDto) {
        logger.info("Procesando consultaAcuseByPlantillaDato sin conexión a BD...");

        DecargarAcuseDto decargarAcuseDto = new DecargarAcuseDto();
        decargarAcuseDto.setCodigo(1); // Por defecto error

        try {
            // Obtener la configuración para el tipo de acuse recibido en el DTO
            AcuseConfig config = acuseConfigService.getConfigForType(plantillaDatoDto.getTipoAcuse());
            if (config == null) {
                 throw new RuntimeException("No se encontró configuración para el tipo de acuse: " + plantillaDatoDto.getTipoAcuse());
            }

            // Aquí el PlantillaDatoDto ya debería tener el tipoAcuse
            byte[] pdfBytes = generarAcuseconDatosJSON(plantillaDatoDto);

            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            decargarAcuseDto.setDocumento("data:application/pdf;base64," + pdfBase64);
            // Usar el nomDocumento de la configuración centralizada
            decargarAcuseDto.setNombreDocumento(config.getNomDocumento() + ".pdf");
            decargarAcuseDto.setCodigo(0);
            decargarAcuseDto.setMensaje("Acuse generado exitosamente sin conexión a BD.");

        } catch (Exception e) {
            logger.error("Error al generar el acuse: {}", e.getMessage(), e);
            decargarAcuseDto.setMensaje("Error al generar acuse: " + e.getMessage());
        }

        return decargarAcuseDto;
    }
}