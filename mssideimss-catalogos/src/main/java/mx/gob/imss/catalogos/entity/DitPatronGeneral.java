package mx.gob.imss.catalogos.entity;
 
import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;
import mx.gob.imss.catalogos.entity.DitPatronGeneral;

 
@Data
@Entity
@Table(name = "DIT_PATRON_GENERAL")
public class  DitPatronGeneral implements Serializable { 

	private static final long serialVersionUID = 1L;


	@Column(name = "ID_PATRON_GENERAL", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long idPatronGeneral;



	@Column(name = "DENOMINACION_RAZON_SOCIAL")
	@Basic(fetch = FetchType.EAGER)

	String denominacionRazonSocial;


	@Column(name = "RFC")
	@Basic(fetch = FetchType.EAGER)

	String rfc;


	@Column(name = "REGISTRO_PATRONAL")
	@Basic(fetch = FetchType.EAGER)
	String registroPatronal;


	@Column(name = "CVE_DELEGACION")
	@Basic(fetch = FetchType.EAGER)
	String cveDelegacion;


	@Column(name = "DES_DELEGACION")
	@Basic(fetch = FetchType.EAGER)
	String desDelegacion;


	@Column(name = "CVE_SUBDELEGACION")
	@Basic(fetch = FetchType.EAGER)
	String cveSubdelegacion;


	@Column(name = "DES_SUBDELEGACION")
	@Basic(fetch = FetchType.EAGER)
	String desSubdelegacion;
}
