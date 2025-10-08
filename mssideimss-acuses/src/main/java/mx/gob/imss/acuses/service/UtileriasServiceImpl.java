package mx.gob.imss.acuses.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64; 
import java.io.UnsupportedEncodingException; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger; 
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


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
 

	@Override
	public BufferedImage generaQRImage(String content)  throws Exception{
		BufferedImage image = null;
		 

			int size = 220;
			QRCodeWriter qrcode = new QRCodeWriter();
			BitMatrix matrix = qrcode.encode(content, BarcodeFormat.QR_CODE, size, size);
			int matrixWidth = matrix.getWidth();
			image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
			image.createGraphics();

			Graphics2D graphics = (Graphics2D) image.getGraphics();
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, matrixWidth, matrixWidth);
			graphics.setColor(Color.BLACK);

			for (int b = 0; b < matrixWidth; b++) {
				for (int j = 0; j < matrixWidth; j++) {
					if (matrix.get(b, j)) {
						graphics.fillRect(b, j, 1, 1);
					}
				}
			}
			logger.info("Imagen QR: "+image.toString());

 
		return image;
	}
  	
    
}
