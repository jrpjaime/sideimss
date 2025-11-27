package mx.gob.imss.catalogos.service;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mx.gob.imss.catalogos.dto.DespachoRequestDto;
import mx.gob.imss.catalogos.dto.DespachoResponseDto;
 

@Service("despachoService")
public class DespachoServiceImpl implements DespachoService {
    private final static Logger logger = LoggerFactory.getLogger(DespachoServiceImpl.class);
    

       @Override
    public DespachoResponseDto consultarDatosDespacho(DespachoRequestDto request) {
        logger.info("Simulando consulta de despacho para RFC: {}", request.getRfc());
        
        // Simulación MOCK  
        if ("MOGF5304159BA".equalsIgnoreCase(request.getRfc())) {
            DespachoResponseDto response = new DespachoResponseDto();
            response.setRfcDespacho("MOSB650818PB4");
            response.setNombreRazonSocial("BEATRIZ MORENO SALINAS de gortari");
            
            
            response.setCveIdTipoSociedad("2"); // Independiente
            response.setDesTipoSociedad("Independiente");
            response.setCveIdCargoContador("2"); // Director
            response.setDesCargoContador("Director");
            response.setTelefonoFijo("5857564355");
            response.setTieneTrabajadores("No");
            response.setNumeroTrabajadores("100");
            
            return response;
        } else {
            // Si no coincide el RFC simulado, retornamos null
            return null;
        }
    }
 
}
