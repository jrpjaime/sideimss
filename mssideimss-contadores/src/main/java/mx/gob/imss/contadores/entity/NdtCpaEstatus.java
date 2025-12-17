package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_CPA_ESTATUS", schema = "MGPBDTU9X")
public class NdtCpaEstatus {

    @Id
    @SequenceGenerator(name = "seq_estatus", sequenceName = "MGPBDTU9X.SEQ_NDTCPAESTATUS", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_estatus")
    @Column(name = "CVE_ID_ESTATUS_CPA")
    private Long cveIdEstatusCpa;

    @Column(name = "CVE_ID_CPA")
    private Long cveIdCpa;

    @Column(name = "CVE_ID_ESTADO_CPA")
    private Long cveIdEstadoCpa;

    @Column(name = "FEC_BAJA")
    private LocalDateTime fecBaja;

    @Column(name = "DES_COMENTARIOS")
    private String desComentarios;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;
    
    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDateTime fecRegistroActualizado;
    
    // Agregamos usuario para auditoría
    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;
    
    // Opcional: Si en el futuro necesitas ligarlo a un trámite
    @Column(name = "CVE_ID_CPA_TRAMITE")
    private Long cveIdCpaTramite;
}