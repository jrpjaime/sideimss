package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_SECUELA_CONSECUENCIA")
public class SwcSecuelaConsecuencia {

    @Id
    @Column(name = "ID_SECUELA_CONSECUENCIA", nullable = false)
    private Integer idSecuelaConsecuencia;

    @Column(name = "DES_SECUELA_CONSECUENCIA")
    private String desSecuelaConsecuencia;

    @Column(name = "DES_OBSERVACIONES")
    private String desObservaciones;

    @OneToMany(mappedBy = "swcSecuelaConsecuencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtIncapacidad> swtIncapacidads;

    @OneToMany(mappedBy = "swcSecuelaConsecuencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
