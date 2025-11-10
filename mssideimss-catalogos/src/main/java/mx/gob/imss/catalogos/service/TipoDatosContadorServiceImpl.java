package mx.gob.imss.catalogos.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Service;

import mx.gob.imss.catalogos.dto.TipoDatoContadorDto; 

@Service("tipoDatosContadorService")
public class TipoDatosContadorServiceImpl implements TipoDatosContadorService {

    private final static Logger logger = LoggerFactory.getLogger(TipoDatosContadorServiceImpl.class);
        


    @Override
    public List<TipoDatoContadorDto> getTiposDatosContador() {  
        List<TipoDatoContadorDto> tipos = new ArrayList<>(); 

        
        tipos.add(new TipoDatoContadorDto("1", "Personales"));
        tipos.add(new TipoDatoContadorDto("2", "Del Despacho"));
        tipos.add(new TipoDatoContadorDto("3", "Del Colegio"));
        return tipos;
    }
}
