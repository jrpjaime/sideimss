package mx.gob.imss.acuses.model;

import java.io.Serializable;
  

import jakarta.persistence.*;

import java.util.Date; 
import lombok.Data;


/**
 * The persistent class for  
 * 
 */
@Entity
@Table(name="BUT_PLANTILLA_DATOS")
@Data
public class PlantillaDato implements Serializable {
	private static final long serialVersionUID = 1L;

 
	@Column(name = "CVE_ID_PLANTILLA_DATOS", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@SequenceGenerator(name = "inc_but_plantilla_datos", sequenceName = "SEQ_BUTPLANTILLADATOS", allocationSize = 1)    
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_but_plantilla_datos")	
	 
	private Long cveIdPlantillaDatos;

	@Lob 
	@Column(name="DES_DATOS")
	private String desDatos;
	
	
	@Column(name="DES_VERSION")
	private String desVersion;
	
	@Column(name="NOM_DOCUMENTO")
	private String nomDocumento;

	@Temporal(TemporalType.DATE)
	@Column(name="FEC_REGISTRO")
	private Date fecRegistro;

	
 
 






}