package mx.gob.imss.acuses.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
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
import java.util.Base64; // Importar Base64
import java.util.Collections;
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
	
 
	@Autowired
	private UtileriasService utileriasService;


	@Autowired
	private PlantillaDatosRepository plantillaDatosRepository;
	

	
	@Override
	@Transactional
	public DecargarAcuseDto consultaAcuseByUrlDocumento(String urlDocumento){
		
		DecargarAcuseDto decargarAcuseDto=new DecargarAcuseDto();
		decargarAcuseDto.setCodigo(1); // Por defecto, asume un error
		decargarAcuseDto.setMensaje("Error en acuse.");
		
		try {
	  		String filename=utileriasService.desencriptar(urlDocumento); 
			logger.info("1filename: " + filename );
			StringTokenizer tokens=new StringTokenizer(filename, "|");
	        int nDatos=tokens.countTokens();
	      
	        String[] datos=new String[nDatos];
	        Integer i=0;
	        while(tokens.hasMoreTokens()){
	            String str=tokens.nextToken();
	            datos[i]=str;
	            logger.info("str: " + str);
	            i++;
	            
	        }
	        
	    
	        String rfc=datos[0];
	        logger.info("rfc: " + rfc);
	        Long cveIdPlantillaDatos=Long.parseLong( datos[1]);
	        logger.info("cveIdPlantillaDatos: " + cveIdPlantillaDatos);
	        
	        PlantillaDato plantillaDato=plantillaDatosRepository.findById(cveIdPlantillaDatos).get();
	        logger.info("plantillaDato: " + plantillaDato.getDesDatos());
	        
	        String nombreDocumento=plantillaDato.getNomDocumento();
	        logger.info("nombreDocumento: " + nombreDocumento);
			// Generar el acuse y obtener los bytes del PDF
			byte[] pdfBytes = generarAcuseconDatosJSON(plantillaDato);

			// Codificar los bytes del PDF a Base64
			String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

			// Asignar los valores al DTO
			decargarAcuseDto.setDocumento("data:application/pdf;base64," + pdfBase64); // Formato de URI de datos
			decargarAcuseDto.setNombreDocumento(nombreDocumento + ".pdf"); // Nombre dinámico
			decargarAcuseDto.setCodigo(0); // Éxito
			decargarAcuseDto.setMensaje("Acuse generado y codificado exitosamente.");
	        
	        
	        
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
        
        
		return decargarAcuseDto;
	}
	 
	
	
	/**
	 * Genera un acuse en formato PDF utilizando una plantilla JasperReports y datos JSON.
	 *
	 * @param plantillaDato El objeto PlantillaDato que contiene los datos JSON y la versión de la plantilla.
	 * @return Un array de bytes que representa el documento PDF generado.
	 * @throws JRException Si ocurre un error durante la generación del reporte Jasper.
	 * @throws java.io.IOException Si ocurre un error al procesar el JSON.
	 */
	 private byte[] generarAcuseconDatosJSON(PlantillaDato plantillaDato) throws JRException, java.io.IOException {
        logger.info("Iniciando generación de acuse con datos JSON...");

        String datosJSON = plantillaDato.getDesDatos();
        String desVersionPlantilla = plantillaDato.getDesVersion() + ".jasper"; // Ruta completa del .jasper

        logger.info("Plantilla Jasper a usar: {}", desVersionPlantilla);
        logger.info("Datos JSON recibidos: {}", datosJSON);

        // 1️⃣ Cargar la plantilla Jasper del classpath
        InputStream jasperStream = this.getClass().getClassLoader().getResourceAsStream(desVersionPlantilla);
        if (jasperStream == null) {
            logger.error("No se encontró la plantilla Jasper: {}", desVersionPlantilla);
            throw new JRException("No se encontró la plantilla Jasper: " + desVersionPlantilla);
        }

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

        // 2️⃣ Parsear el JSON recibido
        ObjectMapper objectMapper = new ObjectMapper();
        JRDataSource dataSource;
        Map<String, Object> parameters = new HashMap<>();

        try {
            // Si el JSON es una lista (para reportes tipo tabla)
            List<Map<String, Object>> dataList = objectMapper.readValue(datosJSON, new TypeReference<List<Map<String, Object>>>(){});
            dataSource = new JRBeanCollectionDataSource(dataList);
            logger.info("JSON interpretado como lista de objetos.");
        } catch (Exception e) {
            // Si el JSON es un solo objeto
            logger.info("JSON interpretado como objeto único.");
            Map<String, Object> dataMap = objectMapper.readValue(datosJSON, new TypeReference<Map<String, Object>>() {});
            parameters.putAll(dataMap);
            dataSource = new JREmptyDataSource(1);
        }

        // 3️⃣ Llenar el reporte Jasper con los parámetros y/o datasource
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

        // 4️⃣ Exportar el reporte a PDF
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
            // Crear el objeto PlantillaDato desde el DTO
            PlantillaDato plantillaDato = new PlantillaDato();
            plantillaDato.setNomDocumento(plantillaDatoDto.getNomDocumento());
            plantillaDato.setDesVersion(plantillaDatoDto.getDesVersion());
            plantillaDato.setDesDatos(plantillaDatoDto.getDatosJson()); // ✅ clave para no requerir BD

            logger.info("Generando PDF con la plantilla: {}", plantillaDato.getDesVersion());

            // Generar el PDF
            byte[] pdfBytes = generarAcuseconDatosJSON(plantillaDato);

            // Codificar a Base64 para enviar al frontend
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);

            decargarAcuseDto.setDocumento("data:application/pdf;base64," + pdfBase64);
            decargarAcuseDto.setNombreDocumento(plantillaDato.getNomDocumento());
            decargarAcuseDto.setCodigo(0);
            decargarAcuseDto.setMensaje("Acuse generado exitosamente sin conexión a BD.");

        } catch (Exception e) {
            logger.error("❌ Error al generar el acuse sin BD: {}", e.getMessage(), e);
            decargarAcuseDto.setMensaje("Error al generar acuse: " + e.getMessage());
        }

        return decargarAcuseDto;
    }

}
