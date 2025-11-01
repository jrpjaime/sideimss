package mx.gob.imss.contadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatosPersonalesDto {
    private String rfc;
    private String curp;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String nombre;
    private String registroIMSS;
    private String estatus;
    private String delegacion;
    private String subdelegacion;
}