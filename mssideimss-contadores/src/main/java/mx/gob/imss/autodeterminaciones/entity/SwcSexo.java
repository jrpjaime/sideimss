package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_SEXO")
public class SwcSexo {

    @Id
    @Column(name = "ID_SEXO", nullable = false)
    private Integer idSexo;

    @Column(name = "DES_SEXO")
    private String desSexo;

    @OneToMany(mappedBy = "swcSexo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtAsegurado> swtAsegurados;

}
