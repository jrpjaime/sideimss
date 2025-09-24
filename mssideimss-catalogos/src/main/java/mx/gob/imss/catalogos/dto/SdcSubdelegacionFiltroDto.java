package mx.gob.imss.catalogos.dto;

import jakarta.validation.constraints.NotNull;
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
	@NotNull(message = "El campo 'Delegación' es requerido y no puede estar vacío.")
	private Integer idDelegacion;
 

	public SdcSubdelegacionFiltroDto(){
	}

}
