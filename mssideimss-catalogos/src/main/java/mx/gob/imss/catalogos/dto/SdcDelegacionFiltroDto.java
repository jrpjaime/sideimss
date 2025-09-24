package mx.gob.imss.catalogos.dto;

 
public class  SdcDelegacionFiltroDto  { 

	private Integer page; 
	private Integer size; 
	private String order; 
	private boolean asc; 
	private Integer idDelegacion;
	private String cveDelegacion;
	private String desDelegacion;

	public Integer getPage() {
		return page;
	}
	
	public void setPage(Integer page) {
		this.page = page;
	}
	
	public Integer getSize() {
		return size;
	}
	
	public void setSize(Integer size) {
		this.size = size;
	}
	
	public String getOrder() {
		return order;
	}
	
	public void setOrder(String order) {
		this.order = order;
	}
	
	public boolean isAsc() {
		return asc;
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


	public SdcDelegacionFiltroDto(){
	}

}
