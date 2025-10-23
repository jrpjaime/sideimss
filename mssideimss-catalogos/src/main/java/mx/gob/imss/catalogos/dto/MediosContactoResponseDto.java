package mx.gob.imss.catalogos.dto;

import java.util.List;
import lombok.Data; // Si usas Lombok

@Data 
public class MediosContactoResponseDto {
    private String rfc;
    private List<MedioContactoDto> medios;
 
}
