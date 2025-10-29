package mx.gob.imss.contadores.dto;
 

import lombok.Data;

@Data
public class CadenaOriginalRequestDto {
    private String rfc;
    private String curp;
    private String nombreRazonSocial;
    private String cadenaOriginal;
}
