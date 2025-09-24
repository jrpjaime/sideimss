package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_CONTROL_INCAPACIDAD")
public class SwcControlIncapacidad {

    @Id
    @Column(name = "ID_CONTROL_INCAPACIDAD", nullable = false)
    private Integer idControlIncapacidad;

    @Column(name = "DES_CONTROL_INCAPACIDAD")
    private String desControlIncapacidad;

    @Column(name = "DES_OBSERVACIONES")
    private String desObservaciones;

    @Column(name = "ID_RAMO_SEGURO")
    private Integer idRamoSeguro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RAMO_SEGURO", insertable=false, updatable=false)
    private SwcRamoSeguro swcRamoSeguro;

    @OneToMany(mappedBy = "swcControlIncapacidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtIncapacidad> swtIncapacidads;

    @OneToMany(mappedBy = "swcControlIncapacidad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
