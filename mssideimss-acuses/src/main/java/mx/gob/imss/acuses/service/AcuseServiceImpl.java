package mx.gob.imss.acuses.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import mx.gob.imss.acuses.dto.DecargarAcuseDto;
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
	private PlantillaDatosRepository plantillaDatosRepository;
	
	@Autowired
	private UtileriasService utileriasService;
	
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
		logger.info("private byte[] generarAcuse");
		String datosJSON = plantillaDato.getDesDatos();
		String desVersionPlantilla = plantillaDato.getDesVersion()+".jasper"; // Esto debería ser el path o nombre del archivo .jasper

		 
		logger.info("desVersionPlantilla: "+ desVersionPlantilla);
		logger.info("Datos JSON para la plantilla: " + datosJSON);

		// 1. Cargar la plantilla Jasper
		// Asegúrate de que la plantilla .jasper esté accesible en el classpath
		// Por ejemplo, si está en src/main/resources/reportes/mi_plantilla.jasper
		InputStream jasperStream = this.getClass().getClassLoader().getResourceAsStream(desVersionPlantilla);
		if (jasperStream == null) {
			logger.error("No se encontró la plantilla Jasper: reportes/" + desVersionPlantilla);
			throw new JRException("No se encontró la plantilla Jasper: " + desVersionPlantilla);
		}
		
		logger.error("Se genera documento con la plantilla: " + desVersionPlantilla);
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);


		// 2. Procesar los datos JSON
		ObjectMapper objectMapper = new ObjectMapper();
		JRDataSource dataSource;
		logger.info("Parámetros para el reporte  ");
		Map<String, Object> parameters = Collections.emptyMap(); // Parámetros para el reporte  
		logger.info("paso Map<String, Object> parameters = Coll  ");
		try {
			// Intenta parsear el JSON como una lista de objetos (para tablas)
			logger.info("Intenta parsear el JSON como una lista de objetos (para tablas)");
			List<Map<String, Object>> dataList = objectMapper.readValue(datosJSON, new TypeReference<List<Map<String, Object>>>(){});
			dataSource = new JRBeanCollectionDataSource(dataList);
			logger.info("Datos JSON parseados como lista de objetos.");
		} catch (Exception e) {
			// Si no es una lista, intenta parsearlo como un solo objeto (para datos generales)
			try {
				logger.info("intenta parsearlo como un solo objeto (para datos generales");
				Map<String, Object> dataMap = objectMapper.readValue(datosJSON, new TypeReference<Map<String, Object>>(){});
				//dataSource = new JRBeanCollectionDataSource(Collections.singletonList(dataMap)); // Envuelve el mapa en una lista para el datasource
				parameters = dataMap;
				 
		        // 1. Obtener la cadena de datos para el QR del parámetro "image" que fue cargado del JSON.
		        String datosQR = (String) parameters.get("image");
		        logger.info("Datos para QR: " + datosQR);

		        if (datosQR != null && !datosQR.isEmpty()) {
		            logger.info("Datos para generar QR obtenidos del parámetro 'image': " + datosQR);

		            // 2. Generar la imagen QR
		            BufferedImage qrImage = utileriasService.generaQRImage(datosQR);
		            
		            // 3. Reemplazar la cadena original en 'parameters' con el BufferedImage generado
		            parameters.put("image", qrImage);
		            logger.info("Imagen QR generada y reemplazada en los parámetros del reporte bajo la clave 'image'.");
		        } else {
		            logger.warn("El parámetro 'image' en el JSON estaba vacío o nulo. No se generará imagen QR.");
		            // Si el QR es opcional y no se desea generar si la cadena está vacía.
		            
		        }
		        
		        
				logger.info("Datos JSON parseados como un solo objeto. Se usará como datasource y parámetros.");
			} catch (Exception ex) {
				logger.error("Error al parsear el JSON: " + datosJSON, ex);
				throw new java.io.IOException("Error al parsear el JSON: " + datosJSON, ex);
			}
		}
		logger.info("Llenar el reporte");
		// 3. Llenar el reporte
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource(1));
		logger.info("Exportar a PDF");
		// 4. Exportar a PDF
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JRPdfExporter exporter = new JRPdfExporter();

		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

		SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
		reportConfig.setSizePageToContent(true);
		reportConfig.setForceLineBreakPolicy(false);
	
		SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
		exportConfig.setMetadataAuthor("IMSS"); // Puedes configurar metadata del PDF
		

		exporter.setConfiguration(reportConfig);
		exporter.setConfiguration(exportConfig);

		exporter.exportReport();

		logger.info("Acuse generado exitosamente para plantilla: " + desVersionPlantilla);
		return baos.toByteArray();
	}
	


}
