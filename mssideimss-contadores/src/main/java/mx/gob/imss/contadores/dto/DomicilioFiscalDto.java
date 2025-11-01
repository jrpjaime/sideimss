package mx.gob.imss.contadores.dto;

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
    private String entreCalle; // "Entre la calle de"
    private String yCalle; // "Y la calle de"
    private String colonia; // "Colonia (asentamiento)"
    private String localidad;
    private String municipioODelegacion;
    private String entidadFederativa;
    private String codigoPostal;
}