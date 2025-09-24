package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_FRACCION")
public class SwcFraccion {

    @Id
    @Column(name = "ID_FRACCION", nullable = false)
    private Integer idFraccion;

    @Column(name = "CVE_FRACCION")
    private String cveFraccion;

    @Column(name = "DES_FRACCION")
    private String desFraccion;

    @Column(name = "ID_CLASE")
    private Integer idClase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CLASE", insertable=false, updatable=false)
    private SwcClase swcClase;

    @OneToMany(mappedBy = "swcFraccion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPatron> swtPatrons;

}
