 package mx.gob.imss.seguridad.entity;

import java.time.LocalDate;

 
import lombok.Data; 

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
 

@Entity
@Table(name = "DIT_PERSONA")  
@Data
public class DITPersona {
    @Id
    @Column(name = "CVE_ID_PERSONA")
    private Long cveIdPersona;

    @Column(name = "NOM_NOMBRE")
    private String nomNombre;

    @Column(name = "NOM_PRIMER_APELLIDO")
    private String nomPrimerApellido;

    @Column(name = "NOM_SEGUNDO_APELLIDO")
    private String nomSegundoApellido;

    @Column(name = "CURP")
    private String curp;

    @Column(name = "RFC")
    private String rfc;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

 
}