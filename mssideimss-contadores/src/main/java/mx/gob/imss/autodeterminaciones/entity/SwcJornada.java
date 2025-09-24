package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_JORNADA")
public class SwcJornada {

    @Id
    @Column(name = "ID_JORNADA", nullable = false)
    private Integer idJornada;

    @Column(name = "DES_JORNADA")
    private String desJornada;

    @OneToMany(mappedBy = "swcJornada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtAsegurado> swtAsegurados;

    @OneToMany(mappedBy = "swcJornada", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
