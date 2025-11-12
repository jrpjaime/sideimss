package mx.gob.imss.catalogos.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mx.gob.imss.catalogos.dto.TipoSociedadFormaParteDto;

@Service("tipoSociedadContadorService")
public class TipoSociedadContadorServiceImpl implements TipoSociedadContadorService { 
private final static Logger logger = LoggerFactory.getLogger(TipoSociedadContadorServiceImpl.class);
    @Override
    public List<TipoSociedadFormaParteDto> getTiposSociedadFormaParte() {
        logger.info("Generando MOC para tipos de sociedad.");
        List<TipoSociedadFormaParteDto> tiposSociedad = new ArrayList<>();
        tiposSociedad.add(new TipoSociedadFormaParteDto("1", "Despacho"));
        tiposSociedad.add(new TipoSociedadFormaParteDto("2", "Independiente"));
        return tiposSociedad;
    }
}
