package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_DELEGACION")
public class SwcDelegacion {

    @Id
    @Column(name = "ID_DELEGACION", nullable = false)
    private Integer idDelegacion;

    @Column(name = "ID_ENTIDAD")
    private Integer idEntidad;

    @Column(name = "CVE_DELEGACION")
    private String cveDelegacion;

    @Column(name = "DES_DELEGACION")
    private String desDelegacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTIDAD", insertable=false, updatable=false)
    private SwcEntidad swcEntidad;

    @OneToMany(mappedBy = "swcDelegacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwcSubdelegacion> swcSubdelegacions;

}
