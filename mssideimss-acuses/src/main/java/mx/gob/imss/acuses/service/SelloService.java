package mx.gob.imss.acuses.service;
 
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.stereotype.Service;

import mx.gob.imss.acuses.dto.CadenaOriginalRequestDto;
 
import mx.gob.imss.acuses.wsfirmaelectronicaseg.FirmaElectronicaSegPortType;  
import mx.gob.imss.acuses.wsfirmaelectronicaseg.FirmaSimpleRequestType;
import mx.gob.imss.acuses.wsfirmaelectronicaseg.FirmaSimpleResponseType;
import mx.gob.imss.acuses.wsfirmaelectronicaseg.ObjectFactory;  


@Service
public class SelloService {

    private static final Logger logger = LogManager.getLogger(SelloService.class);

    @Autowired  
    private FirmaElectronicaSegPortType firmaElectronicaSegPortType;  

    @Autowired  
    private ObjectFactory objectFactory; 

    public String generarSelloDigital(CadenaOriginalRequestDto cadenaOriginalRequestDto) throws Exception {
        logger.info("Generando sello digital para cadena original: {}", cadenaOriginalRequestDto.getCadenaOriginal());
        logger.info("Generando sello digital para curp: {}", cadenaOriginalRequestDto.getCurp());
        logger.info("Generando sello digital para razon_social: {}", cadenaOriginalRequestDto.getNombreRazonSocial()); 
     
        JSONObject jsonWidget = new JSONObject(); 
        
        jsonWidget.put("rfc", cadenaOriginalRequestDto.getRfc());  
        jsonWidget.put("aplicacion", "GENERICO_ID_OP");  
        jsonWidget.put("id_llavefirma", "IMSS_CSD_01"); 
        jsonWidget.put("cadenaoriginal", cadenaOriginalRequestDto.getCadenaOriginal());
        jsonWidget.put("curp", cadenaOriginalRequestDto.getCurp());
        jsonWidget.put("nombre_rs", cadenaOriginalRequestDto.getNombreRazonSocial());
        jsonWidget.put("razon_social", cadenaOriginalRequestDto.getNombreRazonSocial());
        jsonWidget.put("nombre", cadenaOriginalRequestDto.getNombreRazonSocial());

        // Usar ObjectFactory para crear la request
        FirmaSimpleRequestType request = objectFactory.createFirmaSimpleRequestType();
        request.setJsonParms(jsonWidget.toString());
        
        // ¡Ahora sí! Usar la instancia inyectada para llamar al método
        FirmaSimpleResponseType response = firmaElectronicaSegPortType.firmaSimple(request); 

        String respuesta = response.getJsonSalida();

        JSONObject objetoJson;
        if (respuesta != null && !respuesta.isEmpty()) {
            objetoJson = new JSONObject(respuesta);


           logger.info("Elementos de la respuesta del servicio de sellado:");
            Iterator<String> keys = objetoJson.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                Object value = objetoJson.opt(key); // Usar opt para evitar NullPointerException si la clave no existe
                logger.info("  {}: {}", key, value);
            }


            String selloDigital = objetoJson.optString("sello"); 
            if (selloDigital == null || selloDigital.trim().isEmpty()) {
                throw new Exception("El servicio de sellado no devolvió un sello válido.");
            }
            return selloDigital;
        } else {
            throw new Exception("El servicio de sellado no devolvió una respuesta válida.");
        }
    }
}