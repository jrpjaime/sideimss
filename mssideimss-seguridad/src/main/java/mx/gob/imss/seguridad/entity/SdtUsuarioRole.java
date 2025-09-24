package mx.gob.imss.seguridad.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import mx.gob.imss.seguridad.entity.SdtUsuarioRole;
 
@Data
@Entity
@Table(name = "SWT_USUARIO_ROLE")
public class  SdtUsuarioRole implements Serializable { 
 
	private static final long serialVersionUID = -3716676870L;


	@Column(name = "ID_USUARIO_ROLE", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	Integer idUsuarioRole;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO") })
	SdtUsuario sdtUsuario;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "ID_ROLE", referencedColumnName = "ID_ROLE") })
	SdcRole sdcRole;
}
