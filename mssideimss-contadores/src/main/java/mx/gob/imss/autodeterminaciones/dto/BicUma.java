package mx.gob.imss.autodeterminaciones.dto;

import java.util.Date;  
import lombok.Data;
@Data
public class BicUma {
	Long cveIdUma;
	Double umaDiario;
	Double umaMensual;
	Date fecInicioVigencia;
	Date fecFinVigencia;
 
	
}
