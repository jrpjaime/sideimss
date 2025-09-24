package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.time.LocalDate;

@Data
@Entity
@Table(name = "DIC_SUBDELEGACION")
public class DicSubdelegacion {

    @Column(name = "DES_SUBDELEGACION")
    private String desSubdelegacion;

    @Column(name = "ANIO_INI_OPER")
    private String anioIniOper;

    @Column(name = "CLAVE_SUBDELEGACION")
    private String claveSubdelegacion;

    @Id
    @Column(name = "CVE_ID_SUBDELEGACION", nullable = false)
    private Integer cveIdSubdelegacion;

    @Column(name = "CVE_ID_DELEGACION")
    private Integer cveIdDelegacion;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "DOMICILIO_ID")
    private Long domicilioId;

 
}
