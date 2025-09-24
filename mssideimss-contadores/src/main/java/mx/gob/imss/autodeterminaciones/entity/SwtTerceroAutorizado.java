package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@Table(name = "SWT_TERCERO_AUTORIZADO")
public class SwtTerceroAutorizado {

    @Id
    @SequenceGenerator(name = "inc_swt_tercero_autorizado", sequenceName = "SWTS_TERCERO_AUTORIZADO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_swt_tercero_autorizado")
    @Column(name = "ID_TERCERO_AUTORIZADO", nullable = false)
    private Long idTerceroAutorizado;

    @Column(name = "ID_USUARIO")
    private Integer idUsuario;

    @Column(name = "ID_USUARIO_TERC_AUT")
    private Integer idUsuarioTercAut;

    @Column(name = "REF_FOL_AUTORIZACION")
    private String refFolAutorizacion;

    @Column(name = "FEC_AUTORIZACION")
    private LocalDate fecAutorizacion;

    @Column(name = "REF_FOL_BAJA")
    private String refFolBaja;

    @Column(name = "FEC_BAJA")
    private LocalDate fecBaja;

    @Column(name = "FEC_REGISTRO_ALTA")
    private LocalDate fecRegistroAlta;

    @Column(name = "FEC_REGISTRO_ACTUALIZADO")
    private LocalDate fecRegistroActualizado;

    @Column(name = "FEC_REGISTRO_BAJA")
    private LocalDate fecRegistroBaja;

 

    @OneToMany(mappedBy = "swtTerceroAutorizado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPatronAutoriza> swtPatronAutorizas;

}
