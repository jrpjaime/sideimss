package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate; 

@Data
@Entity
@Table(name = "SWT_PRIMA_RT")
public class SwtPrimaRt {

    @Id
    @SequenceGenerator(name = "inc_swt_prima_rt", sequenceName = "SWTS_PRIMA_RT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_prima_rt")
    @Column(name = "ID_PRIMA_RT", nullable = false)
    private Long idPrimaRt;

    @Column(name = "POR_PRIMA_RT")
    private BigDecimal porPrimaRt;

    @Column(name = "ID_PATRON")
    private Integer idPatron;

    @Column(name = "ID_CICLO")
    private Integer idCiclo;

    @Column(name = "ID_MES")
    private Integer idMes;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PATRON", insertable=false, updatable=false)
    private SwtPatron swtPatron;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CICLO", insertable=false, updatable=false)
    private SwcCiclo swcCiclo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MES", insertable=false, updatable=false)
    private SwcMes swcMes;

}
