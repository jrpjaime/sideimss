package mx.gob.imss.autodeterminaciones.dto;

 import lombok.Data;
@Data
public class  SwcRoleFiltroDto  { 

	private Integer page; 
	private Integer size; 
	private String order; 
	private boolean asc; 
	private Integer idRole;
	private String desRole;
 

}
