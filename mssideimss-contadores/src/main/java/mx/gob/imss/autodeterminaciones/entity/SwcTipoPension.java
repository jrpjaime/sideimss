package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_TIPO_PENSION")
public class SwcTipoPension {

    @Id
    @Column(name = "ID_TIPO_PENSION", nullable = false)
    private Integer idTipoPension;

    @Column(name = "DES_TIPO_PENSION")
    private String desTipoPension;

    @OneToMany(mappedBy = "swcTipoPension", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtAsegurado> swtAsegurados;

    @OneToMany(mappedBy = "swcTipoPension", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
