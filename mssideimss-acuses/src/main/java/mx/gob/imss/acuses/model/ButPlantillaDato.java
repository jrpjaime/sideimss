package mx.gob.imss.bi.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.Date;
import java.util.List;


/**
 * The persistent class for the but_plantilla_datos database table.
 * 
 */
@Entity
@Table(name="BUT_PLANTILLA_DATOS")
public class ButPlantillaDato implements Serializable {
	private static final long serialVersionUID = 1L;

 
	@Column(name = "CVE_ID_PLANTILLA_DATOS", nullable = false)
	@Basic(fetch = FetchType.EAGER)
	@Id
	@SequenceGenerator(name = "inc_but_plantilla_datos", sequenceName = "SEQ_BUTPLANTILLADATOS", allocationSize = 1)    
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inc_but_plantilla_datos")	
	@XmlElement
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

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "CVE_ID_PLANTILLA_DOCUMENTO", referencedColumnName = "CVE_ID_PLANTILLA_DOCUMENTO") })
	@XmlTransient
	@JsonBackReference
	private ButPlantillaDocumento butPlantillaDocumento;

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "CVE_ID_USUARIO", referencedColumnName = "CVE_ID_USUARIO") })
	@XmlTransient
	@JsonBackReference
	BitUsuario bitUsuario;
	
	
	@OneToMany(mappedBy = "butPlantillaDatoAutorizacion", cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY)
	@XmlElement(name = "", namespace = "")
	@JsonBackReference
 	private List<ButProcTerceAutori> butProcTerceAutorisAutorizacion;

 
	@OneToMany(mappedBy = "butPlantillaDatoBaja", cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY)
	@XmlElement(name = "", namespace = "")
	@JsonBackReference
	private List<ButProcTerceAutori> butProcTerceAutorisBaja;

	public ButPlantillaDato() {
	}



	public String getNomDocumento() {
		return nomDocumento;
	}



	public void setNomDocumento(String nomDocumento) {
		this.nomDocumento = nomDocumento;
	}



	public Long getCveIdPlantillaDatos() {
		return cveIdPlantillaDatos;
	}



	public void setCveIdPlantillaDatos(Long cveIdPlantillaDatos) {
		this.cveIdPlantillaDatos = cveIdPlantillaDatos;
	}



	public String getDesDatos() {
		return this.desDatos;
	}

	public void setDesDatos(String desDatos) {
		this.desDatos = desDatos;
	}

	public Date getFecRegistro() {
		return this.fecRegistro;
	}

	public void setFecRegistro(Date fecRegistro) {
		this.fecRegistro = fecRegistro;
	}

	public ButPlantillaDocumento getButPlantillaDocumento() {
		return this.butPlantillaDocumento;
	}

	public void setButPlantillaDocumento(ButPlantillaDocumento butPlantillaDocumento) {
		this.butPlantillaDocumento = butPlantillaDocumento;
	}

	public BitUsuario getBitUsuario() {
		return bitUsuario;
	}

	public void setBitUsuario(BitUsuario bitUsuario) {
		this.bitUsuario = bitUsuario;
	}

	public List<ButProcTerceAutori> getButProcTerceAutorisAutorizacion() {
		return butProcTerceAutorisAutorizacion;
	}

	public void setButProcTerceAutorisAutorizacion(List<ButProcTerceAutori> butProcTerceAutorisAutorizacion) {
		this.butProcTerceAutorisAutorizacion = butProcTerceAutorisAutorizacion;
	}

	public List<ButProcTerceAutori> getButProcTerceAutorisBaja() {
		return butProcTerceAutorisBaja;
	}

	public void setButProcTerceAutorisBaja(List<ButProcTerceAutori> butProcTerceAutorisBaja) {
		this.butProcTerceAutorisBaja = butProcTerceAutorisBaja;
	}

	public String getDesVersion() {
		return desVersion;
	}

	public void setDesVersion(String desVersion) {
		this.desVersion = desVersion;
	}







}