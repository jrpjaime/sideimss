package mx.gob.imss.catalogos.dto;

import lombok.Data;
@Data
public class  DitPatronGeneralDto  { 

	private Integer idPatronGeneral;
	private String denominacionRazonSocial;
	private String rfc;
	private String registroPatronal;
	private String cveDelegacion;
	private String desDelegacion;
	private String cveSubdelegacion;
	private String desSubdelegacion;

	public DitPatronGeneralDto(){
	}

 

}
