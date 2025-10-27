package mx.gob.imss.acuses.service;
 
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
     
        JSONObject jsonWidget = new JSONObject(); 
        
        jsonWidget.put("rfc", cadenaOriginalRequestDto.getRfc());  
        jsonWidget.put("aplicacion", "GENERICO_ID_OP");  
        jsonWidget.put("id_llavefirma", "IMSS_CSD_01"); 
        jsonWidget.put("cadenaoriginal", cadenaOriginalRequestDto.getCadenaOriginal());

        // Usar ObjectFactory para crear la request
        FirmaSimpleRequestType request = objectFactory.createFirmaSimpleRequestType();
        request.setJsonParms(jsonWidget.toString());
        
        // ¡Ahora sí! Usar la instancia inyectada para llamar al método
        FirmaSimpleResponseType response = firmaElectronicaSegPortType.firmaSimple(request); 

        String respuesta = response.getJsonSalida();

        JSONObject objetoJson;
        if (respuesta != null && !respuesta.isEmpty()) {
            objetoJson = new JSONObject(respuesta);
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