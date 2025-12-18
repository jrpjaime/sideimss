package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_R2_DESPACHO", schema = "MGPBDTU9X")
public class NdtR2Despacho {

    @Id
    @SequenceGenerator(name = "seq_r2", sequenceName = "MGPBDTU9X.SEQ_NDTR2DESPACHO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_r2")
    @Column(name = "CVE_ID_R2_DESPACHO")
    private Long cveIdR2Despacho;

    @Column(name = "CVE_ID_CPA")
    private Long cveIdCpa;

    @Column(name = "CVE_ID_CPA_TRAMITE")
    private Long cveIdCpaTramite;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;
    
    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;

    // --- CAMPOS NECESARIOS PARA EL FLUJO ---
    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDateTime fecRegistroBaja;

    @Column(name = "CVE_ID_DESPACHO")
    private Long cveIdDespacho;

    @Column(name = "CVE_ID_PMDOM_FISCAL")
    private Long cveIdPmdomFiscal;

    @Column(name = "CVE_ID_PFDOM_FISCAL")
    private Long cveIdPfdomFiscal;

    @Column(name = "IND_TIPO_CPA") 
    private Long indTipoCpa; 

    @Column(name = "CARGO_QUE_DESEMPENA")
    private String cargoQueDesempena;

    @Column(name = "TELEFONO_FIJO")
    private String telefonoFijo;
    
    @Column(name = "NUM_TRABAJADORES_CONTRATADOS")
    private Integer numTrabajadores;

    @Column(name = "IND_CUENTACON_TRAB")
    private String indCuentaconTrab; // En tus inserts vi que guarda "1 " o strings
}
