package mx.gob.imss.autodeterminaciones.dto; 

import lombok.Data;
@Data

public class FactorReversionVO {
	
	private String faPatron;			// FA-PATRON
	private Integer tipoConvenio;		// FA_TIPO_COVE
	private String fecIniConvenio;	    // FA_INI_CONVE
	private String fecFinConvenio;	    // FA_FIN_CONVE
	private double porFrEymFija;
	private double porFrEymExcP;
	private double porFrEymExcO;
	private double porFrEymPredP;
	private double porFrEymPredO;
	private double porFrEymPreeP;
	private double porFrEymPreeO;
	private double porFrInvVidaP;
	private double porFrInvVidaO;
	private double porFrRiesTra;
	private double porFrGuar;
	private double porFrRet;
	private double porFrCyvP;
	private double porFrCyvO;
	private double porFaiEymFija;
	private double porFaiEymExc;
	private double porFaiEymPred;
	private double porFaiEymPree;
	private double porFaiInvVida;
	private double porFaiRiesTra;
	private double porFaiGuar;
	private double porFaiRet;
	private double porFaiCyv;
	private double porBcEymFija;
	private double porBcEymExc;
	private double porBcEymPred;
	private double porBcEymPree;
	private double porBcInvVida;
	private double porBcRiesTra;
	private double porBcGuar;
	private double porBcRet;
	private double porBcCyv;
	
	public FactorReversionVO(String faPatron, Integer tipoConvenio, String fecIniConvenio,
			String fecFinConvenio, double porFrEymFija, double porFrEymExcP, double porFrEymExcO, double porFrEymPredP,
			double porFrEymPredO, double porFrEymPreeP, double porFrEymPreeO, double porFrInvVidaP,
			double porFrInvVidaO, double porFrRiesTra, double porFrGuar, double porFrRet, double porFrCyvP,
			double porFrCyvO, double porFaiEymFija, double porFaiEymExc, double porFaiEymPred, double porFaiEymPree,
			double porFaiInvVida, double porFaiRiesTra, double porFaiGuar, double porFaiRet, double porFaiCyv,
			double porBcEymFija, double porBcEymExc, double porBcEymPred, double porBcEymPree, double porBcInvVida,
			double porBcRiesTra, double porBcGuar, double porBcRet, double porBcCyv) {
		this.faPatron = faPatron;
		this.tipoConvenio = tipoConvenio;
		this.fecIniConvenio = fecIniConvenio;
		this.fecFinConvenio = fecFinConvenio;
		this.porFrEymFija = porFrEymFija;
		this.porFrEymExcP = porFrEymExcP;
		this.porFrEymExcO = porFrEymExcO;
		this.porFrEymPredP = porFrEymPredP;
		this.porFrEymPredO = porFrEymPredO;
		this.porFrEymPreeP = porFrEymPreeP;
		this.porFrEymPreeO = porFrEymPreeO;
		this.porFrInvVidaP = porFrInvVidaP;
		this.porFrInvVidaO = porFrInvVidaO;
		this.porFrRiesTra = porFrRiesTra;
		this.porFrGuar = porFrGuar;
		this.porFrRet = porFrRet;
		this.porFrCyvP = porFrCyvP;
		this.porFrCyvO = porFrCyvO;
		this.porFaiEymFija = porFaiEymFija;
		this.porFaiEymExc = porFaiEymExc;
		this.porFaiEymPred = porFaiEymPred;
		this.porFaiEymPree = porFaiEymPree;
		this.porFaiInvVida = porFaiInvVida;
		this.porFaiRiesTra = porFaiRiesTra;
		this.porFaiGuar = porFaiGuar;
		this.porFaiRet = porFaiRet;
		this.porFaiCyv = porFaiCyv;
		this.porBcEymFija = porBcEymFija;
		this.porBcEymExc = porBcEymExc;
		this.porBcEymPred = porBcEymPred;
		this.porBcEymPree = porBcEymPree;
		this.porBcInvVida = porBcInvVida;
		this.porBcRiesTra = porBcRiesTra;
		this.porBcGuar = porBcGuar;
		this.porBcRet = porBcRet;
		this.porBcCyv = porBcCyv;
	}

	public FactorReversionVO() {
		
	}

	
}
