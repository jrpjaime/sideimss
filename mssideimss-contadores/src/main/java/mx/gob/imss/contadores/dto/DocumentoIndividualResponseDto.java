package mx.gob.imss.contadores.dto;

 
import lombok.Data;

@Data
public class DocumentoIndividualResponseDto {
	 
 
	private String desRfc; 
 
  
    private Integer codigo;
    private String mensaje; 
	private String fechaActual;   // dd/MM/yyyy
    private String desPathHdfsAcreditacion; 
    private String desPathHdfsMembresia; 
	
	
	
}
