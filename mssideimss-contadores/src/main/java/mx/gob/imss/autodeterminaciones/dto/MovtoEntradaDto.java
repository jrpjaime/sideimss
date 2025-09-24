package mx.gob.imss.autodeterminaciones.dto;

import java.util.List;
import lombok.Data;
@Data
public class MovtoEntradaDto {

	private String patron;   		 //   LRE00-PATRON         PIC X(08).                       
	private Integer modalidad;    	 //   LRE00-MODALIDAD      PIC 99.                          
	private Integer tipocontrib;   	 //   LRE00-TIPOCONTRIB    PIC 9.                *    3 ES TRIPARTITA, 2 ES BIPARTITA                           
	private String periodo;    		 //   LRE00-PERIODO.                                        
	private Integer tipocalculo;   	 //   LRE00-TIPO-CALCULO   PIC 9.                *    1 MENSUAL 2 BIMESTRAL
	private String nss;   			 //   LRE00-NSS            PIC 9(11).                       
	private double primart;    		 //   LRE00-PRIMA-RT       PIC 9(03)V9(05).                 
	private String incsua;    		 //   LRE00-INC-SUA        PIC X.                *    INDICA SI EL CALCULO DEBE CONSIDERAR INCIDENCIAS SUA. *    'S' PARA SI, 'N' PARA NO
	private String calrever;         //   LRE00-CAL-REVER      PIC X.                *    INDICA SI EL CALCULO DEBE CONSIDERAR INCLUIR REVERSION DE CUO *    'S' PARA SI, 'N' PARA NO
	private String segesp;           //   LRE00-SEG-ESP        PIC X.                *    INDICA SI EL CALCULO ES PARA CASOS O MODALIDADES ESPECIALES  *    EN BASE A ESTE CAMPO SE ASIGNARAN LAS FECHAS DE INI Y FIN PER
	private Integer trapen;          //   LRE00-TRA-PEN        PIC 99.                          
	// Infonavit
	private String tipoAmort;		 //   LRE-TPO-AMORT
	private double porcOvsm;		 //   LRE-PORCE-OVS 
	private String cvemunpatron;
	
	private String obtenerparam;     //   LRE00-OBTENER-PARAM  PIC X.                *    INDICA SI SE DEBEN OBTENER NUEVAMENTE LOS PARAMETROS *    S=SI, N=NO
 	private String fecPago;			 //   LRE-FEC-PAGO
	
 	private String fim;    			 //   15 LRE01-FIM.      pic 9(08)                                 
 	private String ffm;    			 //   15 LRE01-FFM.      pic 9(08)                                 
 	private String fib;    			 //   15 LRE01-FIB.      pic 9(08)                                                                  
 	private String ffb;    			 //   15 LRE01-FFB.      pic 9(08)  
 	
 	private PorcentajesCalculoVO porcentajesCalculo;
 	private ValoresObtenidosVO valoresObtenidos;
 	private FactorReversionVO factorReversion;
 	
 	private List<MovimientoDto> movtos;

 	
}
