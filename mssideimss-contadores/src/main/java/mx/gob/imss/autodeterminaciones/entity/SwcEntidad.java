package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_ENTIDAD")
public class SwcEntidad {

    @Id
    @Column(name = "ID_ENTIDAD", nullable = false)
    private Integer idEntidad;

    @Column(name = "CVE_ENTIDAD")
    private String cveEntidad;

    @Column(name = "DES_ENTIDAD")
    private String desEntidad;

    @Column(name = "SIGLAS_ENTIDAD")
    private String siglasEntidad;

    @OneToMany(mappedBy = "swcEntidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwcDelegacion> swcDelegacions;

    @OneToMany(mappedBy = "swcEntidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwcMunicipio> swcMunicipios;

    @OneToMany(mappedBy = "swcEntidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtAsegurado> swtAsegurados;

}
