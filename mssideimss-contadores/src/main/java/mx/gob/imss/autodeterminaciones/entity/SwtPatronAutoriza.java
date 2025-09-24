package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.time.LocalDate; 

@Data
@Entity
@Table(name = "SWT_PATRON_AUTORIZA")
public class SwtPatronAutoriza {

    @Id
    @SequenceGenerator(name = "inc_swt_patron_autoriza", sequenceName = "SWTS_PATRON_AUTORIZA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_patron_autoriza")
    @Column(name = "ID_PATRON_AUTORIZA", nullable = false)
    private Long idPatronAutoriza;

    @Column(name = "ID_TERCERO_AUTORIZADO")
    private Integer idTerceroAutorizado;

    @Column(name = "FEC_AUTORIZACION")
    private LocalDate fecAutorizacion;

    @Column(name = "FEC_BAJA")
    private LocalDate fecBaja;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

    @Column(name = "CVE_REGISTRO_PATRONAL")
    private String cveRegistroPatronal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TERCERO_AUTORIZADO", insertable=false, updatable=false)
    private SwtTerceroAutorizado swtTerceroAutorizado;

}
