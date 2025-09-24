package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_SUBDELEGACION")
public class SwcSubdelegacion {

    @Id
    @Column(name = "ID_SUBDELEGACION", nullable = false)
    private Integer idSubdelegacion;

    @Column(name = "CVE_SUBDELEGACION")
    private String cveSubdelegacion;

    @Column(name = "DES_SUBDELEGACION")
    private String desSubdelegacion;

    @Column(name = "ID_DELEGACION")
    private Integer idDelegacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DELEGACION", insertable=false, updatable=false)
    private SwcDelegacion swcDelegacion;

    @OneToMany(mappedBy = "swcSubdelegacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPatron> swtPatrons;

}
