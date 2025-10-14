package mx.gob.imss.acuses.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Base64;

import javax.imageio.ImageIO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException; 
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger; 
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
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
   

	@Override
public InputStream generaQRImageInputStream(String content) throws WriterException, IOException {
    // 1. Configuración y Generación de la Matriz QR (ZXing)
    int size = 220;
    QRCodeWriter qrcode = new QRCodeWriter();
    
    // Esta línea lanza com.google.zxing.WriterException
    BitMatrix matrix = qrcode.encode(content, BarcodeFormat.QR_CODE, size, size);
    
    // 2. Creación del BufferedImage
    int matrixWidth = matrix.getWidth();
    BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
    image.createGraphics();

    Graphics2D graphics = (Graphics2D) image.getGraphics();
    graphics.setColor(Color.WHITE); // Color de fondo
    graphics.fillRect(0, 0, matrixWidth, matrixWidth);
    graphics.setColor(Color.BLACK); // Color del código QR

    // Pintar la matriz
    for (int b = 0; b < matrixWidth; b++) {
        for (int j = 0; j < matrixWidth; j++) {
            if (matrix.get(b, j)) {
                graphics.fillRect(b, j, 1, 1);
            }
        }
    }
    
 
    
    // 3. Escribir el BufferedImage en un ByteArrayOutputStream
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    
    // ImageIO.write lanza java.io.IOException
    // Escribimos la imagen en formato PNG  
    ImageIO.write(image, "PNG", os); 
    
    // 4. Convertir el ByteArrayOutputStream a ByteArrayInputStream
    // El ByteArrayInputStream es un tipo de InputStream que opera sobre la matriz de bytes del QR.
    return new ByteArrayInputStream(os.toByteArray());
}
    
}
