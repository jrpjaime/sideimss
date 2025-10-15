package mx.gob.imss.contadores.dto;
 
import lombok.Data;

@Data
public class AcreditacionMenbresiaResponseDto { 
	private String desRfc;   
    private Integer codigo;
    private String mensaje; 
	private String fechaActual;   
	private String urlDocumento; 
	
	
}
