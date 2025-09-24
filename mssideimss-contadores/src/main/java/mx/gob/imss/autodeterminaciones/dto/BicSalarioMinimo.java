package mx.gob.imss.autodeterminaciones.dto;
import lombok.Data;
import java.util.Date;  
 
@Data
public class BicSalarioMinimo {
 
	Long cveIdSalarioGeneral;
	Long cveIdAreaGeografica;
	Date fecRegistro;
	Date fecInicioVigencia;
	Double salarioMinimo;
	Double porcentajeIncremento;
	Date fecFinVigencia;
	Double salMinTntegrado;

}
