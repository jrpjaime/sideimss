package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_CPA_ACREDITACION", schema = "MGPBDTU9X") 
public class NdtCpaAcreditacion {

    @Id
    @SequenceGenerator(name = "seq_acred", sequenceName = "MGPBDTU9X.SEQ_NDTCPAACREDITACION", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_acred")
    @Column(name = "CVE_ID_ACREDITACION")
    private Long cveIdAcreditacion;

    @Column(name = "CVE_ID_CPA")
    private Long cveIdCpa;

    @Column(name = "CVE_ID_COLEGIO")
    private Long cveIdColegio;

    @Column(name = "IND_ACRED_MEMBRESIA")
    private Integer indAcredMembresia;

    @Column(name = "FEC_ACREDITACION_CP")
    private LocalDateTime fecAcreditacionCp;  

    @Column(name = "FEC_PRESENTACION_ACREDITACION")
    private LocalDateTime fecPresentacionAcreditacion;  
     
    @Column(name = "FEC_DOCUMENTO1") 
    private LocalDateTime fecDocumento1; 
 
    @Column(name = "FEC_DOCUMENTO2") 
    private LocalDateTime fecDocumento2; 

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDateTime fecRegistroActualizado;
    
     
    @Column(name = "CVE_ID_USUARIO")
    private String cveIdUsuario;
}
