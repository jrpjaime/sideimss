package mx.gob.imss.catalogos.dto;

import lombok.Data; // Si usas Lombok

@Data
public class MedioContactoDto {
    private String tipoContacto;
    private String desFormaContacto;
    private String rfcAsociado;  
}