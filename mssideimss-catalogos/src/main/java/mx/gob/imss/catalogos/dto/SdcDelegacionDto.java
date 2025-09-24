package mx.gob.imss.catalogos.dto;
 
public class  SdcDelegacionDto  { 

	private Integer idDelegacion;
	private String cveDelegacion;
	private String desDelegacion;

public SdcDelegacionDto(){
	}

	public Integer getIdDelegacion(){
		return  idDelegacion;
	}

	public void setIdDelegacion(Integer idDelegacion){
		this.idDelegacion= idDelegacion ;
	}

	public String getCveDelegacion(){
		return  cveDelegacion;
	}

	public void setCveDelegacion(String cveDelegacion){
		this.cveDelegacion= cveDelegacion ;
	}

	public String getDesDelegacion(){
		return  desDelegacion;
	}

	public void setDesDelegacion(String desDelegacion){
		this.desDelegacion= desDelegacion ;
	}

}
