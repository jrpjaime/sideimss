package mx.gob.imss.seguridad.entity;

  
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import mx.gob.imss.seguridad.entity.SdcRole;

 
@Data
@Entity
@Table(name = "SWC_ROLE")
public class  SdcRole implements Serializable { 

	private static final long serialVersionUID = 1L;


	@Column(name = "ID_ROLE", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	Integer idRole;


	@OneToMany(mappedBy = "sdcRole", cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY) 
	@JsonBackReference
	java.util.List<SdtUsuarioRole> sdtUsuarioRoles;


	@Column(name = "DES_ROLE")
	@Basic(fetch = FetchType.EAGER) 
	String desRole;
}
