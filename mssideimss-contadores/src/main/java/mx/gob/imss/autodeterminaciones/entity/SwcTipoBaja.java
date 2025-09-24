package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_TIPO_BAJA")
public class SwcTipoBaja {

    @Id
    @Column(name = "ID_TIPO_BAJA", nullable = false)
    private Integer idTipoBaja;

    @Column(name = "DES_TIPO_BAJA")
    private String desTipoBaja;

    @OneToMany(mappedBy = "swcTipoBaja", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
