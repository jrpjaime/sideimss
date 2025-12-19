package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_CPA_TRAMITE", schema = "MGPBDTU9X")
public class NdtCpaTramite {

    @Id
    @SequenceGenerator(name = "seq_tramite", sequenceName = "MGPBDTU9X.SEQ_NDTCPATRAMITE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tramite")
    @Column(name = "CVE_ID_CPA_TRAMITE")
    private Long cveIdCpaTramite;

    @Column(name = "CVE_ID_CPA")
    private Long cveIdCpa;

   
    @Column(name = "CVE_ID_TRAMITE") 
    private Long cveIdTramite; 

    @Column(name = "FEC_SOLICITUD_MOVIMIENTO")
    private LocalDateTime fecSolicitudMovimiento;

    @Column(name = "NUM_TRAMITE_NOTARIA")
    private String numTramiteNotaria; // Aquí guardaremos el Folio de Solicitud (UUID)

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;
    
    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;

    @Column(name = "URL_ACUSE_NOTARIA")
    private String urlAcuseNotaria;
}