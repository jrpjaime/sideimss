package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "SWT_PATRON")
public class SwtPatron {

    @Id
    @SequenceGenerator(name = "inc_swt_patron", sequenceName = "SWTS_PATRON", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_patron")
    @Column(name = "ID_PATRON", nullable = false)
    private Long idPatron;

    @Column(name = "REF_RFC")
    private String refRfc;

    @Column(name = "NOM_RAZON_SOCIAL")
    private String nomRazonSocial;

    @Column(name = "REF_ACTIVIDAD_ECONOMICA")
    private String refActividadEconomica;

    @Column(name = "REF_DOMICILIO")
    private String refDomicilio;

    @Column(name = "REF_CODIGO_POSTAL")
    private String refCodigoPostal;

    @Column(name = "REF_TELEFONO")
    private String refTelefono;

    @Column(name = "IND_REEMBOLSO")
    private Integer indReembolso;

    @Column(name = "NOM_REPRESENTANTE_LEGAL")
    private String nomRepresentanteLegal;

    @Column(name = "IND_STYPS")
    private Integer indStyps;

    @Column(name = "ID_SUBDELEGACION")
    private Integer idSubdelegacion;

    @Column(name = "ID_FRACCION")
    private Integer idFraccion;

    @Column(name = "ID_AREA_GEOGRAFICA")
    private Integer idAreaGeografica;

    @Column(name = "ID_MUNICIPIO")
    private Integer idMunicipio;

    @Column(name = "ID_MES")
    private Integer idMes;

    @Column(name = "ID_CICLO")
    private Integer idCiclo;

    @Column(name = "CVE_REGISTRO_PATRONAL")
    private String cveRegistroPatronal;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @Column(name = "NOM_NOMBRE")
    private String nomNombre;

    @Column(name = "NOM_PRIMER_APELLIDO")
    private String nomPrimerApellido;

    @Column(name = "NOM_SEGUNDO_APELLIDO")
    private String nomSegundoApellido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SUBDELEGACION", insertable=false, updatable=false)
    private SwcSubdelegacion swcSubdelegacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FRACCION", insertable=false, updatable=false)
    private SwcFraccion swcFraccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AREA_GEOGRAFICA", insertable=false, updatable=false)
    private SwcAreaGeografica swcAreaGeografica;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MUNICIPIO", insertable=false, updatable=false)
    private SwcMunicipio swcMunicipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MES", insertable=false, updatable=false)
    private SwcMes swcMes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CICLO", insertable=false, updatable=false)
    private SwcCiclo swcCiclo;

 

    @OneToMany(mappedBy = "swtPatron", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtAporInfonavit> swtAporInfonavits;

    @OneToMany(mappedBy = "swtPatron", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtAsegurado> swtAsegurados;

    @OneToMany(mappedBy = "swtPatron", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPrimaRt> swtPrimaRts;

}
