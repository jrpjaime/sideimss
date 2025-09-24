package mx.gob.imss.autodeterminaciones.dto;

import lombok.Data;

@Data
public class MensajeDto {
    private String mensaje;
    private Integer codigo;

    public MensajeDto(Integer codigo, String mensaje) {
        this.mensaje=mensaje;
        this.codigo=codigo;
        
    }

    public MensajeDto() { 
        
    }

 
}
