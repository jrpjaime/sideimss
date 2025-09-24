package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal; 

@Data
@Entity
@Table(name = "SWT_CUOTAS_RCV")
public class SwtCuotasRcv {

    @Id
    @SequenceGenerator(name = "inc_swt_cuotas_rcv", sequenceName = "SWTS_CUOTAS_RCV", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_cuotas_rcv")
    @Column(name = "ID_CUOTAS_RCV", nullable = false)
    private Long idCuotasRcv;

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

    @Column(name = "IMP_PRC_CYV_PAT")
    private BigDecimal impPrcCyvPat;

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

    @Column(name = "IMP_CYV_PAT")
    private BigDecimal impCyvPat;

    @Column(name = "IMP_CYV_OBR")
    private BigDecimal impCyvObr;

    @Column(name = "IMP_ACT_P_CYV")
    private BigDecimal impActPCyv;

    @Column(name = "IMP_REC_P_CYV")
    private BigDecimal impRecPCyv;

    @Column(name = "IMP_ACT_O_CYV")
    private BigDecimal impActOCyv;

    @Column(name = "IMP_REC_O_CYV")
    private BigDecimal impRecOCyv;

    @Column(name = "IMP_RET")
    private BigDecimal impRet;

    @Column(name = "IMP_ACT_RET")
    private BigDecimal impActRet;

    @Column(name = "IMP_REC_RET")
    private BigDecimal impRecRet;

    @Column(name = "NUM_DIAS_INFONAVIT")
    private Integer numDiasInfonavit;

    @Column(name = "NUM_DIAS_INCAP_INFO")
    private Integer numDiasIncapInfo;

    @Column(name = "NUM_DIAS_LICEN_INFO")
    private Integer numDiasLicenInfo;

    @Column(name = "NUM_DIAS_AUSEN_INFO")
    private Integer numDiasAusenInfo;

    @Column(name = "IMP_VALOR_DESCUENTO")
    private BigDecimal impValorDescuento;

    @Column(name = "IMP_INFVT")
    private BigDecimal impInfvt;

    @Column(name = "IMP_INFVT_AMORT")
    private BigDecimal impInfvtAmort;

    @Column(name = "IND_ID_RISS")
    private Integer indIdRiss;

    @Column(name = "IMP_TOTAL_PATR")
    private BigDecimal impTotalPatr;

    @Column(name = "IMP_TOTAL_ASEG")
    private BigDecimal impTotalAseg;

    @Column(name = "IMP_TOTAL_TOT")
    private BigDecimal impTotalTot;

    @Column(name = "IND_TIPO")
    private Integer indTipo;

    @Column(name = "ID_ASEGURADO")
    private Integer idAsegurado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ASEGURADO", insertable=false, updatable=false)
    private SwtAsegurado swtAsegurado;

}
