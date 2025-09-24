package mx.gob.imss.autodeterminaciones.dto;
 import lombok.Data;
@Data
public class  SdcDelegacionFiltroDto  { 

	private Integer page; 
	private Integer size; 
	private String order; 
	private boolean asc; 
	private Integer idDelegacion;
	private String cveDelegacion;
	private String desDelegacion;

}
