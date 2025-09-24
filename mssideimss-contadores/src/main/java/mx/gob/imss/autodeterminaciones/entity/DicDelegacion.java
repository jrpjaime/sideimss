package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "DIC_DELEGACION")
public class DicDelegacion {

    @Column(name = "DES_DELEG")
    private String desDeleg;

    @Column(name = "ANIO_INI_OPER")
    private String anioIniOper;

    @Column(name = "CLAVE_DELEGACION")
    private String claveDelegacion;

    @Id
    @Column(name = "CVE_ID_DELEGACION", nullable = false)
    private Integer cveIdDelegacion;

    @Column(name = "TIP_DELEGACION")
    private Integer tipDelegacion;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "DOMICILIO_ID")
    private Long domicilioId;

    @Column(name = "CVE_CIZ")
    private Integer cveCiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CVE_ID_DELEGACION", insertable=false, updatable=false)
    private DicDelegacion dicDelegacion;

    @OneToMany(mappedBy = "dicDelegacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DicDelegacion> dicDelegacions;

 

}
