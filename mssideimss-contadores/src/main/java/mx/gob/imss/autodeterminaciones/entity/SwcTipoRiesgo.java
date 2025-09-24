package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_TIPO_RIESGO")
public class SwcTipoRiesgo {

    @Id
    @Column(name = "ID_TIPO_RIESGO", nullable = false)
    private Integer idTipoRiesgo;

    @Column(name = "DES_TIPO_RIESGO")
    private String desTipoRiesgo;

    @OneToMany(mappedBy = "swcTipoRiesgo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtIncapacidad> swtIncapacidads;

    @OneToMany(mappedBy = "swcTipoRiesgo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
