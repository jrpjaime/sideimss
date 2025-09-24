package mx.gob.imss.autodeterminaciones.dto; 

import lombok.Data;
@Data

public class DetalleCuotasVO {

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
	private double salTopado2;
	private double salTopadoIV;
	private double smGvDf;
	private double smdiGvDf;
	private double smZona;
	private double smdiZona;
	private double umi;
	
	// Cuota fija
	private Double impCFija100;		 	 // LRS03-IMP-CF-100
	private Double ImpAusCf;			 //	LRS03-IMP-AUS-CF
	private Double impRevCf;			 //	LRS03-IMP-REV-CF
	private Double impCFMenAusRev;		 //	LRS03-IMP-CF-AUS-REV           
	private Double impADeducCf;			 // LRS03-IMP-A-DEDUC-CF           
	private Double impCFMenAusRevDeduc;	 //	LRS03-IMP-CF-AUS-REV-DEDUC
	private Double impCFDeducReman;		 //
	private Double impActCf;			 // LRS03-IMP-ACT-CF               
	private Double impRecCf;			 // LRS03-IMP-REC-CF               
	
	// Excedente
	private Double impExcP100;			 // LRS03-IMP-EXC-P-100            
	private Double impAusExcP;			 // LRS03-IMP-AUS-EXC-P            
	private Double impRevExcP;			 // LRS03-IMP-REV-EXC-P            
	private Double impExcPMenAusRev;	 // LRS03-IMP-EXC-P-AUS-REV        
	private Double impExcPMenAusRevDeduc;// LRS03-IMP-EXC-P-AUS-REV-DEDUC  
	private Double impActExcP;			 // LRS03-IMP-ACT-EXC-P            
	private Double impRecExpP;			 // LRS03-IMP-REC-EXP-P            
	private Double impExcO100;			 // LRS03-IMP-EXC-O-100    
	private Double impAusExcO;			 // LRS03-IMP-AUS-EXC-O            
	private Double impRevExcO;			 // LRS03-IMP-REV-EXC-O            
	private Double impExcOMenAusRev;	 // LRS03-IMP-EXC-O-AUS-REV        
	private Double impExcOMenAusRevDeduc;// LRS03-IMP-EXC-O-AUS-REV-DEDUC  
	private Double impActExcO;			 // LRS03-IMP-ACT-EXC-O            
	private Double impRecExcO;			 // LRS03-IMP-REC-EXP-O         
	
	// Prestamos dinero
	private Double impPdP100;			 //	LRS03-IMP-PD-P-100
	private Double impAusPdP;			 // LRS03-IMP-AUS-PD-P             
	private Double impRevPdP;			 // LRS03-IMP-REV-PD-P             
	private Double impPdPMenAusRev;		 // LRS03-IMP-PD-P-AUS-REV         
	private Double impPdPMenAusRevDeduc; // LRS03-IMP-PD-P-AUS-REV-DEDUC   
	private Double impActPdP;			 // LRS03-IMP-ACT-PD-P             
	private Double impRecPdP;			 // LRS03-IMP-REC-PD-P             
	private Double impPdO100;			 // LRS03-IMP-PD-O-100             
	private Double impAusPdO;			 // LRS03-IMP-AUS-PD-O             
	private Double impRevPdO;			 // LRS03-IMP-REV-PD-O             
	private Double impPdOMenAusRev;		 // LRS03-IMP-PD-O-AUS-REV         
	private Double impPdOMenAusRevDeduc; // LRS03-IMP-PD-O-AUS-REV-DEDUC   
	private Double impActPdO;			 // LRS03-IMP-ACT-PD-O             
	private Double impRecPdO;			 // LRS03-IMP-REC-PD-O             
	
	// Gastos médicos
	private Double impGmpP100;			 // LRS03-IMP-GMP-P-100            
	private Double impAusGmpP;			 // LRS03-IMP-AUS-GMP-P            
	private Double impRevGmpP;			 // LRS03-IMP-REV-GMP-P            
	private Double impGmpPMenAusRev;	 // LRS03-IMP-GMP-P-AUS-REV        
	private Double impGmpPMenAusRevDeduc;// LRS03-IMP-GMP-P-AUS-REV-DEDUC  
	private Double impActGmpP;			 // LRS03-IMP-ACT-GMP-P            
	private Double impRecGmpP;			 // LRS03-IMP-REC-GMP-P            
	private Double impGmpO100;			 // LRS03-IMP-GMP-O-100            
	private Double impAusGmpO;			 // LRS03-IMP-AUS-GMP-O            
	private Double impRevGmpO;			 // LRS03-IMP-REV-GMP-O            
	private Double impGmpOMenAusRev;	 // LRS03-IMP-GMP-O-AUS-REV        
	private Double impGmpOMenAusRevDeduc;// LRS03-IMP-GMP-O-AUS-REV-DEDUC  
	private Double impActGmpO;			 // LRS03-IMP-ACT-GMP-O            
	private Double impRecGmpO;			 // LRS03-IMP-REC-GMP-O
	
	// Invalidez y vida
	private Double impIvP100;			 // LRS03-IMP-IV-P-100             
	private Double impAusIvP;			 // LRS03-IMP-AUS-IV-P             
	private Double impRevIvP;			 // LRS03-IMP-REV-IV-P             
	private Double impIvPMenAusRev;		 // LRS03-IMP-IV-P-AUS-REV         
	private Double impActIvP;			 // LRS03-IMP-ACT-IV-P             
	private Double impRecIvP;			 // LRS03-IMP-REC-IV-P             
	private Double impIvO100;			 // LRS03-IMP-IV-O-100             
	private Double impAusIvO;			 // LRS03-IMP-AUS-IV-O             
	private Double impRevIvO;			 // LRS03-IMP-REV-IV-O
	private Double impIvOMenAusRev;		 // LRS03-IMP-IV-O-AUS-REV
	private Double impActIvO;			 // LRS03-IMP-ACT-IV-O             
	private Double impRecIvO;			 // LRS03-IMP-REC-IV-O             
	
	// Riesgo trabajo
	private Double impRt100;			 // LRS03-IMP-RT-100               
	private Double impAusRt;			 // LRS03-IMP-AUS-RT               
	private Double impRevRt;			 // LRS03-IMP-REV-RT               
	private Double impRtMenAusRev;		 // LRS03-IMP-RT-AUS-REV           
	private Double impActRt;			 // LRS03-IMP-ACT-RT               
	private Double impRecRt;			 // LRS03-IMP-REC-RT               
	
	// Guarderias
	private Double impGuar100;			 // LRS03-IMP-GUAR-100             
	private Double impAusGuar;			 // LRS03-IMP-AUS-GUAR             
	private Double impRevGuar;			 // LRS03-IMP-REV-GUAR             
	private Double impGuarMenAusRev;	 // LRS03-IMP-GUAR-AUS-REV         
	private Double impActGuar;			 // LRS03-IMP-ACT-GUAR             
	private Double impRecGuar;			 // LRS03-IMP-REC-GUAR             
	
	// Infonavit
	//private Double impInfvt;			 // LRS03-INFVT
	//private Double impInfvtAmort;		 // LRS03-INFVT-AMORT
	private String idRiss;
	
	// Totales
	private Double impTotalPatr;
	private Double impTotalAseg;
	private Double impTotalTot;
	
	public DetalleCuotasVO() {
		this.diasCotizados = 0;
		this.diasIncapacidad = 0;
		this.diasAusentismo = 0;
		this.diasLicencia = 0;
		this.diasCotizaMenIncap = 0;
		this.diasCotizaMenAusen = 0;
		this.diasCotizaMenLicen = 0;
		this.diasCotizaMenil = 0;
		this.diasCotizaMenila = 0;
		
		this.impCFija100 = 0d;
		this.ImpAusCf = 0d;
		this.impRevCf = 0d;
		this.impCFMenAusRev = 0d;           
		this.impADeducCf = 0d;           
		this.impCFMenAusRevDeduc = 0d;
		this.impCFDeducReman = 0d;
		this.impActCf = 0d;
		this.impRecCf = 0d;
		
		this.impExcP100 = 0d;            
		this.impAusExcP = 0d;            
		this.impRevExcP = 0d;            
		this.impExcPMenAusRev = 0d;        
		this.impExcPMenAusRevDeduc = 0d;  
		this.impActExcP = 0d;           
		this.impRecExpP = 0d;            
		this.impExcO100 = 0d;  
		this.impAusExcO = 0d;         
		this.impRevExcO = 0d;          
		this.impExcOMenAusRev = 0d;       
		this.impExcOMenAusRevDeduc = 0d;
		this.impActExcO = 0d;           
		this.impRecExcO = 0d;
		
		this.impPdP100 = 0d;
		this.impAusPdP = 0d;             
		this.impRevPdP = 0d;            
		this.impPdPMenAusRev = 0d;         
		this.impPdPMenAusRevDeduc = 0d;   
		this.impActPdP = 0d;           
		this.impRecPdP = 0d;             
		this.impPdO100 = 0d;             
		this.impAusPdO = 0d;             
		this.impRevPdO = 0d;           
		this.impPdOMenAusRev = 0d;        
		this.impPdOMenAusRevDeduc = 0d;  
		this.impActPdO = 0d;
		this.impRecPdO = 0d;             
		
		this.impGmpP100 = 0d;          
		this.impAusGmpP = 0d;           
		this.impRevGmpP = 0d;            
		this.impGmpPMenAusRev = 0d;       
		this.impGmpPMenAusRevDeduc = 0d;  
		this.impActGmpP = 0d;           
		this.impRecGmpP = 0d;        
		this.impGmpO100 = 0d;         
		this.impAusGmpO = 0d;        
		this.impRevGmpO = 0d;            
		this.impGmpOMenAusRev = 0d;        
		this.impGmpOMenAusRevDeduc = 0d;  
		this.impActGmpO = 0d;          
		this.impRecGmpO = 0d;
		
		this.impIvP100 = 0d;  
		this.impAusIvP = 0d;        
		this.impRevIvP = 0d;        
		this.impIvPMenAusRev = 0d;      
		this.impActIvP = 0d;           
		this.impRecIvP = 0d;            
		this.impIvO100 = 0d;            
		this.impAusIvO = 0d;             
		this.impRevIvO = 0d;
		this.impIvOMenAusRev = 0d;
		this.impActIvO = 0d;             
		this.impRecIvO = 0d;             
		
		this.impRt100 = 0d;
		this.impAusRt = 0d;              
		this.impRevRt = 0d;               
		this.impRtMenAusRev = 0d;           
		this.impActRt  = 0d;              
		this.impRecRt  = 0d;               
		
		this.impGuar100 = 0d;             
		this.impAusGuar = 0d;             
		this.impRevGuar = 0d;            
		this.impGuarMenAusRev = 0d;       
		this.impActGuar = 0d;             
		this.impRecGuar = 0d;
		
		this.impTotalAseg = 0d;
		this.impTotalPatr = 0d;
		this.impTotalTot = 0d;
	}
	
	
}
