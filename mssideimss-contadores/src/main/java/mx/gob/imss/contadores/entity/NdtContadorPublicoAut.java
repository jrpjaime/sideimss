package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_CONTADOR_PUBLICO_AUT")
public class NdtContadorPublicoAut {

    @Id
    @Column(name = "CVE_ID_CPA")
    private Long cveIdCpa; // No generamos ID aquí, solo consultamos o actualizamos

    @Column(name = "NUM_REGISTRO_CPA")
    private Integer numRegistroCpa;

    @Column(name = "CURP")
    private String curp;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDateTime fecRegistroBaja;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDateTime fecRegistroActualizado;

    @Column(name = "CVE_ID_ESTADO_CPA")
    private Long cveIdEstadoCpa;

    @Column(name = "CVE_ID_PERSONA")
    private Long cveIdPersona;
}