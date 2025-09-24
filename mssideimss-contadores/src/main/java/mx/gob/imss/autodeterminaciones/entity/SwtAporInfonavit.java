package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate; 

@Data
@Entity
@Table(name = "SWT_APOR_INFONAVIT")
public class SwtAporInfonavit {

    @Id
    @SequenceGenerator(name = "inc_swt_apor_infonavit", sequenceName = "SWTS_APOR_INFONAVIT", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_apor_infonavit")
    @Column(name = "ID_APOR_INFONAVIT", nullable = false)
    private Long idAporInfonavit;

    @Column(name = "ID_PATRON")
    private Integer idPatron;

    @Column(name = "FEC_NOTIFICAMUL")
    private LocalDate fecNotificamul;

    @Column(name = "IMP_MULTA")
    private BigDecimal impMulta;

    @Column(name = "IMP_FUNDEMEX")
    private BigDecimal impFundemex;

    @Column(name = "IND_MAYOR25")
    private Integer indMayor25;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PATRON", insertable=false, updatable=false)
    private SwtPatron swtPatron;

}
