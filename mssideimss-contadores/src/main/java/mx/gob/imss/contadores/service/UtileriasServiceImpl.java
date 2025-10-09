package mx.gob.imss.contadores.service;

 
import java.util.Base64;
import java.io.IOException;
import java.io.UnsupportedEncodingException; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger; 
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
 


@Service("utileriasService")
public class UtileriasServiceImpl implements UtileriasService { 

	private static final Logger logger = LogManager.getLogger(UtileriasServiceImpl.class);
	
  
    @Override
    public String encriptar(String s) throws UnsupportedEncodingException { 
      return Base64.getEncoder().encodeToString(s.getBytes("utf-8"));
    } 
    @Override
    public String desencriptar(String s) throws UnsupportedEncodingException {
        byte[] decode = Base64.getDecoder().decode(s.getBytes());
    	
        return new String(decode, "utf-8");
    }
 
     /**
     * Convierte un MultipartFile a una cadena Base64 de forma reactiva,
     * ejecutando la operación bloqueante en un Scheduler separado.
     *
     * @param file El MultipartFile a convertir.
     * @param fileName La descripción del archivo para el logging (e.g., "archivoUno").
     * @return Un Mono que emite la cadena Base64, o un error si la conversión falla.
     */
	@Override
    public Mono<String> convertMultipartFileToBase64(MultipartFile file, String fileName) {
        return Mono.fromCallable(() -> {
            logger.debug("Convirtiendo {} a Base64...", fileName);
            if (file.isEmpty()) {
                throw new IOException("El archivo " + fileName + " está vacío.");
            }
            return Base64.getEncoder().encodeToString(file.getBytes());
        })
        .subscribeOn(Schedulers.boundedElastic()) // Ejecuta la operación bloqueante en un hilo separado
        .doOnError(IOException.class, e -> logger.error("Error de I/O al convertir {} a Base64: {}", fileName, e.getMessage(), e))
        .onErrorResume(e -> Mono.error(new RuntimeException("Fallo al convertir " + fileName + " a Base64: " + e.getMessage(), e)));
    }
    
}
