package mx.gob.imss.autodeterminaciones.dto;

import lombok.Data;
@Data

public class MovSalidaParamDto {

    private Integer codigo;
	
	private String mensaje;
	
	private PorcentajesCalculoVO porcentajesCalculo;
	
	private ValoresObtenidosVO valoresObtenidos;
	
	private FactorReversionVO factorReversion;

	public MovSalidaParamDto() {
		this.codigo = null;
		this.porcentajesCalculo = new PorcentajesCalculoVO();
		this.valoresObtenidos = new ValoresObtenidosVO();
		this.factorReversion = new FactorReversionVO();
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public PorcentajesCalculoVO getPorcentajesCalculo() {
		return porcentajesCalculo;
	}

	public void setPorcentajesCalculo(PorcentajesCalculoVO porcentajesCalculo) {
		this.porcentajesCalculo = porcentajesCalculo;
	}

	public ValoresObtenidosVO getValoresObtenidos() {
		return valoresObtenidos;
	}

	public void setValoresObtenidos(ValoresObtenidosVO valoresObtenidos) {
		this.valoresObtenidos = valoresObtenidos;
	}

	public FactorReversionVO getFactorReversion() {
		return factorReversion;
	}

	public void setFactorReversion(FactorReversionVO factorReversion) {
		this.factorReversion = factorReversion;
	}
	
}
