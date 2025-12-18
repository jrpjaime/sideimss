package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_R1_DATOS_PERSONALES", schema = "MGPBDTU9X")
public class NdtR1DatosPersonales {

    @Id
    // Aseguramos el esquema en la secuencia también
    @SequenceGenerator(name = "seq_r1", sequenceName = "MGPBDTU9X.SEQ_NDTR1DATOSPERSONALES", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_r1")
    @Column(name = "CVE_ID_R1_DATOS_PERSONALES")
    private Long cveIdR1DatosPersonales;

    @Column(name = "CVE_ID_CPA")
    private Long cveIdCpa;

    @Column(name = "CEDULA_PROFESIONAL")
    private String cedulaProfesional;

    @Column(name = "DES_TITULO_EXPEDIDO_POR")
    private String desTituloExpedidoPor;

    @Column(name = "CORREO_IMSS")
    private String correoImss;
    
    @Column(name = "TELEFONO_IMSS")
    private String telefonoImss;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;

    // --- CAMPOS FALTANTES AGREGADOS ---

    @Column(name = "CVE_ID_PFDOM_FISCAL")
    private Long cveIdPfdomFiscal;

    @Column(name = "CVE_ID_SUBDELEGACION")
    private Long cveIdSubdelegacion;

    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;

    @Column(name = "CVE_ID_CPA_TRAMITE")
    private Long cveIdCpaTramite;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDateTime fecRegistroBaja;
}