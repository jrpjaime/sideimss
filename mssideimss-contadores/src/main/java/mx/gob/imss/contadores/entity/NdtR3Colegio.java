package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_R3_COLEGIO", schema = "MGPBDTU9X")
public class NdtR3Colegio {

    @Id
    @SequenceGenerator(name = "seq_r3", sequenceName = "MGPBDTU9X.SEQ_NDTR3COLEGIO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_r3")
    // CORRECCIÓN: Nombre exacto con el guion bajo al final
    @Column(name = "CVE_ID_R3_COLEGIO_XCONTADOR_") 
    private Long cveIdR3Colegio;

    @Column(name = "CVE_ID_CPA")
    private Long cveIdCpa;
    
    @Column(name = "CVE_ID_COLEGIO")
    private Long cveIdColegio;

    @Column(name = "CVE_ID_CPA_TRAMITE")
    private Long cveIdCpaTramite;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;
    
    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDateTime fecRegistroBaja;

    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;
 
    @Column(name = "CVE_ID_PMDOM_FISCAL")
    private Long cveIdPmdomFiscal;

    @Column(name = "CVE_ID_DOCTO_PROBATORIO")
    private Long cveIdDoctoProbatorio;

    @Column(name = "NUM_TRAMITE_NOTARIA")
    private String numTramiteNotaria;

    @Column(name = "URL_ACUSE_NOTARIA")
    private String urlAcuseNotaria;
}