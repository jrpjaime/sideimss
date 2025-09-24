package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_TIPO_DESCUENTO")
public class SwcTipoDescuento {

    @Id
    @Column(name = "ID_TIPO_DESCUENTO", nullable = false)
    private Integer idTipoDescuento;

    @Column(name = "DES_TIPO_DESCUENTO")
    private String desTipoDescuento;

    @OneToMany(mappedBy = "swcTipoDescuento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtAsegurado> swtAsegurados;

    @OneToMany(mappedBy = "swcTipoDescuento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtCredito> swtCreditos;

    @OneToMany(mappedBy = "swcTipoDescuento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
