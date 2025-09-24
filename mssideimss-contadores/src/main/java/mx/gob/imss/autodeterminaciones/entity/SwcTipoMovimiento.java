package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_TIPO_MOVIMIENTO")
public class SwcTipoMovimiento {

    @Id
    @Column(name = "ID_TIPO_MOVIMIENTO", nullable = false)
    private Integer idTipoMovimiento;

    @Column(name = "DES_TIPO_MOVIMIENTO")
    private String desTipoMovimiento;

    @Column(name = "CVE_TIPO_MOVIMIENTO")
    private String cveTipoMovimiento;

    @OneToMany(mappedBy = "swcTipoMovimiento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
