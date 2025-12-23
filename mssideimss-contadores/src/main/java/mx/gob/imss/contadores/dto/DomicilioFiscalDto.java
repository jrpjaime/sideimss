package mx.gob.imss.contadores.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DomicilioFiscalDto {
    private String calle;
    private String numeroExterior;
    private String numeroInterior;
    @JsonProperty("entreCalle")
    private String entreCalle; // "Entre la calle de"
    @JsonProperty("yCalle") 
    private String yCalle; // "Y la calle de"
    private String colonia; // "Colonia (asentamiento)"
    private String localidad;
    private String municipioODelegacion;
    private String entidadFederativa;
    private String codigoPostal;
}