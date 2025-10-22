package mx.gob.imss.documentos.service;
 

 
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Date; 
import java.io.IOException; 
import java.nio.charset.StandardCharsets;
import java.util.Base64;  

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

 
import mx.gob.imss.documentos.dto.DocumentoIndividualDto;
import mx.gob.imss.documentos.dto.DownloadFileDto;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream; 
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation; // Para configurar el usuario de Hadoop

import org.springframework.beans.factory.annotation.Value; 
import org.springframework.core.io.Resource; 

import jakarta.annotation.PostConstruct; // Para inicialización de bean
import jakarta.annotation.PreDestroy; // Para limpieza de bean

@Service("cargaDocumentoService")
public class CargaDocumentoServiceImpl implements CargaDocumentoService { 

	private static final Logger logger = LogManager.getLogger(CargaDocumentoServiceImpl.class);

    @Value("${hadoop.username}")
    private String hadoopUserName;

    @Value("${hadoop.default-fs}")
    private String defaultFS;
    
    // Inyectamos el FileSystem como un bean gestionado por Spring
    // Esto asegura que haya una sola instancia (o las que se definan) y que se cierre correctamente.
    private FileSystem hadoopFileSystem;

    // Configuración para el FileSystem que será un bean
    private Configuration hadoopConfiguration;

    @PostConstruct // Se ejecuta después de que el bean se ha inicializado y las propiedades @Value se han inyectado
    public void init() throws IOException {
        logger.info("Inicializando Hadoop FileSystem para usuario: {} y defaultFS: {}", hadoopUserName, defaultFS);
        hadoopConfiguration = new Configuration();
        hadoopConfiguration.set("fs.defaultFS", defaultFS);
        hadoopConfiguration.setInt("ipc.client.connect.max.retries", 3);
        hadoopConfiguration.setInt("ipc.client.connect.max.retries.on.timeouts", 3);
        hadoopConfiguration.setInt("ipc.client.connect.timeout", 5000); // Considera aumentar si hay muchos time-outs

        // Configurar el usuario de Hadoop de forma segura y correcta para la instancia de FileSystem
        // Esto es esencial para evitar race conditions y asegurar que las operaciones se hagan con el usuario correcto.
        // Option 1: Using UserGroupInformation (más robusta para casos complejos de seguridad)
        UserGroupInformation.setConfiguration(hadoopConfiguration);
        UserGroupInformation ugi = UserGroupInformation.createRemoteUser(hadoopUserName);
        // La siguiente línea es crucial para que el FileSystem se obtenga con el usuario correcto
       // hadoopFileSystem = ugi.doAs((java.security.PrivilegedExceptionAction<FileSystem>) () -> FileSystem.get(hadoopConfiguration));
        
        // Option 2 (más simple si no hay kerberos o seguridad compleja): 
         hadoopConfiguration.set("hadoop.job.ugi", hadoopUserName + "," + hadoopUserName);
         hadoopFileSystem = FileSystem.get(hadoopConfiguration);

        logger.info("FileSystem de Hadoop inicializado correctamente.");
    }

    @PreDestroy // Se ejecuta cuando el bean está a punto de ser destruido
    public void destroy() {
        if (hadoopFileSystem != null) {
            try {
                logger.info("Cerrando Hadoop FileSystem.");
                hadoopFileSystem.close();
            } catch (IOException e) {
                logger.error("Error al cerrar Hadoop FileSystem.", e);
            }
        }
    }

	@Override 
	public DocumentoIndividualDto cargaDocumentoHadoop(DocumentoIndividualDto documentoIndividualVO ) throws IOException, IllegalArgumentException, ParseException { 
		
	    logger.info("-------------inicio cargaDocumentoHadoop cargaDocumentoBase64 " );
	    
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
			
		    String fileName = documentoIndividualVO.getNomArchivo();
            if (fileName == null || fileName.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del archivo (nomArchivo) no puede ser nulo o vacío.");
            }

		   	String pattern = "yyyyMMdd";
		   	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern,new Locale("es", "MX"));
		   	String dateCarga = simpleDateFormat.format(fechaCarga);
		   	
		    String  path=  dateCarga +  "/"  + documentoIndividualVO.getDesRfc() + "/" ;
		   	
		    if(documentoIndividualVO.getDesPath()!=null) {
		      path +=  documentoIndividualVO.getDesPath() ;
		    }

			logger.info("se asigna el path correcto "+ path);
		    documentoIndividualVO.setDesPath(path);

		  	String pathUbicacion= path;
		  	logger.info("pathUbicacion "+ pathUbicacion);

			String pathHdfs=  pathUbicacion;
			
            byte[] archivoBytes;
            try {
			    archivoBytes = Base64.getDecoder().decode(documentoIndividualVO.getDocumentoBase64());
                documentoIndividualVO.setDocumentoBase64(null); // Liberar memoria
            } catch (IllegalArgumentException e) {
                logger.error("Error al decodificar el Base64 del documento.", e);
                throw new IllegalArgumentException("El documento Base64 no es válido.", e);
            }
 
			logger.info("archivoDatos.getBytes(): " + archivoBytes.length + " bytes.");
			
			//carga el documento en hadoop
			String fullHdfsPath= saveDocumentoHdfs(archivoBytes, pathHdfs, fileName); 

            String fullHdfsPathBase64 = Base64.getEncoder().encodeToString(fullHdfsPath.getBytes(StandardCharsets.UTF_8));
            logger.info("documentoIndividualVO.getDesRfc(): " + documentoIndividualVO.getDesRfc() +" fullHdfsPathBase64: " + fullHdfsPathBase64 );

            documentoIndividualVO.setDesPathHdfs(fullHdfsPathBase64);
			
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
    public DocumentoIndividualDto cargaDocumentoHadoop(
            MultipartFile archivo,
            String desRfc,
            String nomArchivo,
            String desPath,
            String fechaActualStr) throws IOException, IllegalArgumentException, ParseException {

        logger.info("-------------inicio cargaDocumentoHadoop (MultipartFile) -------------");

        DocumentoIndividualDto resultadoDto = new DocumentoIndividualDto();
        resultadoDto.setDesRfc(desRfc);
        resultadoDto.setNomArchivo(nomArchivo);
        resultadoDto.setDesPath(desPath);
        resultadoDto.setFechaActual(fechaActualStr); // Se establecerá la fecha parseada más adelante

        // 1. Validaciones iniciales
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede ser nulo o vacío.");
        }
        if (desRfc == null || desRfc.trim().isEmpty()) {
            throw new IllegalArgumentException("El RFC (desRfc) no puede ser nulo o vacío.");
        }
        if (nomArchivo == null || nomArchivo.trim().isEmpty()) {
            // Si nomArchivo viene vacío, intentar usar el original del MultipartFile
            if (archivo.getOriginalFilename() != null && !archivo.getOriginalFilename().trim().isEmpty()) {
                nomArchivo = archivo.getOriginalFilename();
                resultadoDto.setNomArchivo(nomArchivo);
                logger.warn("El nomArchivo proporcionado estaba vacío, usando el nombre original del archivo: {}", nomArchivo);
            } else {
                throw new IllegalArgumentException("El nombre del archivo (nomArchivo) no puede ser nulo o vacío.");
            }
        }


        // 2. Parsear fecha
        Date fechaCarga;
        if (fechaActualStr != null && !fechaActualStr.isEmpty()) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "MX"));
                fechaCarga = parser.parse(fechaActualStr);
            } catch (ParseException e) {
                logger.error("Error al parsear la fecha del documento: " + fechaActualStr, e);
                throw new ParseException("Formato de fecha inválido: " + fechaActualStr + ". Se esperaba dd/MM/yyyy", e.getErrorOffset());
            }
        } else {
            fechaCarga = new Date();
            logger.warn("El campo fechaActual estaba nulo o vacío. Usando fecha actual: " + fechaCarga);
        }
        // Actualizar el DTO con la fecha formateada si no se proporcionó o si se parseó correctamente
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", new Locale("es", "MX"));
        resultadoDto.setFechaActual(formatter.format(fechaCarga));


        // 3. Obtener bytes del archivo
        byte[] archivoBytes = archivo.getBytes();
        logger.info("Tamaño del archivo recibido: {} bytes.", archivoBytes.length);

        // 4. Construir el path en HDFS
        String pattern = "yyyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, new Locale("es", "MX"));
        String dateCargaFormatted = simpleDateFormat.format(fechaCarga);

        String baseHdfsPath = dateCargaFormatted + "/" + desRfc + "/";
        String finalHdfsPath;

        if (desPath != null && !desPath.trim().isEmpty()) {
            // Asegurarse de que desPath no empiece ni termine con "/" para evitar duplicados
            String cleanedDesPath = desPath.replaceAll("^/|/$", "");
            finalHdfsPath = baseHdfsPath + cleanedDesPath + "/";
        } else {
            finalHdfsPath = baseHdfsPath;
        }

        logger.info("Path base en HDFS construido: {}", finalHdfsPath);
        resultadoDto.setDesPath(finalHdfsPath); // Establecer el path que se usará


        // 5. Guardar el documento en HDFS
        String fullHdfsPath = saveDocumentoHdfs(archivoBytes, finalHdfsPath, nomArchivo);

        // 6. Codificar el path de HDFS a Base64 y asignarlo al DTO
        String fullHdfsPathBase64 = Base64.getEncoder().encodeToString(fullHdfsPath.getBytes(StandardCharsets.UTF_8));
        resultadoDto.setDesPathHdfs(fullHdfsPathBase64);
        logger.info("Path en HDFS (Base64) para {}/{}: {}", desRfc, nomArchivo, fullHdfsPathBase64);

        // 7. Establecer códigos de éxito
        resultadoDto.setCodigo(0);
        resultadoDto.setMensaje("Éxito. Documento cargado correctamente en HDFS.");

        logger.info("-------------Fin cargaDocumentoHadoop (MultipartFile) -------------");
        return resultadoDto;
    }





    @Override
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
  
        loadFileToHDFS(file, fullHdfsPath); 

        if (!checkFileExistenceInHDFS(fullHdfsPath)) {
            logger.error("El archivo no se encontró en HDFS después de la carga: " + fullHdfsPath);
            throw new IOException("Fallo la verificación: El archivo no se encontró en HDFS después de cargarlo.");
        } else {
            logger.info("Validación exitosa: El archivo se encuentra en HDFS: " + fullHdfsPath);
        }

         return fullHdfsPath;
    }
    
    // El FileSystem ahora se inyecta y se gestiona como un bean, no se crea en cada llamada.
    // Esto reduce la sobrecarga de conexiones y NameNode.
    private void loadFileToHDFS(byte[] fileBytes, String pathHdfs) throws IOException { 
        // No se necesita configurar System.setProperty("HADOOP_USER_NAME", hadoopUserName); aquí
        // porque el FileSystem ya fue inicializado con el usuario correcto.
        
        Path hdfsPath = new Path(pathHdfs);
        logger.debug("Intentando crear archivo en HDFS: {}", pathHdfs); // Usar debug para logs frecuentes

        // Usamos try-with-resources para asegurar que fsDataOutputStream.close() se llame automáticamente.
        try (FSDataOutputStream fsDataOutputStream = hadoopFileSystem.create(hdfsPath, true)) { // `true` para sobrescribir
            fsDataOutputStream.write(fileBytes);
            logger.info("Archivo cargado en HDFS en la ruta: {}", pathHdfs);
        } catch (IOException e) { 
            logger.error("Ocurrió un error de E/S durante el proceso de carga a HDFS para {}: {}", pathHdfs, e.getMessage(), e);
            throw e; 
        } catch (Exception e) { 
            logger.error("Ocurrió un error inesperado durante el proceso de carga a HDFS para {}: {}", pathHdfs, e.getMessage(), e);
            throw new IOException("Error inesperado al cargar el archivo a HDFS: " + pathHdfs, e); 
        }
    }

    /**
     * Verifica si un archivo existe en la ruta especificada de HDFS.
     * Reutiliza la instancia de hadoopFileSystem.
     * 
     * @param hdfsFilePath La ruta completa del archivo en HDFS.
     * @return true si el archivo existe, false en caso contrario.
     * @throws IOException Si ocurre un error al interactuar con HDFS.
     */
    private boolean checkFileExistenceInHDFS(String hdfsFilePath) throws IOException {
        try {
            logger.debug("Iniciando verificación de existencia de archivo en HDFS: {}", hdfsFilePath);
            // No se necesita System.setProperty aquí.
            Path path = new Path(hdfsFilePath);
            boolean exists = hadoopFileSystem.exists(path);
            logger.debug("El archivo {} {}", hdfsFilePath, (exists ? " existe." : " NO existe."));
            return exists;
        } catch (IOException e) {
            logger.error("Error al verificar la existencia del archivo en HDFS para la ruta {}: {}", hdfsFilePath, e.getMessage(), e);
            throw e; 
        }
    }


   
    @Override
    public Resource readFileHdfs(String pathHdfs ) throws IOException {  
        InputStreamResource resource = null;   
		try {
			   logger.info("readFileHdfs HADOOP_USER_NAME: " + hadoopUserName);
               // System.setProperty("HADOOP_USER_NAME", hadoopUserName); -- No es necesario
			   // FileSystem fileSystem = fileSystemHDFS(); -- Usar la instancia inyectada
			   logger.info("Inicia lectura de archivo HDFS " + new Date());
		       Path hdfsReadPath = new Path(pathHdfs);
		       logger.info("hdfsReadPath " + hdfsReadPath);
		       FSDataInputStream inputStream = hadoopFileSystem.open(hdfsReadPath); 
		       resource = new InputStreamResource(inputStream);
		}catch (IOException e) { 
			logger.error("Error al leer el archivo de HDFS: " + e.getMessage(), e);
			throw e; 
		}catch (Exception e) { 
            logger.error("Error inesperado al leer el archivo de HDFS: " + e.getMessage(), e);
            throw new IOException("Error inesperado al leer el archivo de HDFS.", e); 
        }
		return resource;
    }



    @Override
    public DownloadFileDto downloadDocumentoHdfs(String fullHdfsPathBase64) throws IOException, IllegalArgumentException {
        logger.info("Iniciando downloadDocumentoHdfs en Service.");
        String fullHdfsPath = null;
        try {
            // 1. Decodificar la ruta Base64
            byte[] decodedBytes = Base64.getDecoder().decode(fullHdfsPathBase64);
            fullHdfsPath = new String(decodedBytes, StandardCharsets.UTF_8);
            logger.debug("Ruta HDFS decodificada para descarga: {}", fullHdfsPath);

            // 2. Obtener el Resource del archivo
            Resource resource = readFileHdfs(fullHdfsPath);

            if (resource == null || !resource.exists() || !resource.isReadable()) {
                logger.warn("El archivo no se encontró o no se pudo leer en HDFS: {}", fullHdfsPath);
                throw new IOException("El documento solicitado no fue encontrado o no es accesible.");
            }

            // 3. Obtener el nombre del archivo de la ruta HDFS
            String filename = new Path(fullHdfsPath).getName();
            logger.info("Archivo recuperado para descarga: {}", filename);

            // 4. Construir y devolver el DTO de descarga
            return DownloadFileDto.builder()
                        .resource(resource)
                        .filename(filename)
                       
                        //.mediaType(inferMediaType(filename))
                        .build();

        } catch (IllegalArgumentException e) {
            logger.error("Error al decodificar la ruta Base64 para descarga: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Formato de ruta de documento inválido.", e);
        } catch (IOException e) {
            logger.error("Error de I/O al descargar el documento de HDFS ({}): {}", fullHdfsPath, e.getMessage(), e);
            throw e; // Re-lanzar o envolver según sea necesario para el controlador
        } catch (Exception e) {
            logger.error("Error inesperado en downloadDocumentoHdfs para ({}): {}", fullHdfsPath, e.getMessage(), e);
            throw new IOException("Error inesperado al preparar el documento para descarga.", e);
        } finally {
            logger.info("Finalizando downloadDocumentoHdfs en Service.");
        }
    }




        @Override
    public void deleteDocumentoHdfs(String fullHdfsPathBase64) throws IOException, IllegalArgumentException {
        logger.info("Iniciando deleteDocumentoHdfs en Service.");
        String fullHdfsPath = null;
        try {
            // 1. Decodificar la ruta Base64
            byte[] decodedBytes = Base64.getDecoder().decode(fullHdfsPathBase64);
            fullHdfsPath = new String(decodedBytes, StandardCharsets.UTF_8);
            logger.debug("Ruta HDFS decodificada para eliminación: {}", fullHdfsPath);

            Path hdfsPath = new Path(fullHdfsPath);

            // 2. Verificar si el archivo existe antes de intentar eliminarlo (opcional, pero buena práctica)
            if (!hadoopFileSystem.exists(hdfsPath)) {
                logger.warn("Intento de eliminar archivo no existente en HDFS: {}", fullHdfsPath);
                throw new IOException("El documento a eliminar no fue encontrado en HDFS: " + fullHdfsPath);
            }

            // 3. Eliminar el archivo de HDFS
            // 'false' significa que no será recursivo (solo elimina el archivo, no directorios)
            boolean deleted = hadoopFileSystem.delete(hdfsPath, false);

            if (deleted) {
                logger.info("Documento eliminado exitosamente de HDFS: {}", fullHdfsPath);
            } else {
                logger.error("Fallo la eliminación del documento en HDFS (el archivo podría no haber existido o hubo un permiso): {}", fullHdfsPath);
                throw new IOException("No se pudo eliminar el documento de HDFS: " + fullHdfsPath);
            }

        } catch (IllegalArgumentException e) {
            logger.error("Error al decodificar la ruta Base64 para eliminación: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Formato de ruta de documento inválido para eliminación.", e);
        } catch (IOException e) {
            logger.error("Error de I/O al eliminar el documento de HDFS ({}): {}", fullHdfsPath, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado en deleteDocumentoHdfs para ({}): {}", fullHdfsPath, e.getMessage(), e);
            throw new IOException("Error inesperado al intentar eliminar el documento de HDFS.", e);
        } finally {
            logger.info("Finalizando deleteDocumentoHdfs en Service.");
        }
    }



}