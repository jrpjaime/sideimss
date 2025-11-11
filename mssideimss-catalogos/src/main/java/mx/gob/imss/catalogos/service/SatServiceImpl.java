package mx.gob.imss.catalogos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mx.gob.imss.catalogos.dto.RfcColegioRequestDto;
import mx.gob.imss.catalogos.dto.RfcColegioResponseDto;

@Service("satService")
public class SatServiceImpl implements SatService {
    private final static Logger logger = LoggerFactory.getLogger(SatServiceImpl.class);


    @Override
    public RfcColegioResponseDto consultarRfc(RfcColegioRequestDto rfcColegioRequestDto) {
        logger.info("Consulta de RFC: {}", rfcColegioRequestDto.getRfc());
        // **MOCK DE DATOS**
        RfcColegioResponseDto response = new RfcColegioResponseDto();
        response.setRfc(rfcColegioRequestDto.getRfc());
        response.setNombreRazonSocial("MOCK S.A. DE C.V.");

      
        logger.info("Respuesta MOCK para RFC {}: {}", rfcColegioRequestDto.getRfc(), response.getNombreRazonSocial());
        return response;
    }


}
