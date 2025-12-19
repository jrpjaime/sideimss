package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_R2_FORMACONTACTO", schema = "MGPBDTU9X")
public class NdtR2FormaContacto {

    @Id
    @SequenceGenerator(name = "seq_r2_fc", sequenceName = "MGPBDTU9X.SEQ_NDTR2FORMACONTACTO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_r2_fc")
    @Column(name = "CVE_ID_R2_FCONTACTO")
    private Long cveIdR2Fcontacto;

    @Column(name = "CVE_ID_R2_DESPACHO")
    private Long cveIdR2Despacho; // Relación con el Despacho

    @Column(name = "CVE_ID_FORMA_CONTACTO")
    private Long cveIdFormaContacto; // Relación con el número de teléfono

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;
    
    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;
     
    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDateTime fecRegistroBaja;
}
