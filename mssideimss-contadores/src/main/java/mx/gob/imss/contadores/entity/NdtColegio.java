package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_COLEGIO", schema = "MGPBDTU9X")
public class NdtColegio {
    @Id
    @Column(name = "CVE_ID_COLEGIO")
    private Long cveIdColegio;

    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;
    
    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDateTime fecRegistroBaja;
}