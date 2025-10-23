package mx.gob.imss.contadores.dto;

import java.util.List;
import lombok.Data; 

@Data
public class MediosContactoContadoresResponseDto {
    private String rfc;
    private List<MedioContactoContadoresDto> medios;
}