package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_TIPO_SALARIO")
public class SwcTipoSalario {

    @Id
    @Column(name = "ID_TIPO_SALARIO", nullable = false)
    private Integer idTipoSalario;

    @Column(name = "DES_TIPO_SALARIO")
    private String desTipoSalario;

    @OneToMany(mappedBy = "swcTipoSalario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtAsegurado> swtAsegurados;

}
