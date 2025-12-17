package mx.gob.imss.contadores.entity;

import java.io.Serializable;
import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data; 

@Data
@Entity
@Table(name = "NDT_PATRON_DICTAMEN" , schema = "MGPBDTU9X")
public class NdtPatronDictamen implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "CVE_ID_PATRON_DICTAMEN", unique = true, nullable = false) 
    private Long cveIdPatronDictamen;

    @Column(name = "DES_RFC", length = 13)
    private String desRfc;

    @Column(name = "DES_NOMBRE_RAZON_SOCIAL", length = 255)
    private String desNombreRazonSocial;

    @Column(name = "FEC_REGISTRO_ALTA") 
    private Date fecRegistroAlta;
 
    
    @Column(name = "CVE_ID_EJER_FISCAL")
    private Long cveIdEjerFiscal;

    @Column(name = "CVE_ID_ESTADO_DICTAMEN")
    private Long cveIdEstadoDictamen;
 

    public NdtPatronDictamen() {
    }
    
}
