package mx.gob.imss.seguridad.entity;
 
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.io.Serializable; 
import java.time.LocalDateTime;

import lombok.Data;
 

 
@Data
@Entity
@Table(name = "SWT_USUARIO")
public class  SdtUsuario implements Serializable { 

	private static final long serialVersionUID = 1L;


	@Column(name = "ID_USUARIO", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer idUsuario;


	@OneToMany(mappedBy = "sdtUsuario", cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY)
	@JsonBackReference
	java.util.List<SdtUsuarioRole> sdtUsuarioRoles;


	@Column(name = "NOM_NOMBRE")
	@Basic(fetch = FetchType.EAGER)
	String nomNombre;


	@Column(name = "NOM_APELLIDO_PATERNO")
	@Basic(fetch = FetchType.EAGER)
	String nomApellidoPaterno;


	@Column(name = "NOM_APELLIDO_MATERNO")
	@Basic(fetch = FetchType.EAGER)
	String nomApellidoMaterno;


	@Column(name = "DES_USUARIO")
	@Basic(fetch = FetchType.EAGER)
	String desUsuario;


	@Column(name = "DES_PASSWORD")
	@Basic(fetch = FetchType.EAGER)
	String desPassword;

	@Column(name = "REF_RFC")
	@Basic(fetch = FetchType.EAGER)
	String refRfc;
/*
	//CAMBIOS_ORACLE
	@Column(name = "CVE_ID_DELEGACION")
	//@Column(name = "CVE_DELEGACION")
	@Basic(fetch = FetchType.EAGER)
	String cveDelegacion;


 

	//CAMBIOS_ORACLE
	@Column(name = "CVE_ID_SUBDELEGACION")
	//@Column(name = "CVE_SUBDELEGACION")
	@Basic(fetch = FetchType.EAGER)
	String cveSubdelegacion;

 
  
*/
}
