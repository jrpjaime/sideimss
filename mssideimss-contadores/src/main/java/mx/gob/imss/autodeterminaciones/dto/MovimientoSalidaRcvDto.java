package mx.gob.imss.autodeterminaciones.dto;
import java.util.List;

import lombok.Data;
@Data 

public class MovimientoSalidaRcvDto {

    private Integer codigo;
	
	private String mensaje;
	
	private List<DetalleCuotasRcvVO> lstDetalleMovto;
	
	private DetalleCuotasRcvVO detalleTrabajador;

	public MovimientoSalidaRcvDto() {
        codigo = null;
        lstDetalleMovto = null;
        detalleTrabajador = null;
	}

	
}
