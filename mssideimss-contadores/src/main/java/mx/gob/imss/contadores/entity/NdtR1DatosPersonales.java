package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_R1_DATOS_PERSONALES")
public class NdtR1DatosPersonales {

    @Id
    @SequenceGenerator(name = "seq_r1", sequenceName = "SEQ_NDTR1DATOSPERSONALES", allocationSize = 1)
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
}