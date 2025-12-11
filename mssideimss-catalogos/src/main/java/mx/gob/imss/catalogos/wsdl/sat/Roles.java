
package mx.gob.imss.catalogos.wsdl.sat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Roles complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Roles"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="c_Rol" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Rol" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Tipo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Alta_Rol" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Baja_Rol" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Roles", propOrder = {
    "cRol",
    "dRol",
    "dTipo",
    "fAltaRol",
    "fBajaRol"
})
public class Roles {

    @XmlElement(name = "c_Rol", required = true, nillable = true)
    protected String cRol;
    @XmlElement(name = "d_Rol", required = true, nillable = true)
    protected String dRol;
    @XmlElement(name = "d_Tipo", required = true, nillable = true)
    protected String dTipo;
    @XmlElement(name = "f_Alta_Rol", required = true, nillable = true)
    protected String fAltaRol;
    @XmlElement(name = "f_Baja_Rol", required = true, nillable = true)
    protected String fBajaRol;

    /**
     * Obtiene el valor de la propiedad cRol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCRol() {
        return cRol;
    }

    /**
     * Define el valor de la propiedad cRol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCRol(String value) {
        this.cRol = value;
    }

    /**
     * Obtiene el valor de la propiedad dRol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRol() {
        return dRol;
    }

    /**
     * Define el valor de la propiedad dRol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRol(String value) {
        this.dRol = value;
    }

    /**
     * Obtiene el valor de la propiedad dTipo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDTipo() {
        return dTipo;
    }

    /**
     * Define el valor de la propiedad dTipo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDTipo(String value) {
        this.dTipo = value;
    }

    /**
     * Obtiene el valor de la propiedad fAltaRol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFAltaRol() {
        return fAltaRol;
    }

    /**
     * Define el valor de la propiedad fAltaRol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFAltaRol(String value) {
        this.fAltaRol = value;
    }

    /**
     * Obtiene el valor de la propiedad fBajaRol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFBajaRol() {
        return fBajaRol;
    }

    /**
     * Define el valor de la propiedad fBajaRol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFBajaRol(String value) {
        this.fBajaRol = value;
    }

}
