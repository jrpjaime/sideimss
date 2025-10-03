package mx.gob.imss.documentos.dto;

 
import lombok.Data;

@Data
public class DocumentoIndividualVO {
	 
 
	private String desRfc; 
	private String nomArchivo;   
	private String desPath;  
	private String documentoBase64;
  
    private Integer codigo;
    private String mensaje; 
	private String fechaActual;   // dd/MM/yyyy
	   

 
	
	
	
}
