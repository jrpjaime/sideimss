package mx.gob.imss.acuses.dto;


import mx.gob.imss.acuses.enums.TipoAcuse;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;


@Data
public class PlantillaDatoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long cveIdPlantillaDatos;
    private String nomDocumento;
    private String desVersion; // Corresponde a desVersion de PlantillaDato
    private String datosJson; // Aquí se pasará la cadena JSON
    private TipoAcuse tipoAcuse;

    private Map<String, Object> additionalParameters;

    public PlantillaDatoDto() {
    }


}

