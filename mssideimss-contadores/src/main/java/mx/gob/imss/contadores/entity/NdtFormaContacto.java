package mx.gob.imss.contadores.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "NDT_FORMA_CONTACTO", schema = "MGPBDTU9X")
public class NdtFormaContacto {

    @Id
    @SequenceGenerator(name = "seq_fc", sequenceName = "MGPBDTU9X.SEQ_NDTFORMACONTACTO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_fc")
    @Column(name = "CVE_ID_FORMA_CONTACTO")
    private Long cveIdFormaContacto;

    @Column(name = "DES_FORMA_CONTACTO")
    private String desFormaContacto; // <--- AQUÍ SE GUARDA EL NÚMERO TELEFÓNICO

    @Column(name = "CVE_ID_TIPO_CONTACTO")
    private Long cveIdTipoContacto; // 2 = Teléfono Fijo

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDateTime fecRegistroAlta;
    
   
}
