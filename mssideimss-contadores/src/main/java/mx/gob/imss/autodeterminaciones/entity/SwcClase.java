package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_CLASE")
public class SwcClase {

    @Id
    @Column(name = "ID_CLASE", nullable = false)
    private Integer idClase;

    @Column(name = "CVE_CLASE")
    private String cveClase;

    @Column(name = "DES_CLASE")
    private String desClase;

    @OneToMany(mappedBy = "swcClase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwcFraccion> swcFraccions;

}
