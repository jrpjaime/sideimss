package mx.gob.imss.catalogos.dto;

 import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class  DitPatronGeneralFiltroDto  { 

	private Integer page; 
	private Integer size; 
	private String order; 
	private boolean asc; 
	private Integer idPatronGeneral;
	private String denominacionRazonSocial;
    @NotBlank(message = "El campo 'RFC' es requerido y no puede estar vacío.")
	private String rfc;
	private String registroPatronal;
	private String cveDelegacion;
	private String desDelegacion;
	private String cveSubdelegacion;
	private String desSubdelegacion;


	public DitPatronGeneralFiltroDto(){
	}

}
