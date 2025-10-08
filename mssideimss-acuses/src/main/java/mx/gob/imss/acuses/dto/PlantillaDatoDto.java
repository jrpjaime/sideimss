package mx.gob.imss.acuses.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class PlantillaDatoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long cveIdPlantillaDatos;
    private String nomDocumento;
    private String desVersion; // Corresponde a desVersion de PlantillaDato
    private String datosJson; // Aquí se pasará la cadena JSON

    // Constructor vacío
    public PlantillaDatoDto() {
    }

 

    @Override
    public String toString() {
        return "PlantillaDatoDto{" +
               "cveIdPlantillaDatos=" + cveIdPlantillaDatos +
               ", nomDocumento='" + nomDocumento + '\'' +
               ", desVersion='" + desVersion + '\'' +
               ", datosJson='" + datosJson + '\'' +
               '}';
    }
}