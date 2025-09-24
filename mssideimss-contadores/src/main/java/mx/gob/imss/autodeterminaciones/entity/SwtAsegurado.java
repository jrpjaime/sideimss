package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "SWT_ASEGURADO")
public class SwtAsegurado {

    @Id
    @SequenceGenerator(name = "inc_swt_asegurado", sequenceName = "SWTS_ASEGURADO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_asegurado")
    @Column(name = "ID_ASEGURADO", nullable = false)
    private Long idAsegurado;

    @Column(name = "NUM_NSS")
    private String numNss;

    @Column(name = "REF_CURP")
    private String refCurp;

    @Column(name = "REF_RFC")
    private String refRfc;

    @Column(name = "NOM_NOMBRE")
    private String nomNombre;

    @Column(name = "NOM_PRIMER_APELLIDO")
    private String nomPrimerApellido;

    @Column(name = "NOM_SEGUNDO_APELLIDO")
    private String nomSegundoApellido;

    @Column(name = "FEC_ALT")
    private LocalDate fecAlt;

    @Column(name = "FEC_BAJ")
    private LocalDate fecBaj;

    @Column(name = "CVE_UBCACION")
    private String cveUbcacion;

    @Column(name = "CVE_MUNICIPIO")
    private String cveMunicipio;

    @Column(name = "ID_TIPO_TRABAJADOR")
    private Integer idTipoTrabajador;

    @Column(name = "ID_JORNADA")
    private Integer idJornada;

    @Column(name = "ID_TIPO_PENSION")
    private Integer idTipoPension;

    @Column(name = "ID_TIPO_SALARIO")
    private Integer idTipoSalario;

    @Column(name = "ID_ENTIDAD")
    private Integer idEntidad;

    @Column(name = "ID_SEXO")
    private Integer idSexo;

    @Column(name = "REF_CPP_TRAB")
    private String refCppTrab;

    @Column(name = "FEC_NAC")
    private LocalDate fecNac;

    @Column(name = "NUM_UMF_TRAB")
    private String numUmfTrab;

    @Column(name = "REF_OCUPA")
    private String refOcupa;

    @Column(name = "NUM_JOR_HORAS")
    private String numJorHoras;

    @Column(name = "SALARIO_DIARIO_INTEGRADO")
    private BigDecimal salarioDiarioIntegrado;

    @Column(name = "NUM_CREDITO")
    private String numCredito;

    @Column(name = "FEC_INICIO_DESCUENTO")
    private LocalDate fecInicioDescuento;

    @Column(name = "CAN_VALOR_DESCUENTO")
    private Integer canValorDescuento;

    @Column(name = "FEC_SUSPENSION")
    private LocalDate fecSuspension;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

    @Column(name = "ID_TIPO_DESCUENTO")
    private Integer idTipoDescuento;

    @Column(name = "IND_TABLA_DISMINUCION")
    private Integer indTablaDisminucion;

    @Column(name = "CVE_REGISTRO_PATRONAL")
    private String cveRegistroPatronal;

    @Column(name = "ID_PATRON")
    private Integer idPatron;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_TRABAJADOR", insertable=false, updatable=false)
    private SwcTipoTrabajador swcTipoTrabajador;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_JORNADA", insertable=false, updatable=false)
    private SwcJornada swcJornada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_PENSION", insertable=false, updatable=false)
    private SwcTipoPension swcTipoPension;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SALARIO", insertable=false, updatable=false)
    private SwcTipoSalario swcTipoSalario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTIDAD", insertable=false, updatable=false)
    private SwcEntidad swcEntidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SEXO", insertable=false, updatable=false)
    private SwcSexo swcSexo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_DESCUENTO", insertable=false, updatable=false)
    private SwcTipoDescuento swcTipoDescuento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PATRON", insertable=false, updatable=false)
    private SwtPatron swtPatron;

    @OneToMany(mappedBy = "swtAsegurado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtCredito> swtCreditos;

    @OneToMany(mappedBy = "swtAsegurado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtCuotasCop> swtCuotasCops;

    @OneToMany(mappedBy = "swtAsegurado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtCuotasRcv> swtCuotasRcvs;

    @OneToMany(mappedBy = "swtAsegurado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
