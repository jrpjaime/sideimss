package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_R1_DATOS_PERSONALES", schema = "MGPBDTU9X")
public class NdtR1DatosPersonales {

    @Id
    @SequenceGenerator(name = "seq_r1", sequenceName = "MGPBDTU9X.SEQ_NDTR1DATOSPERSONALES", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_r1")
    @Column(name = "CVE_ID_R1_DATOS_PERSONALES")
    private Long cveIdR1DatosPersonales;

    @Column(name = "CVE_ID_CPA")
    private Long cveIdCpa;

    @Column(name = "CVE_ID_SUBDELEGACION")
    private Long cveIdSubdelegacion;

    @Column(name = "CEDULA_PROFESIONAL")
    private String cedulaProfesional;

    @Column(name = "DES_TITULO_EXPEDIDO_POR")
    private String desTituloExpedidoPor;

    // --- EL CAMPO QUE CAUSABA EL ERROR ---
    @Column(name = "FEC_EXPEDICION_CEDPROF")
    private LocalDateTime fecExpedicionCedprof;

    @Column(name = "CVE_ID_PFDOM_FISCAL")
    private Long cveIdPfdomFiscal;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDateTime fecRegistroBaja;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDateTime fecRegistroActualizado;

    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;

    @Column(name = "CVE_ID_CPA_TRAMITE")
    private Long cveIdCpaTramite;

    @Column(name = "TELEFONO_IMSS")
    private String telefonoImss;

    @Column(name = "CORREO_IMSS")
    private String correoImss;

    // --- CAMPOS DE RELACIÓN CON NDT_FORMA_CONTACTO ---
    @Column(name = "CVE_ID_TELEFONO_IMSS")
    private Long cveIdTelefonoImss;

    @Column(name = "CVE_ID_CORREO_IMSS")
    private Long cveIdCorreoImss;

    @Column(name = "DES_OBSERVACIONES")
    private String desObservaciones;
}