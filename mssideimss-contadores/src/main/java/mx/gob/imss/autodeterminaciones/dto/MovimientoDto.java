package mx.gob.imss.autodeterminaciones.dto;
import lombok.Data;
@Data
public class MovimientoDto {
	
	private String fecmovini;    //        20 LRE-FEC-MOV-INI   PIC  X(10).
	private String fecmovfin;    //        20 LRE-FEC-MOV-FIN   PIC  X(10).
	private Integer tpmovini;    //        20 LRE-TP-MOV-INI    PIC S9(02)V      USAGE COMP-3.
	private Integer tpmovfin;    //        20 LRE-TP-MOV-FIN    PIC S9(02)V      USAGE COMP-3.
	private Integer consec;    	 //        20 LRE-CONSEC        PIC S9(04)       USAGE COMP.
	private double salbas;       //        20 LRE-SAL-BAS       PIC S9(04)V9(02) USAGE COMP-3.
    private String idriss;       //        20 LRE-ID-RISS       PIC  X(01).
	private Integer pctjriss;    //        20 LRE-PCTJ-RISS     PIC  9(03).
	private double smgvdf;
	private double smdigvdf;
	private double smzona;       //        20 LRE-SM-ZONA       PIC  9(03)V9(03).
	private double smsdizona;    //        20 LRE-SM-SDI-ZONA   PIC  9(03)V9(03).
	private String ramoincap;    //        20 LRE-RAMO-INCAP    PIC  X(01).
	private String etapaproc;    //        20 LRE-ETAPA-PROC    PIC  X(01).
	private String semjor;       //        20 LRE-SEM-JOR       PIC  X(01).
	private double smdfUma;		 // 	   W500-SMGVDF-UMA
	private double sbcIV;		 // 	   LRE-SBC-IV
	private double sbcEym;		 //        LRE-SBC-EYM
	private float prcCyvPat;
	

}
