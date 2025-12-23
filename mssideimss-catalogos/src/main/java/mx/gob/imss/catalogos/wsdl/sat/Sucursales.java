
package mx.gob.imss.catalogos.wsdl.sat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Sucursales complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Sucursales"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Calle" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Pais_Residencia" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Telefono1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Telefono2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_ALR" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Colonia" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Ent_Fed" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Localidad" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Municipio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Sit_Cont_Suc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Sit_Suc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_CRH" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cp" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_ALR" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Colonia" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Ent_Fed" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_EntreCalle1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_EntreCalle2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Inmueble" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Localidad" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Municipio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Referencia" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Sit_Cont_Suc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Sit_Suc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Vialidad" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_CRH" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="email" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Alta_Suc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="n_Establecimiento" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="n_Exterior" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="n_Interior" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="t_Inmueble" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="t_Tel1" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="t_Tel2" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="t_Vialidad" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="caract_domicilio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Sucursales", propOrder = {
    "calle",
    "paisResidencia",
    "telefono1",
    "telefono2",
    "calr",
    "cColonia",
    "cEntFed",
    "cLocalidad",
    "cMunicipio",
    "cSitContSuc",
    "cSitSuc",
    "ccrh",
    "cp",
    "dalr",
    "dColonia",
    "dEntFed",
    "dEntreCalle1",
    "dEntreCalle2",
    "dInmueble",
    "dLocalidad",
    "dMunicipio",
    "dReferencia",
    "dSitContSuc",
    "dSitSuc",
    "dVialidad",
    "dcrh",
    "email",
    "fAltaSuc",
    "nEstablecimiento",
    "nExterior",
    "nInterior",
    "tInmueble",
    "tTel1",
    "tTel2",
    "tVialidad",
    "caractDomicilio"
})
public class Sucursales {

    @XmlElement(name = "Calle", required = true, nillable = true)
    protected String calle;
    @XmlElement(name = "Pais_Residencia", required = true, nillable = true)
    protected String paisResidencia;
    @XmlElement(name = "Telefono1", required = true, nillable = true)
    protected String telefono1;
    @XmlElement(name = "Telefono2", required = true, nillable = true)
    protected String telefono2;
    @XmlElement(name = "c_ALR", required = true, nillable = true)
    protected String calr;
    @XmlElement(name = "c_Colonia", required = true, nillable = true)
    protected String cColonia;
    @XmlElement(name = "c_Ent_Fed", required = true, nillable = true)
    protected String cEntFed;
    @XmlElement(name = "c_Localidad", required = true, nillable = true)
    protected String cLocalidad;
    @XmlElement(name = "c_Municipio", required = true, nillable = true)
    protected String cMunicipio;
    @XmlElement(name = "c_Sit_Cont_Suc", required = true, nillable = true)
    protected String cSitContSuc;
    @XmlElement(name = "c_Sit_Suc", required = true, nillable = true)
    protected String cSitSuc;
    @XmlElement(name = "c_CRH", required = true, nillable = true)
    protected String ccrh;
    @XmlElement(required = true, nillable = true)
    protected String cp;
    @XmlElement(name = "d_ALR", required = true, nillable = true)
    protected String dalr;
    @XmlElement(name = "d_Colonia", required = true, nillable = true)
    protected String dColonia;
    @XmlElement(name = "d_Ent_Fed", required = true, nillable = true)
    protected String dEntFed;
    @XmlElement(name = "d_EntreCalle1", required = true, nillable = true)
    protected String dEntreCalle1;
    @XmlElement(name = "d_EntreCalle2", required = true, nillable = true)
    protected String dEntreCalle2;
    @XmlElement(name = "d_Inmueble", required = true, nillable = true)
    protected String dInmueble;
    @XmlElement(name = "d_Localidad", required = true, nillable = true)
    protected String dLocalidad;
    @XmlElement(name = "d_Municipio", required = true, nillable = true)
    protected String dMunicipio;
    @XmlElement(name = "d_Referencia", required = true, nillable = true)
    protected String dReferencia;
    @XmlElement(name = "d_Sit_Cont_Suc", required = true, nillable = true)
    protected String dSitContSuc;
    @XmlElement(name = "d_Sit_Suc", required = true, nillable = true)
    protected String dSitSuc;
    @XmlElement(name = "d_Vialidad", required = true, nillable = true)
    protected String dVialidad;
    @XmlElement(name = "d_CRH", required = true, nillable = true)
    protected String dcrh;
    @XmlElement(required = true, nillable = true)
    protected String email;
    @XmlElement(name = "f_Alta_Suc", required = true, nillable = true)
    protected String fAltaSuc;
    @XmlElement(name = "n_Establecimiento", required = true, nillable = true)
    protected String nEstablecimiento;
    @XmlElement(name = "n_Exterior", required = true, nillable = true)
    protected String nExterior;
    @XmlElement(name = "n_Interior", required = true, nillable = true)
    protected String nInterior;
    @XmlElement(name = "t_Inmueble", required = true, nillable = true)
    protected String tInmueble;
    @XmlElement(name = "t_Tel1", required = true, nillable = true)
    protected String tTel1;
    @XmlElement(name = "t_Tel2", required = true, nillable = true)
    protected String tTel2;
    @XmlElement(name = "t_Vialidad", required = true, nillable = true)
    protected String tVialidad;
    @XmlElement(name = "caract_domicilio", required = true, nillable = true)
    protected String caractDomicilio;

    /**
     * Obtiene el valor de la propiedad calle.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCalle() {
        return calle;
    }

    /**
     * Define el valor de la propiedad calle.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCalle(String value) {
        this.calle = value;
    }

    /**
     * Obtiene el valor de la propiedad paisResidencia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaisResidencia() {
        return paisResidencia;
    }

    /**
     * Define el valor de la propiedad paisResidencia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaisResidencia(String value) {
        this.paisResidencia = value;
    }

    /**
     * Obtiene el valor de la propiedad telefono1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefono1() {
        return telefono1;
    }

    /**
     * Define el valor de la propiedad telefono1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefono1(String value) {
        this.telefono1 = value;
    }

    /**
     * Obtiene el valor de la propiedad telefono2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefono2() {
        return telefono2;
    }

    /**
     * Define el valor de la propiedad telefono2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefono2(String value) {
        this.telefono2 = value;
    }

    /**
     * Obtiene el valor de la propiedad calr.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCALR() {
        return calr;
    }

    /**
     * Define el valor de la propiedad calr.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCALR(String value) {
        this.calr = value;
    }

    /**
     * Obtiene el valor de la propiedad cColonia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCColonia() {
        return cColonia;
    }

    /**
     * Define el valor de la propiedad cColonia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCColonia(String value) {
        this.cColonia = value;
    }

    /**
     * Obtiene el valor de la propiedad cEntFed.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCEntFed() {
        return cEntFed;
    }

    /**
     * Define el valor de la propiedad cEntFed.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCEntFed(String value) {
        this.cEntFed = value;
    }

    /**
     * Obtiene el valor de la propiedad cLocalidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCLocalidad() {
        return cLocalidad;
    }

    /**
     * Define el valor de la propiedad cLocalidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCLocalidad(String value) {
        this.cLocalidad = value;
    }

    /**
     * Obtiene el valor de la propiedad cMunicipio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCMunicipio() {
        return cMunicipio;
    }

    /**
     * Define el valor de la propiedad cMunicipio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCMunicipio(String value) {
        this.cMunicipio = value;
    }

    /**
     * Obtiene el valor de la propiedad cSitContSuc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSitContSuc() {
        return cSitContSuc;
    }

    /**
     * Define el valor de la propiedad cSitContSuc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSitContSuc(String value) {
        this.cSitContSuc = value;
    }

    /**
     * Obtiene el valor de la propiedad cSitSuc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCSitSuc() {
        return cSitSuc;
    }

    /**
     * Define el valor de la propiedad cSitSuc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCSitSuc(String value) {
        this.cSitSuc = value;
    }

    /**
     * Obtiene el valor de la propiedad ccrh.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCCRH() {
        return ccrh;
    }

    /**
     * Define el valor de la propiedad ccrh.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCCRH(String value) {
        this.ccrh = value;
    }

    /**
     * Obtiene el valor de la propiedad cp.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCp() {
        return cp;
    }

    /**
     * Define el valor de la propiedad cp.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCp(String value) {
        this.cp = value;
    }

    /**
     * Obtiene el valor de la propiedad dalr.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDALR() {
        return dalr;
    }

    /**
     * Define el valor de la propiedad dalr.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDALR(String value) {
        this.dalr = value;
    }

    /**
     * Obtiene el valor de la propiedad dColonia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDColonia() {
        return dColonia;
    }

    /**
     * Define el valor de la propiedad dColonia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDColonia(String value) {
        this.dColonia = value;
    }

    /**
     * Obtiene el valor de la propiedad dEntFed.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEntFed() {
        return dEntFed;
    }

    /**
     * Define el valor de la propiedad dEntFed.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEntFed(String value) {
        this.dEntFed = value;
    }

    /**
     * Obtiene el valor de la propiedad dEntreCalle1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEntreCalle1() {
        return dEntreCalle1;
    }

    /**
     * Define el valor de la propiedad dEntreCalle1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEntreCalle1(String value) {
        this.dEntreCalle1 = value;
    }

    /**
     * Obtiene el valor de la propiedad dEntreCalle2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDEntreCalle2() {
        return dEntreCalle2;
    }

    /**
     * Define el valor de la propiedad dEntreCalle2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDEntreCalle2(String value) {
        this.dEntreCalle2 = value;
    }

    /**
     * Obtiene el valor de la propiedad dInmueble.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDInmueble() {
        return dInmueble;
    }

    /**
     * Define el valor de la propiedad dInmueble.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDInmueble(String value) {
        this.dInmueble = value;
    }

    /**
     * Obtiene el valor de la propiedad dLocalidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDLocalidad() {
        return dLocalidad;
    }

    /**
     * Define el valor de la propiedad dLocalidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDLocalidad(String value) {
        this.dLocalidad = value;
    }

    /**
     * Obtiene el valor de la propiedad dMunicipio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDMunicipio() {
        return dMunicipio;
    }

    /**
     * Define el valor de la propiedad dMunicipio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDMunicipio(String value) {
        this.dMunicipio = value;
    }

    /**
     * Obtiene el valor de la propiedad dReferencia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDReferencia() {
        return dReferencia;
    }

    /**
     * Define el valor de la propiedad dReferencia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDReferencia(String value) {
        this.dReferencia = value;
    }

    /**
     * Obtiene el valor de la propiedad dSitContSuc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDSitContSuc() {
        return dSitContSuc;
    }

    /**
     * Define el valor de la propiedad dSitContSuc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDSitContSuc(String value) {
        this.dSitContSuc = value;
    }

    /**
     * Obtiene el valor de la propiedad dSitSuc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDSitSuc() {
        return dSitSuc;
    }

    /**
     * Define el valor de la propiedad dSitSuc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDSitSuc(String value) {
        this.dSitSuc = value;
    }

    /**
     * Obtiene el valor de la propiedad dVialidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDVialidad() {
        return dVialidad;
    }

    /**
     * Define el valor de la propiedad dVialidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDVialidad(String value) {
        this.dVialidad = value;
    }

    /**
     * Obtiene el valor de la propiedad dcrh.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCRH() {
        return dcrh;
    }

    /**
     * Define el valor de la propiedad dcrh.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCRH(String value) {
        this.dcrh = value;
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

    /**
     * Obtiene el valor de la propiedad fAltaSuc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFAltaSuc() {
        return fAltaSuc;
    }

    /**
     * Define el valor de la propiedad fAltaSuc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFAltaSuc(String value) {
        this.fAltaSuc = value;
    }

    /**
     * Obtiene el valor de la propiedad nEstablecimiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNEstablecimiento() {
        return nEstablecimiento;
    }

    /**
     * Define el valor de la propiedad nEstablecimiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNEstablecimiento(String value) {
        this.nEstablecimiento = value;
    }

    /**
     * Obtiene el valor de la propiedad nExterior.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNExterior() {
        return nExterior;
    }

    /**
     * Define el valor de la propiedad nExterior.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNExterior(String value) {
        this.nExterior = value;
    }

    /**
     * Obtiene el valor de la propiedad nInterior.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNInterior() {
        return nInterior;
    }

    /**
     * Define el valor de la propiedad nInterior.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNInterior(String value) {
        this.nInterior = value;
    }

    /**
     * Obtiene el valor de la propiedad tInmueble.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTInmueble() {
        return tInmueble;
    }

    /**
     * Define el valor de la propiedad tInmueble.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTInmueble(String value) {
        this.tInmueble = value;
    }

    /**
     * Obtiene el valor de la propiedad tTel1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTTel1() {
        return tTel1;
    }

    /**
     * Define el valor de la propiedad tTel1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTTel1(String value) {
        this.tTel1 = value;
    }

    /**
     * Obtiene el valor de la propiedad tTel2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTTel2() {
        return tTel2;
    }

    /**
     * Define el valor de la propiedad tTel2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTTel2(String value) {
        this.tTel2 = value;
    }

    /**
     * Obtiene el valor de la propiedad tVialidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTVialidad() {
        return tVialidad;
    }

    /**
     * Define el valor de la propiedad tVialidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTVialidad(String value) {
        this.tVialidad = value;
    }

    /**
     * Obtiene el valor de la propiedad caractDomicilio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCaractDomicilio() {
        return caractDomicilio;
    }

    /**
     * Define el valor de la propiedad caractDomicilio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCaractDomicilio(String value) {
        this.caractDomicilio = value;
    }

}
