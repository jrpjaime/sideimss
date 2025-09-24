package mx.gob.imss.autodeterminaciones.dto;

 import lombok.Data;
@Data
public class  SdcSubdelegacionFiltroDto  { 

	private Integer page; 
	private Integer size; 
	private String order; 
	private boolean asc; 
	private Integer idSubdelegacion;
	private String cveSubdelegacion;
	private String desSubdelegacion;
	private Integer idDelegacion;


}
