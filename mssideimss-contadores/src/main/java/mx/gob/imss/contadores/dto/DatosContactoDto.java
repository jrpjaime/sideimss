package mx.gob.imss.contadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosContactoDto {
    private String correoElectronico1;
    private String telefono1; //  Telefono 1
    private String correoElectronico2;
    private String correoElectronico3;
    private String telefono2; // El segundo campo Telefono2
}