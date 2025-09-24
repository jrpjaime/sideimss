package mx.gob.imss.autodeterminaciones.dto;
 
import lombok.Data;
@Data
public class DetalleCuotasRcvVO {

	// Días del periodo
	private Integer diasCotizados;
	private Integer diasIncapacidad;
	private Integer diasAusentismo;
	private Integer diasLicencia;
	private Integer diasCotizaMenIncap;
	private Integer diasCotizaMenAusen;
	private Integer diasCotizaMenLicen;
	private Integer diasCotizaMenil;
	private Integer diasCotizaMenila;
	
	// Datos de entrada y de paso
	private String fecMovIni;
	private String fecMovFin;
	private Integer tpMovIni;
	private Integer tpMovFin;
	private Integer consec;
	private double salTopado;
	private Float prcCyvPat;
	private double smGvDf;
	private double smdiGvDf;
	private double smZona;
	private double smdiZona;
	private double umi;
	
	// Cuotas
	private Double impCyvPat;			 // LR03-CYV-PAT
	private Double impCyvObr;			 // LR03-CYV-OBR
	private Double impActPCyv;			 //                
	private Double impRecPCyv;			 //
	private Double impActOCyv;			 //                
	private Double impRecOCyv;			 //
	private Double impRet;				 // LR03-RET
	private Double impActRet;			 //                
	private Double impRecRet;			 // 
	
	// Infonavit
	private int diasInfonavit;
	private int diasIncapInfo;
	private int diasLicenInfo;
	private int diasAusenInfo;
	private double valorDescuento;
	private Double impInfvt;			 // LRS03-INFVT
	private Double impInfvtAmort;		 // LRS03-INFVT-AMORT
	private String idRiss;
	
	// Totales
	private Double impTotalPatr;
	private Double impTotalAseg;
	private Double impTotalTot;
	
	public DetalleCuotasRcvVO() {
		this.diasCotizados = 0;
		this.diasIncapacidad = 0;
		this.diasAusentismo = 0;
		this.diasLicencia = 0;
		this.diasCotizaMenIncap = 0;
		this.diasCotizaMenAusen = 0;
		this.diasCotizaMenLicen = 0;
		this.diasCotizaMenil = 0;
		this.diasCotizaMenila = 0;
		
		this.impCyvPat = 0d;
		this.impCyvObr = 0d;
		this.impActPCyv = 0d;
		this.impRecPCyv = 0d;
		this.impActOCyv = 0d;
		this.impRecOCyv = 0d;
		this.impRet = 0d;
		this.impActRet = 0d;
		this.impRecRet = 0d;

		this.impInfvt = 0d;
		this.impInfvtAmort = 0d;
		this.impTotalPatr = 0d;
		this.impTotalAseg = 0d;
		this.impTotalTot = 0d;
	}
	

}
