package mx.gob.imss.autodeterminaciones.entity;

import jakarta.persistence.*;
import lombok.Data; 
import java.util.List;

@Data
@Entity
@Table(name = "SWC_MES")
public class SwcMes {

    @Id
    @Column(name = "ID_MES", nullable = false)
    private Integer idMes;

    @Column(name = "DES_MES")
    private String desMes;

    @Column(name = "CVE_MES")
    private String cveMes;

    @OneToMany(mappedBy = "swcMes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPatron> swtPatrons;

    @OneToMany(mappedBy = "swcMes", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SwtPrimaRt> swtPrimaRts;

}
