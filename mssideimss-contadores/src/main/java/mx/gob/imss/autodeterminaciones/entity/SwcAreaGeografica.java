package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_AREA_GEOGRAFICA")
public class SwcAreaGeografica {

    @Id
    @Column(name = "ID_AREA_GEOGRAFICA", nullable = false)
    private Integer idAreaGeografica;

    @Column(name = "CVE_AREA_GEOGRAFICA")
    private String cveAreaGeografica;

    @Column(name = "DES_AREA_GEOGRAFICA")
    private String desAreaGeografica;

    @OneToMany(mappedBy = "swcAreaGeografica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPatron> swtPatrons;

}
