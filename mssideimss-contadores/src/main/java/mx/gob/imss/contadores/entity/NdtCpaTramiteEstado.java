package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_CPA_TRAMITE_ESTADO", schema = "MGPBDTU9X")
public class NdtCpaTramiteEstado {

    @Id
    @SequenceGenerator(name = "seq_tramite_estado", sequenceName = "MGPBDTU9X.SEQ_NDTCPATRAMITEESTADO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tramite_estado")
    @Column(name = "CVE_ID_CPA_TRAMITE_ESTADO", nullable = false)
    private Long cveIdCpaTramiteEstado;

    @Column(name = "CVE_ID_CPA_TRAMITE", nullable = false)
    private Long cveIdCpaTramite;

    @Column(name = "CVE_ID_ESTADO_TRAMITE")
    private Long cveIdEstadoTramite;

    @Column(name = "CVE_ID_ESTADO_TRAMITE_PREVIO")
    private Long cveIdEstadoTramitePrevio;

    @Column(name = "CURP_ANALISTA", length = 18)
    private String curpAnalista;

    @Column(name = "OBSERVACIONES", length = 3100)
    private String observaciones;

    @Column(name = "FEC_REGISTRO_ALTA", nullable = false)
    private LocalDateTime fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDateTime fecRegistroBaja;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDateTime fecRegistroActualizado;

    @Column(name = "CVE_ID_USUARIO", length = 20)
    private String cveIdUsuario;
}