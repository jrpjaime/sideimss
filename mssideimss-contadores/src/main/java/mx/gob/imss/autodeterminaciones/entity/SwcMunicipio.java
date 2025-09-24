package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_MUNICIPIO")
public class SwcMunicipio {

    @Id
    @Column(name = "ID_MUNICIPIO", nullable = false)
    private Integer idMunicipio;

    @Column(name = "DES_MUNICIPIO")
    private String desMunicipio;

    @Column(name = "ID_ENTIDAD")
    private Integer idEntidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTIDAD", insertable=false, updatable=false)
    private SwcEntidad swcEntidad;

    @OneToMany(mappedBy = "swcMunicipio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPatron> swtPatrons;

}
