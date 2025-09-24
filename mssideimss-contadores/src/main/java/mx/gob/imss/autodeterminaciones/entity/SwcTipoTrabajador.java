package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_TIPO_TRABAJADOR")
public class SwcTipoTrabajador {

    @Id
    @Column(name = "ID_TIPO_TRABAJADOR", nullable = false)
    private Integer idTipoTrabajador;

    @Column(name = "DES_TIPO_TRABAJADOR")
    private String desTipoTrabajador;

    @OneToMany(mappedBy = "swcTipoTrabajador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtAsegurado> swtAsegurados;

    @OneToMany(mappedBy = "swcTipoTrabajador", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
