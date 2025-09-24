package mx.gob.imss.autodeterminaciones.dto;
import java.util.List;

import lombok.Data;
@Data
public class MovimientoSalidaDto {
	
	private Integer codigo;
	
	private String mensaje;
	
	private List<DetalleCuotasVO> lstDetalleMovto;
	
	private DetalleCuotasVO detalleTrabajador;

	public MovimientoSalidaDto() {
        codigo = null;
        lstDetalleMovto = null;
        detalleTrabajador = null;
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

	public List<DetalleCuotasVO> getLstDetalleMovto() {
		return lstDetalleMovto;
	}

	public void setLstDetalleMovto(List<DetalleCuotasVO> lstDetalleMovto) {
		this.lstDetalleMovto = lstDetalleMovto;
	}

	public DetalleCuotasVO getDetalleTrabajador() {
		return detalleTrabajador;
	}

	public void setDetalleTrabajador(DetalleCuotasVO detalleTrabajador) {
		this.detalleTrabajador = detalleTrabajador;
	}

}
