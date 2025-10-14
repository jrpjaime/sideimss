package mx.gob.imss.acuses.dto;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;
import mx.gob.imss.acuses.enums.TipoAcuse;

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

