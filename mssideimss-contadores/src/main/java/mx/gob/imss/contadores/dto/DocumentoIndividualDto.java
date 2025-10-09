package mx.gob.imss.contadores.dto;

 
import lombok.Data;

@Data
public class DocumentoIndividualDto {
	 
 
	private String desRfc; 
	private String nomArchivo;   
	private String desPath;  
	private String documentoBase64;
  
    private Integer codigo;
    private String mensaje; 
	private String fechaActual;   // dd/MM/yyyy
    private String desPathHdfs; 
 
	
	
	
}
