package mx.gob.imss.contadores.service;

  
import java.io.UnsupportedEncodingException;

import org.springframework.web.multipart.MultipartFile;

import reactor.core.publisher.Mono; 
 
public interface UtileriasService {	 
    public String encriptar(String s) throws UnsupportedEncodingException;
    public String desencriptar(String s) throws UnsupportedEncodingException; 
    public Mono<String> convertMultipartFileToBase64(MultipartFile file, String fileName);
}

