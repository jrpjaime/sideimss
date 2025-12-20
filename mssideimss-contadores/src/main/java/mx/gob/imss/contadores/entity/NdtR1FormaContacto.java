package mx.gob.imss.contadores.entity;


import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "NDT_R1_FORMACONTACTO", schema = "MGPBDTU9X")
public class NdtR1FormaContacto {

    @Id
    @SequenceGenerator(name = "seq_r1_forma_contacto", sequenceName = "MGPBDTU9X.SEQ_NDTR1FORMACONTACTO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_r1_forma_contacto")
    @Column(name = "CVE_ID_R1_FORMACONTACTO")
    private Long cveIdR1FormaContacto;

    @Column(name = "CVE_ID_R1_DATOS_PERSONALES")
    private Long cveIdR1DatosPersonales;

    @Column(name = "CVE_ID_PERSONAF_CONTACTO")
    private Long cveIdPersonafContacto;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDateTime fecRegistroBaja;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDateTime fecRegistroActualizado;

    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;

    @Column(name = "CVE_ID_FORMA_CONTACTO")
    private Long cveIdFormaContacto;
}