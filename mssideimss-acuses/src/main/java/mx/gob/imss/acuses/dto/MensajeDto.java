package mx.gob.imss.acuses.dto;
import lombok.Data;

@Data
public class MensajeDto {
    private String mensaje;

    public MensajeDto(String mensaje) {
        this.mensaje = mensaje;
    }

 
}
