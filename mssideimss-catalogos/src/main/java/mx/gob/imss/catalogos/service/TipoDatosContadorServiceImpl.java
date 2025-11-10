package mx.gob.imss.catalogos.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Service; 

@Service("tipoDatosContadorService")
public class TipoDatosContadorServiceImpl implements TipoDatosContadorService {

    private final static Logger logger = LoggerFactory.getLogger(TipoDatosContadorServiceImpl.class);
        


   @Override
    public List<String> getTiposDatosContador() {
        logger.info("Generando datos MOCK para tipos de datos de contador.");
        List<String> tipos = new ArrayList<>();
        tipos.add("Personales");
        tipos.add("Del Despacho");
        tipos.add("Del Colegio");
        return tipos;
    }

}
