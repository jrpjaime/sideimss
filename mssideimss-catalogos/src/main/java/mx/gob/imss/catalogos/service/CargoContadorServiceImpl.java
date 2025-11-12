package mx.gob.imss.catalogos.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mx.gob.imss.catalogos.dto.CargoContadorDto;

@Service("cargoContadorService")
public class CargoContadorServiceImpl implements CargoContadorService {
    private final static Logger logger = LoggerFactory.getLogger(CargoContadorServiceImpl.class);
    
    @Override
    public List<CargoContadorDto> getCargosContador() {
        logger.info("Generando MOC para cargos de contador.");
        List<CargoContadorDto> cargos = new ArrayList<>();
        cargos.add(new CargoContadorDto("1", "Auditor"));
        cargos.add(new CargoContadorDto("2", "Director"));
        cargos.add(new CargoContadorDto("3", "Gerente"));
        cargos.add(new CargoContadorDto("4", "Socio"));
        return cargos;
    }

}
