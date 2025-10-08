package mx.gob.imss.acuses.dto;
import lombok.Data;

@Data
public class DecargarAcuseDto {
	
	private Integer codigo; //  1=Error 0=correcto
	private String mensaje; 
	
	private String desRfc; //rfc del cual se realiza la consulta 
	private String nombreRazonSocial;// razon social del cual se realiza la consulta  
	private String documento; // documento data:application/pdf;base64 
	private String nombreDocumento;
 
}
