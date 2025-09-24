package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.time.LocalDate; 

@Data
@Entity
@Table(name = "SWT_CREDITO")
public class SwtCredito {

    @Id
    @SequenceGenerator(name = "inc_swt_credito", sequenceName = "SWTS_CREDITO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_credito")
    @Column(name = "ID_CREDITO", nullable = false)
    private Long idCredito;

    @Column(name = "ID_ASEGURADO")
    private Integer idAsegurado;

    @Column(name = "NUM_CREDITO")
    private String numCredito;

    @Column(name = "FEC_SUSPENSION")
    private LocalDate fecSuspension;

    @Column(name = "FEC_INICIO_DESCUENTO")
    private LocalDate fecInicioDescuento;

    @Column(name = "CAN_VALOR_DESCUENTO")
    private Integer canValorDescuento;

    @Column(name = "CVE_MUNICIPIO")
    private String cveMunicipio;

    @Column(name = "ID_TIPO_DESCUENTO")
    private Integer idTipoDescuento;

    @Column(name = "IND_TABLA_DISMINUCION")
    private Integer indTablaDisminucion;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ASEGURADO", insertable=false, updatable=false)
    private SwtAsegurado swtAsegurado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_DESCUENTO", insertable=false, updatable=false)
    private SwcTipoDescuento swcTipoDescuento;

}
