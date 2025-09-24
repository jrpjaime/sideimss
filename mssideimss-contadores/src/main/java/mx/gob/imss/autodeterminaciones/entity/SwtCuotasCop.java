package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal; 

@Data
@Entity
@Table(name = "SWT_CUOTAS_COP")
public class SwtCuotasCop {

    @Id
    @SequenceGenerator(name = "inc_swt_cuotas_cop", sequenceName = "SWTS_CUOTAS_COP", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_cuotas_cop")
    @Column(name = "ID_CUOTAS_COP", nullable = false)
    private Long idCuotasCop;

    @Column(name = "NUM_DIAS_COTIZADOS")
    private Integer numDiasCotizados;

    @Column(name = "NUM_DIAS_INCAPACIDAD")
    private Integer numDiasIncapacidad;

    @Column(name = "NUM_DIAS_AUSENTISMO")
    private Integer numDiasAusentismo;

    @Column(name = "NUM_DIAS_LICENCIA")
    private Integer numDiasLicencia;

    @Column(name = "NUM_DIAS_COTIZA_MEN_INCAP")
    private Integer numDiasCotizaMenIncap;

    @Column(name = "NUM_DIAS_COTIZA_MEN_AUSEN")
    private Integer numDiasCotizaMenAusen;

    @Column(name = "NUM_DIAS_COTIZA_MEN_LICEN")
    private Integer numDiasCotizaMenLicen;

    @Column(name = "NUM_DIAS_COTIZA_MENIL")
    private Integer numDiasCotizaMenil;

    @Column(name = "NUM_DIAS_COTIZA_MENILA")
    private Integer numDiasCotizaMenila;

    @Column(name = "REF_FEC_MOV_INI")
    private String refFecMovIni;

    @Column(name = "REF_FEC_MOV_FIN")
    private String refFecMovFin;

    @Column(name = "NUM_TP_MOV_INI")
    private Integer numTpMovIni;

    @Column(name = "NUM_TP_MOV_FIN")
    private Integer numTpMovFin;

    @Column(name = "NUM_CONSEC")
    private Integer numConsec;

    @Column(name = "IMP_SAL_TOPADO")
    private BigDecimal impSalTopado;

    @Column(name = "IMP_SAL_TOPADO_2")
    private BigDecimal impSalTopado2;

    @Column(name = "IMP_SAL_TOPADO_IV")
    private BigDecimal impSalTopadoIv;

    @Column(name = "IMP_SM_GV_DF")
    private BigDecimal impSmGvDf;

    @Column(name = "IMP_SMDI_GV_DF")
    private BigDecimal impSmdiGvDf;

    @Column(name = "IMP_SM_ZONA")
    private BigDecimal impSmZona;

    @Column(name = "IMP_SMDI_ZONA")
    private BigDecimal impSmdiZona;

    @Column(name = "IMP_UMI")
    private BigDecimal impUmi;

    @Column(name = "IMP_C_FIJA_100")
    private BigDecimal impCFija100;

    @Column(name = "IMP_AUS_CF")
    private BigDecimal impAusCf;

    @Column(name = "IMP_REV_CF")
    private BigDecimal impRevCf;

    @Column(name = "IMP_C_F_MEN_AUS_REV")
    private BigDecimal impCFMenAusRev;

    @Column(name = "IMP_A_DEDUC_CF")
    private BigDecimal impADeducCf;

    @Column(name = "IMP_C_F_MEN_AUS_REV_DEDUC")
    private BigDecimal impCFMenAusRevDeduc;

    @Column(name = "IMP_C_F_DEDUC_REMAN")
    private BigDecimal impCFDeducReman;

    @Column(name = "IMP_ACT_CF")
    private BigDecimal impActCf;

    @Column(name = "IMP_REC_CF")
    private BigDecimal impRecCf;

    @Column(name = "IMP_EXC_P_100")
    private BigDecimal impExcP100;

    @Column(name = "IMP_AUS_EXC_P")
    private BigDecimal impAusExcP;

    @Column(name = "IMP_REV_EXC_P")
    private BigDecimal impRevExcP;

    @Column(name = "IMP_EXC_P_MEN_AUS_REV")
    private BigDecimal impExcPMenAusRev;

    @Column(name = "IMP_EXC_P_MEN_AUS_REV_DEDUC")
    private BigDecimal impExcPMenAusRevDeduc;

    @Column(name = "IMP_ACT_EXC_P")
    private BigDecimal impActExcP;

    @Column(name = "IMP_REC_EXP_P")
    private BigDecimal impRecExpP;

    @Column(name = "IMP_EXC_O_100")
    private BigDecimal impExcO100;

    @Column(name = "IMP_AUS_EXC_O")
    private BigDecimal impAusExcO;

    @Column(name = "IMP_REV_EXC_O")
    private BigDecimal impRevExcO;

    @Column(name = "IMP_EXC_O_MEN_AUS_REV")
    private BigDecimal impExcOMenAusRev;

    @Column(name = "IMP_EXC_O_MEN_AUS_REV_DEDUC")
    private BigDecimal impExcOMenAusRevDeduc;

    @Column(name = "IMP_ACT_EXC_O")
    private BigDecimal impActExcO;

    @Column(name = "IMP_REC_EXC_O")
    private BigDecimal impRecExcO;

    @Column(name = "IMP_PD_P_100")
    private BigDecimal impPdP100;

    @Column(name = "IMP_AUS_PD_P")
    private BigDecimal impAusPdP;

    @Column(name = "IMP_REV_PD_P")
    private BigDecimal impRevPdP;

    @Column(name = "IMP_PD_P_MEN_AUS_REV")
    private BigDecimal impPdPMenAusRev;

    @Column(name = "IMP_PD_P_MEN_AUS_REV_DEDUC")
    private BigDecimal impPdPMenAusRevDeduc;

    @Column(name = "IMP_ACT_PD_P")
    private BigDecimal impActPdP;

    @Column(name = "IMP_REC_PD_P")
    private BigDecimal impRecPdP;

    @Column(name = "IMP_PD_O_100")
    private BigDecimal impPdO100;

    @Column(name = "IMP_AUS_PD_O")
    private BigDecimal impAusPdO;

    @Column(name = "IMP_REV_PD_O")
    private BigDecimal impRevPdO;

    @Column(name = "IMP_PD_O_MEN_AUS_REV")
    private BigDecimal impPdOMenAusRev;

    @Column(name = "IMP_PD_O_MEN_AUS_REV_DEDUC")
    private BigDecimal impPdOMenAusRevDeduc;

    @Column(name = "IMP_ACT_PD_O")
    private BigDecimal impActPdO;

    @Column(name = "IMP_REC_PD_O")
    private BigDecimal impRecPdO;

    @Column(name = "IMP_GMP_P_100")
    private BigDecimal impGmpP100;

    @Column(name = "IMP_AUS_GMP_P")
    private BigDecimal impAusGmpP;

    @Column(name = "IMP_REV_GMP_P")
    private BigDecimal impRevGmpP;

    @Column(name = "IMP_GMP_P_MEN_AUS_REV")
    private BigDecimal impGmpPMenAusRev;

    @Column(name = "IMP_GMP_P_MEN_AUS_REV_DEDUC")
    private BigDecimal impGmpPMenAusRevDeduc;

    @Column(name = "IMP_ACT_GMP_P")
    private BigDecimal impActGmpP;

    @Column(name = "IMP_REC_GMP_P")
    private BigDecimal impRecGmpP;

    @Column(name = "IMP_GMP_O_100")
    private BigDecimal impGmpO100;

    @Column(name = "IMP_AUS_GMP_O")
    private BigDecimal impAusGmpO;

    @Column(name = "IMP_REV_GMP_O")
    private BigDecimal impRevGmpO;

    @Column(name = "IMP_GMP_O_MEN_AUS_REV")
    private BigDecimal impGmpOMenAusRev;

    @Column(name = "IMP_GMP_O_MEN_AUS_REV_DEDUC")
    private BigDecimal impGmpOMenAusRevDeduc;

    @Column(name = "IMP_ACT_GMP_O")
    private BigDecimal impActGmpO;

    @Column(name = "IMP_REC_GMP_O")
    private BigDecimal impRecGmpO;

    @Column(name = "IMP_IV_P_100")
    private BigDecimal impIvP100;

    @Column(name = "IMP_AUS_IV_P")
    private BigDecimal impAusIvP;

    @Column(name = "IMP_REV_IV_P")
    private BigDecimal impRevIvP;

    @Column(name = "IMP_IV_P_MEN_AUS_REV")
    private BigDecimal impIvPMenAusRev;

    @Column(name = "IMP_ACT_IV_P")
    private BigDecimal impActIvP;

    @Column(name = "IMP_REC_IV_P")
    private BigDecimal impRecIvP;

    @Column(name = "IMP_IV_O_100")
    private BigDecimal impIvO100;

    @Column(name = "IMP_AUS_IV_O")
    private BigDecimal impAusIvO;

    @Column(name = "IMP_REV_IV_O")
    private BigDecimal impRevIvO;

    @Column(name = "IMP_IV_O_MEN_AUS_REV")
    private BigDecimal impIvOMenAusRev;

    @Column(name = "IMP_ACT_IV_O")
    private BigDecimal impActIvO;

    @Column(name = "IMP_REC_IV_O")
    private BigDecimal impRecIvO;

    @Column(name = "IMP_RT_100")
    private BigDecimal impRt100;

    @Column(name = "IMP_AUS_RT")
    private BigDecimal impAusRt;

    @Column(name = "IMP_REV_RT")
    private BigDecimal impRevRt;

    @Column(name = "IMP_RT_MEN_AUS_REV")
    private BigDecimal impRtMenAusRev;

    @Column(name = "IMP_ACT_RT")
    private BigDecimal impActRt;

    @Column(name = "IMP_REC_RT")
    private BigDecimal impRecRt;

    @Column(name = "IMP_GUAR_100")
    private BigDecimal impGuar100;

    @Column(name = "IMP_AUS_GUAR")
    private BigDecimal impAusGuar;

    @Column(name = "IMP_REV_GUAR")
    private BigDecimal impRevGuar;

    @Column(name = "IMP_GUAR_MEN_AUS_REV")
    private BigDecimal impGuarMenAusRev;

    @Column(name = "IMP_ACT_GUAR")
    private BigDecimal impActGuar;

    @Column(name = "IMP_REC_GUAR")
    private BigDecimal impRecGuar;

    @Column(name = "IND_ID_RISS")
    private Integer indIdRiss;

    @Column(name = "IMP_TOTAL_PATR")
    private BigDecimal impTotalPatr;

    @Column(name = "IMP_TOTAL_ASEG")
    private BigDecimal impTotalAseg;

    @Column(name = "IMP_TOTAL_TOT")
    private BigDecimal impTotalTot;

    @Column(name = "ID_ASEGURADO")
    private Integer idAsegurado;

    @Column(name = "IND_TIPO")
    private Integer indTipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ASEGURADO", insertable=false, updatable=false)
    private SwtAsegurado swtAsegurado;

}
