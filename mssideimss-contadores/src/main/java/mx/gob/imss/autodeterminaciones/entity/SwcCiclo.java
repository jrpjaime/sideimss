package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_CICLO")
public class SwcCiclo {

    @Id
    @Column(name = "ID_CICLO", nullable = false)
    private Integer idCiclo;

    @Column(name = "DES_CICLO")
    private Integer desCiclo;

    @OneToMany(mappedBy = "swcCiclo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPatron> swtPatrons;

    @OneToMany(mappedBy = "swcCiclo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPrimaRt> swtPrimaRts;

}
