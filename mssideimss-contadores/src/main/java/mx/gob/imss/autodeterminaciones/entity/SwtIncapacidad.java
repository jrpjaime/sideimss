package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.time.LocalDate; 

@Data
@Entity
@Table(name = "SWT_INCAPACIDAD")
public class SwtIncapacidad {

    @Id
    @SequenceGenerator(name = "inc_swt_incapacidad", sequenceName = "SWTS_INCAPACIDAD", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_incapacidad")
    @Column(name = "ID_INCAPACIDAD", nullable = false)
    private Long idIncapacidad;

    @Column(name = "ID_TRABAJADOR_INC")
    private Integer idTrabajadorInc;

    @Column(name = "TIP_INCIDENCIA")
    private Integer tipIncidencia;

    @Column(name = "POR_PORCENTAJE_INCAPACIDAD")
    private Integer porPorcentajeIncapacidad;

    @Column(name = "FEC_INICIO_INCAPACIDAD")
    private LocalDate fecInicioIncapacidad;

    @Column(name = "ID_RAMO_SEGURO")
    private Integer idRamoSeguro;

    @Column(name = "ID_CONTROL_INCAPACIDAD")
    private Integer idControlIncapacidad;

    @Column(name = "ID_TIPO_RIESGO")
    private Integer idTipoRiesgo;

    @Column(name = "ID_SECUELA_CONSECUENCIA")
    private Integer idSecuelaConsecuencia;

    @Column(name = "REF_FOLIO_CERTIFICADO_INC")
    private String refFolioCertificadoInc;

    @Column(name = "FEC_TERMINO")
    private LocalDate fecTermino;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TRABAJADOR_INC", insertable=false, updatable=false)
    private SwtTrabajadorInc swtTrabajadorInc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RAMO_SEGURO", insertable=false, updatable=false)
    private SwcRamoSeguro swcRamoSeguro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTROL_INCAPACIDAD", insertable=false, updatable=false)
    private SwcControlIncapacidad swcControlIncapacidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_RIESGO", insertable=false, updatable=false)
    private SwcTipoRiesgo swcTipoRiesgo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SECUELA_CONSECUENCIA", insertable=false, updatable=false)
    private SwcSecuelaConsecuencia swcSecuelaConsecuencia;

}
