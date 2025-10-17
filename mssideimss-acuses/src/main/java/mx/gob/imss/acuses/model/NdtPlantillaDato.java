package mx.gob.imss.acuses.model;


import java.time.LocalDate;

import jakarta.persistence.*; 
import lombok.Data; 

@Data
@Entity
@Table(name = "NDT_PLANTILLA_DATO")
public class NdtPlantillaDato {

    @Id
    @SequenceGenerator(name = "inc_ndt_plantilla_dato", sequenceName = "NDTS_PLANTILLA_DATO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_ndt_plantilla_dato")
    @Column(name = "CVE_ID_PALNTILLA_DATO", nullable = false)
    private Long cveIdPlantillaDato;

    @Column(name = "NOM_DOCUMENTO")
    private String nomDocumento;

    @Column(name = "DES_RFC")
    private String desRfc;

    @Lob
    @Column(name = "DES_DATOS")
    private String desDatos;

    @Column(name = "DES_TIPO_ACUSE")
    private String desTipoAcuse;

    @Column(name = "DES_PATH_VERSION" )
    private String desPathVersion;
    

    @Column(name = "FEC_REGISTRO")
    private LocalDate fecRegistro;
}
