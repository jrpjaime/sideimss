package mx.gob.imss.acuses.service;

 
import java.awt.image.BufferedImage;
import java.io.UnsupportedEncodingException; 
 
public interface UtileriasService {	 
    public String encriptar(String s) throws UnsupportedEncodingException;
    public String desencriptar(String s) throws UnsupportedEncodingException; 
	BufferedImage generaQRImage(String content)  throws Exception;
}

