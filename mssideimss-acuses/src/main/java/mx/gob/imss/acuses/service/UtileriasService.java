package mx.gob.imss.acuses.service;

 
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import com.google.zxing.WriterException; 
 
public interface UtileriasService {	 
    public String encriptar(String s) throws UnsupportedEncodingException;
    public String desencriptar(String s) throws UnsupportedEncodingException; 
	BufferedImage generaQRImage(String content)  throws Exception;
    public InputStream generaQRImageInputStream(String content) throws WriterException, IOException;
}

