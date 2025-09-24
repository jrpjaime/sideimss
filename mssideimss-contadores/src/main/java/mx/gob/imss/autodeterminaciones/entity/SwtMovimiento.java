package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate; 

@Data
@Entity
@Table(name = "SWT_MOVIMIENTO")
public class SwtMovimiento {

    @Id
    @SequenceGenerator(name = "inc_swt_movimiento", sequenceName = "SWTS_MOVIMIENTO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_movimiento")
    @Column(name = "ID_MOVIMIENTO", nullable = false)
    private Long idMovimiento;

    @Column(name = "ID_ASEGURADO")
    private Integer idAsegurado;

    @Column(name = "ID_TIPO_MOVIMIENTO")
    private Integer idTipoMovimiento;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

    @Column(name = "FEC_INICIO")
    private LocalDate fecInicio;

    @Column(name = "NUM_DIAS")
    private Integer numDias;

    @Column(name = "IND_ART_33")
    private Integer indArt33;

    @Column(name = "SAL_SALARIO_DIARIO_INTEGRADO")
    private BigDecimal salSalarioDiarioIntegrado;

    @Column(name = "ID_TIPO_BAJA")
    private Integer idTipoBaja;

    @Column(name = "ID_TIPO_DESCUENTO")
    private Integer idTipoDescuento;

    @Column(name = "REF_FOLIO")
    private String refFolio;

    @Column(name = "NUM_CREDITO")
    private String numCredito;

    @Column(name = "CAN_VALOR_DESCUENTO")
    private Integer canValorDescuento;

    @Column(name = "IND_TABLA_DISMIN_PORCEN")
    private Integer indTablaDisminPorcen;

    @Column(name = "TIP_INCIDENCIA")
    private Integer tipIncidencia;

    @Column(name = "POR_PORCENTAJE_INCAPACIDAD")
    private Integer porPorcentajeIncapacidad;

    @Column(name = "FEC_INICIO_DESCUENTO")
    private LocalDate fecInicioDescuento;

    @Column(name = "CVE_MUNICIPIO")
    private String cveMunicipio;

    @Column(name = "FEC_TERMINO")
    private LocalDate fecTermino;

    @Column(name = "SAL_SALARIO_IV_CV")
    private BigDecimal salSalarioIvCv;

    @Column(name = "SAL_SALARIO_OTROS_SEGUROS")
    private BigDecimal salSalarioOtrosSeguros;

    @Column(name = "ID_RAMO_SEGURO")
    private Integer idRamoSeguro;

    @Column(name = "ID_TIPO_RIESGO")
    private Integer idTipoRiesgo;

    @Column(name = "ID_SECUELA_CONSECUENCIA")
    private Integer idSecuelaConsecuencia;

    @Column(name = "ID_CONTROL_INCAPACIDAD")
    private Integer idControlIncapacidad;

    @Column(name = "FEC_INICIO_INCAPACIDAD")
    private LocalDate fecInicioIncapacidad;

    @Column(name = "REF_FOLIO_CERTIFICADO_INC")
    private String refFolioCertificadoInc;

    @Column(name = "FEC_SUSPENSION")
    private LocalDate fecSuspension;

    @Column(name = "ID_TIPO_TRABAJADOR")
    private Integer idTipoTrabajador;

    @Column(name = "ID_JORNADA")
    private Integer idJornada;

    @Column(name = "ID_TIPO_PENSION")
    private Integer idTipoPension;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ASEGURADO", insertable=false, updatable=false)
    private SwtAsegurado swtAsegurado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_MOVIMIENTO", insertable=false, updatable=false)
    private SwcTipoMovimiento swcTipoMovimiento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_BAJA", insertable=false, updatable=false)
    private SwcTipoBaja swcTipoBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_DESCUENTO", insertable=false, updatable=false)
    private SwcTipoDescuento swcTipoDescuento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RAMO_SEGURO", insertable=false, updatable=false)
    private SwcRamoSeguro swcRamoSeguro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_RIESGO", insertable=false, updatable=false)
    private SwcTipoRiesgo swcTipoRiesgo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SECUELA_CONSECUENCIA", insertable=false, updatable=false)
    private SwcSecuelaConsecuencia swcSecuelaConsecuencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTROL_INCAPACIDAD", insertable=false, updatable=false)
    private SwcControlIncapacidad swcControlIncapacidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_TRABAJADOR", insertable=false, updatable=false)
    private SwcTipoTrabajador swcTipoTrabajador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_JORNADA", insertable=false, updatable=false)
    private SwcJornada swcJornada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_PENSION", insertable=false, updatable=false)
    private SwcTipoPension swcTipoPension;

}
