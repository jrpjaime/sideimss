
package mx.gob.imss.catalogos.wsdl.sat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Identificacion complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Identificacion"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Ap_Materno" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ap_Paterno" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURP" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="NIT" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Nacionalidad" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Nom_Comercial" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Nombre" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Pais_Origen" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Razon_Soc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Sit_Cont" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Sit_Cont_Dom" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Sit_Dom" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_det_Sit_Cont" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Sit_Cont" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Sit_Cont_Dom" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Sit_Dom" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Constitucion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Ini_Opers" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Nacimiento" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Sit_Cont" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="t_Sociedad" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="t_persona" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Segmento" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Segmento" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DF_Tipo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DF_Folio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DF_F_Inicio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="DF_F_Fin" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Identificacion", propOrder = {
    "apMaterno",
    "apPaterno",
    "curp",
    "nit",
    "nacionalidad",
    "nomComercial",
    "nombre",
    "paisOrigen",
    "razonSoc",
    "cSitCont",
    "cSitContDom",
    "cSitDom",
    "cDetSitCont",
    "dSitCont",
    "dSitContDom",
    "dSitDom",
    "fConstitucion",
    "fIniOpers",
    "fNacimiento",
    "fSitCont",
    "tSociedad",
    "tPersona",
    "cSegmento",
    "dSegmento",
    "dfTipo",
    "dfFolio",
    "dffInicio",
    "dffFin",
    "email"
})
public class Identificacion {

    @XmlElement(name = "Ap_Materno", required = true, nillable = true)
    protected String apMaterno;
    @XmlElement(name = "Ap_Paterno", required = true, nillable = true)
    protected String apPaterno;
    @XmlElement(name = "CURP", required = true, nillable = true)
    protected String curp;
    @XmlElement(name = "NIT", required = true, nillable = true)
    protected String nit;
    @XmlElement(name = "Nacionalidad", required = true, nillable = true)
    protected String nacionalidad;
    @XmlElement(name = "Nom_Comercial", required = true, nillable = true)
    protected String nomComercial;
    @XmlElement(name = "Nombre", required = true, nillable = true)
    protected String nombre;
    @XmlElement(name = "Pais_Origen", required = true, nillable = true)
    protected String paisOrigen;
    @XmlElement(name = "Razon_Soc", required = true, nillable = true)
    protected String razonSoc;
    @XmlElement(name = "c_Sit_Cont", required = true, nillable = true)
    protected String cSitCont;
    @XmlElement(name = "c_Sit_Cont_Dom", required = true, nillable = true)
    protected String cSitContDom;
    @XmlElement(name = "c_Sit_Dom", required = true, nillable = true)
    protected String cSitDom;
    @XmlElement(name = "c_det_Sit_Cont", required = true, nillable = true)
    protected String cDetSitCont;
    @XmlElement(name = "d_Sit_Cont", required = true, nillable = true)
    protected String dSitCont;
    @XmlElement(name = "d_Sit_Cont_Dom", required = true, nillable = true)
    protected String dSitContDom;
    @XmlElement(name = "d_Sit_Dom", required = true, nillable = true)
    protected String dSitDom;
    @XmlElement(name = "f_Constitucion", required = true, nillable = true)
    protected String fConstitucion;
    @XmlElement(name = "f_Ini_Opers", required = true, nillable = true)
    protected String fIniOpers;
    @XmlElement(name = "f_Nacimiento", required = true, nillable = true)
    protected String fNacimiento;
    @XmlElement(name = "f_Sit_Cont", required = true, nillable = true)
    protected String fSitCont;
    @XmlElement(name = "t_Sociedad", required = true, nillable = true)
    protected String tSociedad;
    @XmlElement(name = "t_persona", required = true, nillable = true)
    protected String tPersona;
    @XmlElement(name = "c_Segmento", required = true, nillable = true)
    protected String cSegmento;
    @XmlElement(name = "d_Segmento", required = true, nillable = true)
    protected String dSegmento;
    @XmlElement(name = "DF_Tipo", required = true, nillable = true)
    protected String dfTipo;
    @XmlElement(name = "DF_Folio", required = true, nillable = true)
    protected String dfFolio;
    @XmlElement(name = "DF_F_Inicio", required = true, nillable = true)
    protected String dffInicio;
    @XmlElement(name = "DF_F_Fin", required = true, nillable = true)
    protected String dffFin;
    @XmlElement(required = true, nillable = true)
    protected String email;

    /**
     * Obtiene el valor de la propiedad apMaterno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApMaterno() {
        return apMaterno;
    }

    /**
     * Define el valor de la propiedad apMaterno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApMaterno(String value) {
        this.apMaterno = value;
    }

    /**
     * Obtiene el valor de la propiedad apPaterno.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApPaterno() {
        return apPaterno;
    }

    /**
     * Define el valor de la propiedad apPaterno.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApPaterno(String value) {
        this.apPaterno = value;
    }

    /**
     * Obtiene el valor de la propiedad curp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCURP() {
        return curp;
    }

    /**
     * Define el valor de la propiedad curp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCURP(String value) {
        this.curp = value;
    }

    /**
     * Obtiene el valor de la propiedad nit.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNIT() {
        return nit;
    }

    /**
     * Define el valor de la propiedad nit.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNIT(String value) {
        this.nit = value;
    }

    /**
     * Obtiene el valor de la propiedad nacionalidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNacionalidad() {
        return nacionalidad;
    }

    /**
     * Define el valor de la propiedad nacionalidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNacionalidad(String value) {
        this.nacionalidad = value;
    }

    /**
     * Obtiene el valor de la propiedad nomComercial.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNomComercial() {
        return nomComercial;
    }

    /**
     * Define el valor de la propiedad nomComercial.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNomComercial(String value) {
        this.nomComercial = value;
    }

    /**
     * Obtiene el valor de la propiedad nombre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Define el valor de la propiedad nombre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNombre(String value) {
        this.nombre = value;
    }

    /**
     * Obtiene el valor de la propiedad paisOrigen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaisOrigen() {
        return paisOrigen;
    }

    /**
     * Define el valor de la propiedad paisOrigen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaisOrigen(String value) {
        this.paisOrigen = value;
    }

    /**
     * Obtiene el valor de la propiedad razonSoc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRazonSoc() {
        return razonSoc;
    }

    /**
     * Define el valor de la propiedad razonSoc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRazonSoc(String value) {
        this.razonSoc = value;
    }

    /**
     * Obtiene el valor de la propiedad cSitCont.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSitCont() {
        return cSitCont;
    }

    /**
     * Define el valor de la propiedad cSitCont.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSitCont(String value) {
        this.cSitCont = value;
    }

    /**
     * Obtiene el valor de la propiedad cSitContDom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSitContDom() {
        return cSitContDom;
    }

    /**
     * Define el valor de la propiedad cSitContDom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSitContDom(String value) {
        this.cSitContDom = value;
    }

    /**
     * Obtiene el valor de la propiedad cSitDom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSitDom() {
        return cSitDom;
    }

    /**
     * Define el valor de la propiedad cSitDom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSitDom(String value) {
        this.cSitDom = value;
    }

    /**
     * Obtiene el valor de la propiedad cDetSitCont.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCDetSitCont() {
        return cDetSitCont;
    }

    /**
     * Define el valor de la propiedad cDetSitCont.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCDetSitCont(String value) {
        this.cDetSitCont = value;
    }

    /**
     * Obtiene el valor de la propiedad dSitCont.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDSitCont() {
        return dSitCont;
    }

    /**
     * Define el valor de la propiedad dSitCont.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDSitCont(String value) {
        this.dSitCont = value;
    }

    /**
     * Obtiene el valor de la propiedad dSitContDom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDSitContDom() {
        return dSitContDom;
    }

    /**
     * Define el valor de la propiedad dSitContDom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDSitContDom(String value) {
        this.dSitContDom = value;
    }

    /**
     * Obtiene el valor de la propiedad dSitDom.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDSitDom() {
        return dSitDom;
    }

    /**
     * Define el valor de la propiedad dSitDom.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDSitDom(String value) {
        this.dSitDom = value;
    }

    /**
     * Obtiene el valor de la propiedad fConstitucion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFConstitucion() {
        return fConstitucion;
    }

    /**
     * Define el valor de la propiedad fConstitucion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFConstitucion(String value) {
        this.fConstitucion = value;
    }

    /**
     * Obtiene el valor de la propiedad fIniOpers.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFIniOpers() {
        return fIniOpers;
    }

    /**
     * Define el valor de la propiedad fIniOpers.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFIniOpers(String value) {
        this.fIniOpers = value;
    }

    /**
     * Obtiene el valor de la propiedad fNacimiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFNacimiento() {
        return fNacimiento;
    }

    /**
     * Define el valor de la propiedad fNacimiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFNacimiento(String value) {
        this.fNacimiento = value;
    }

    /**
     * Obtiene el valor de la propiedad fSitCont.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFSitCont() {
        return fSitCont;
    }

    /**
     * Define el valor de la propiedad fSitCont.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFSitCont(String value) {
        this.fSitCont = value;
    }

    /**
     * Obtiene el valor de la propiedad tSociedad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTSociedad() {
        return tSociedad;
    }

    /**
     * Define el valor de la propiedad tSociedad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTSociedad(String value) {
        this.tSociedad = value;
    }

    /**
     * Obtiene el valor de la propiedad tPersona.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTPersona() {
        return tPersona;
    }

    /**
     * Define el valor de la propiedad tPersona.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTPersona(String value) {
        this.tPersona = value;
    }

    /**
     * Obtiene el valor de la propiedad cSegmento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSegmento() {
        return cSegmento;
    }

    /**
     * Define el valor de la propiedad cSegmento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSegmento(String value) {
        this.cSegmento = value;
    }

    /**
     * Obtiene el valor de la propiedad dSegmento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDSegmento() {
        return dSegmento;
    }

    /**
     * Define el valor de la propiedad dSegmento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDSegmento(String value) {
        this.dSegmento = value;
    }

    /**
     * Obtiene el valor de la propiedad dfTipo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFTipo() {
        return dfTipo;
    }

    /**
     * Define el valor de la propiedad dfTipo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFTipo(String value) {
        this.dfTipo = value;
    }

    /**
     * Obtiene el valor de la propiedad dfFolio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFFolio() {
        return dfFolio;
    }

    /**
     * Define el valor de la propiedad dfFolio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFFolio(String value) {
        this.dfFolio = value;
    }

    /**
     * Obtiene el valor de la propiedad dffInicio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFFInicio() {
        return dffInicio;
    }

    /**
     * Define el valor de la propiedad dffInicio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFFInicio(String value) {
        this.dffInicio = value;
    }

    /**
     * Obtiene el valor de la propiedad dffFin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDFFFin() {
        return dffFin;
    }

    /**
     * Define el valor de la propiedad dffFin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDFFFin(String value) {
        this.dffFin = value;
    }

    /**
     * Obtiene el valor de la propiedad email.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define el valor de la propiedad email.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmail(String value) {
        this.email = value;
    }

}
