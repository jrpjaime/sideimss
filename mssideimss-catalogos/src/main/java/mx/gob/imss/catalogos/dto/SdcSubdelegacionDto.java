package mx.gob.imss.catalogos.dto;

 
public class  SdcSubdelegacionDto  { 

	private Integer idSubdelegacion;
	private String cveSubdelegacion;
	private String desSubdelegacion;
	private Integer idDelegacion;

public SdcSubdelegacionDto(){
	}

	public Integer getIdSubdelegacion(){
		return  idSubdelegacion;
	}

	public void setIdSubdelegacion(Integer idSubdelegacion){
		this.idSubdelegacion= idSubdelegacion ;
	}

	public String getCveSubdelegacion(){
		return  cveSubdelegacion;
	}

	public void setCveSubdelegacion(String cveSubdelegacion){
		this.cveSubdelegacion= cveSubdelegacion ;
	}

	public String getDesSubdelegacion(){
		return  desSubdelegacion;
	}

	public void setDesSubdelegacion(String desSubdelegacion){
		this.desSubdelegacion= desSubdelegacion ;
	}

	public Integer getIdDelegacion(){
		return  idDelegacion;
	}

	public void setIdDelegacion(Integer idDelegacion){
		this.idDelegacion= idDelegacion ;
	}

}
