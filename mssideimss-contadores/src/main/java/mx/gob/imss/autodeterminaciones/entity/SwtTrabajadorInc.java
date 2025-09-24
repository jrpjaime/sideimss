package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWT_TRABAJADOR_INC")
public class SwtTrabajadorInc {

    @Column(name = "CVE_REGISTRO_PATRONAL")
    private String cveRegistroPatronal;

    @Column(name = "NUM_NSS")
    private String numNss;

    @Id
    @SequenceGenerator(name = "inc_swt_trabajador_inc", sequenceName = "SWTS_TRABAJADOR_INC", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_trabajador_inc")
    @Column(name = "ID_TRABAJADOR_INC", nullable = false)
    private Long idTrabajadorInc;

    @OneToMany(mappedBy = "swtTrabajadorInc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtIncapacidad> swtIncapacidads;

}
