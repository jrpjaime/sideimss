package mx.gob.imss.catalogos.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "NDT_FORMA_CONTACTO")  
public class NdtFormaContacto {

    @Id
    @Column(name = "CVE_ID_FORMA_CONTACTO")
    private Long cveIdFormaContacto;

    @Column(name = "CVE_ID_TIPO_CONTACTO")
    private String cveIdTipoContacto;  

    @Column(name = "DES_FORMA_CONTACTO")
    private String desFormaContacto;

}