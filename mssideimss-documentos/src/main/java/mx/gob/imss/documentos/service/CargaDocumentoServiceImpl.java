package mx.gob.imss.documentos.service;
 

 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date; 

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation; 
import jakarta.transaction.Transactional;
import mx.gob.imss.documentos.dto.DocumentoIndividualVO;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream; 
import org.apache.hadoop.fs.Path;

import java.io.IOException; // Importar IOException
import java.io.UnsupportedEncodingException; // Para Base64 si surge
import java.util.Base64; // Para Base64
import org.springframework.beans.factory.annotation.Value; // Importar la anotación @Value
import org.springframework.core.io.Resource;

@Service("cargaDocumentoService")
public class CargaDocumentoServiceImpl implements CargaDocumentoService { 

	private static final Logger logger = LogManager.getLogger(CargaDocumentoServiceImpl.class);

    @Value("${hadoop.username}")
    private String hadoopUserName;

    @Value("${hadoop.default-fs}")
    private String defaultFS;

	@Override 
    @Transactional
	public DocumentoIndividualVO cargaDocumentoHadoop(DocumentoIndividualVO documentoIndividualVO ) throws IOException, IllegalArgumentException, ParseException { 
		
	    logger.info("-------------inicio cargaDocumentoHadoop cargaDocumentoBase64 " );
	    
		// 1. Validar el documentoIndividualVO antes de proceder
		if (documentoIndividualVO == null) {
			throw new IllegalArgumentException("El objeto DocumentoIndividualVO no puede ser nulo.");
		}

        Date fechaCarga;
        if (documentoIndividualVO.getFechaActual() != null && !documentoIndividualVO.getFechaActual().isEmpty()) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "MX"));
                fechaCarga = parser.parse(documentoIndividualVO.getFechaActual());
            } catch (ParseException e) {
                logger.error("Error al parsear la fecha del documentoIndividualVO: " + documentoIndividualVO.getFechaActual(), e);
                throw new ParseException("Formato de fecha inválido en el documento: " + documentoIndividualVO.getFechaActual(), e.getErrorOffset());
            }
        } else {
            fechaCarga =  new Date(); 
            logger.warn("El campo fechaActual del DTO estaba nulo o vacío. Usando fecha: " + fechaCarga);
        }

		if(documentoIndividualVO.getDocumentoBase64()!=null) {
			
			Long cveIdTramite=16L; 
		    
		    String fileName=documentoIndividualVO.getNomArchivo();
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del archivo (nomArchivo) no puede ser nulo o vacío.");
            }

		   	String pattern = "yyyyMMdd";
		   	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,new Locale("es", "MX"));
		   	String dateCarga = simpleDateFormat.format(fechaCarga);
		   	
		    String path= "";
		   	
		    if(documentoIndividualVO.getDesPath()!=null) {
		      path=  "/" + dateCarga +   documentoIndividualVO.getDesPath();
		    }else {
		      path=  "/" + dateCarga +  "/";
		    }
		   	
			logger.info("se asigna el path correcto "+ path);
		    documentoIndividualVO.setDesPath(path);

		  	String pathUbicacion=cveIdTramite +  path + documentoIndividualVO.getDesRfc() + "/";
		  	logger.info("pathUbicacion "+ pathUbicacion);

			String pathHdfs=  pathUbicacion;
			
            byte[] archivoBytes;
            try {
			    archivoBytes = Base64.getDecoder().decode(documentoIndividualVO.getDocumentoBase64());
            } catch (IllegalArgumentException e) {
                logger.error("Error al decodificar el Base64 del documento.", e);
                throw new IllegalArgumentException("El documento Base64 no es válido.", e);
            }

			logger.info("utileriasService.saveDocumentoHdfs");
			logger.info("archivoDatos.getBytes(): " + archivoBytes);
			
			//carga el documento en hadoop
			saveDocumentoHdfs(archivoBytes, pathHdfs, fileName); 
			
		} else {
            logger.warn("El campo documentoBase64 del DTO es nulo. No se intentará cargar en HDFS.");
            throw new IllegalArgumentException("El archivo para guardar en HDFS no puede ser nulo o vacío.");
        }
		
      
        documentoIndividualVO.setCodigo(0);
        documentoIndividualVO.setMensaje("Éxito.");
		
		logger.info("termina cargaDocumentoBase64 " );
		logger.info("::::::::::::::::::::::" );
      
	    return documentoIndividualVO;
	}

    @Override
    @Transactional
    public String saveDocumentoHdfs(byte[] file, String pathHdfs, String namefile) throws IOException, IllegalArgumentException {
		if (file == null || file.length == 0) {
			logger.error("Error: el archivo a guardar en HDFS es nulo o está vacío. Se iniciará el rollback.");
			throw new IllegalArgumentException("El archivo para guardar en HDFS no puede ser nulo o vacío.");
		}
    	 
    	logger.info("entro en saveDocumentoHdfs");
    	
		pathHdfs="contenedorSID/"  + pathHdfs ;
    	
        String fullHdfsPath = pathHdfs.endsWith("/") ? pathHdfs + namefile : pathHdfs + "/" + namefile;
        logger.info("Cargando el documento en hadoop pathHdfs: " + pathHdfs);
        logger.info("Cargando el documento en hadoop fullHdfsPath: " + fullHdfsPath);
  
        loadFileToHDFS(file, fullHdfsPath); // Este método ahora lanza IOException directamente
         return "";
    }
    


    private void loadFileToHDFS(byte[] fileBytes, String pathHdfs) throws IOException { 
        FileSystem fileSystem = null; 
        FSDataOutputStream fsDataOutputStream = null;
        try {
            fileSystem = fileSystemHDFS(); 
            Path hdfsPath = new Path(pathHdfs);
            logger.info("2 loadFileToHDFSHADOOP_USER_NAME: " + hadoopUserName);
            System.setProperty("HADOOP_USER_NAME", hadoopUserName);

            fsDataOutputStream = fileSystem.create(hdfsPath);
            fsDataOutputStream.write(fileBytes);
            logger.info("Termina de cargar el archivo en HDFS en la ruta: " + pathHdfs);
        } catch (IOException e) { // Capturamos IOException específica
            logger.error("Ocurrió un error de E/S durante el proceso de carga a HDFS: " + e.getMessage(), e);
            throw e; // Relanzamos la IOException
        } catch (Exception e) { // Cualquier otra excepción inesperada
            logger.error("Ocurrió un error inesperado durante el proceso de carga a HDFS: " + e.getMessage(), e);
            throw new IOException("Error inesperado al cargar el archivo a HDFS.", e); // Envolvemos en IOException
        } finally {
            if (fsDataOutputStream != null) {
                try {
                    fsDataOutputStream.close();
                } catch (IOException e) { // Capturamos IOException específica
                    logger.error("Error al cerrar FSDataOutputStream.", e);
                }
            }

        }
    }
 
   private FileSystem fileSystemHDFS() throws IOException {  
	 
	    	Configuration config = new Configuration();
	    	FileSystem dfs = null; 
	    	
	    	
	    	logger.info("fs.defaultFS: " + defaultFS);
	     	config.set("fs.defaultFS", defaultFS);
	     	config.setInt("ipc.client.connect.max.retries", 3);
	     	config.setInt("ipc.client.connect.max.retries.on.timeouts", 3);
	     	config.setInt("ipc.client.connect.timeout", 5000);

            try {
		 	    dfs = FileSystem.get(config);
            } catch (IOException e) {
                logger.error("Error al obtener el FileSystem de Hadoop: " + e.getMessage(), e);
                throw e;  
            }
		    return dfs;
   }
   
 
  
    @Override
    public Resource readFileHdfs(String pathHdfs ) throws IOException {  
        InputStreamResource resource = null;   
		try {
			   logger.info("readFileHdfs HADOOP_USER_NAME: " + hadoopUserName);
			   System.setProperty("HADOOP_USER_NAME", hadoopUserName);
			   FileSystem fileSystem = fileSystemHDFS(); 
			   logger.info("Inicia lectura de archivo HDFS " + new Date());
		       Path hdfsReadPath = new Path(pathHdfs);
		       logger.info("hdfsReadPath " + hdfsReadPath);
		       FSDataInputStream inputStream = fileSystem.open(hdfsReadPath); 
		       resource = new InputStreamResource(inputStream);
		}catch (IOException e) { // Captura IOException específica
			logger.error("Error al leer el archivo de HDFS: " + e.getMessage(), e);
			throw e; // Relanza la IOException
		}catch (Exception e) { // Para cualquier otra excepción inesperada
            logger.error("Error inesperado al leer el archivo de HDFS: " + e.getMessage(), e);
            throw new IOException("Error inesperado al leer el archivo de HDFS.", e); // Envuelve en IOException
        }
		return resource;
    }
}