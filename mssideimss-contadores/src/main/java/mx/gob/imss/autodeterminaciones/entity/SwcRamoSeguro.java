package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_RAMO_SEGURO")
public class SwcRamoSeguro {

    @Id
    @Column(name = "ID_RAMO_SEGURO", nullable = false)
    private Integer idRamoSeguro;

    @Column(name = "DES_RAMO_SEGURO")
    private String desRamoSeguro;

    @OneToMany(mappedBy = "swcRamoSeguro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwcControlIncapacidad> swcControlIncapacidads;

    @OneToMany(mappedBy = "swcRamoSeguro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtIncapacidad> swtIncapacidads;

    @OneToMany(mappedBy = "swcRamoSeguro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtMovimiento> swtMovimientos;

}
