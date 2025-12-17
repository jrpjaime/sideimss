package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_CPA_ESTATUS")
public class NdtCpaEstatus {

    @Id
    @SequenceGenerator(name = "seq_estatus", sequenceName = "SEQ_NDTCPAESTATUS", allocationSize = 1)
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
}