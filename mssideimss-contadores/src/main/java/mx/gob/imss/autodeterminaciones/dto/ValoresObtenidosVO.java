package mx.gob.imss.autodeterminaciones.dto;

import java.util.List;


import lombok.Data;
@Data
public class ValoresObtenidosVO {
	
	private String fechaInicial;
	private String fechaFinal;
	private int diasPeriodo;
	private List<BicUma> umas;				// LRE07-UMAS
	private List<BicSalarioMinimo> smgvdf;  // LR02-SM-SMGVDF
	private List<BicSalarioMinimo> smgvzona;  		// 
 	private int topeSalarial;  		 		// LR04-TOPE-SALARIAL   PIC 9(02).                       
 	private int topeSalarialIV ;   			// LR04-TOPE-SALARIALIV PIC 9(02).  
 	private double factorbipart;    		// LR05-FACTOR-BIPART   PIC 9(03)V9(03).
 	private double valorInfonavit;
 	private double factorAct;				// LRE08-FACT-ACT
 	private double factorRec;				// LRE08-FACT-REC
 

}
