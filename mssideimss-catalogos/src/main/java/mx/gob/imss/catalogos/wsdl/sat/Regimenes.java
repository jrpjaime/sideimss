
package mx.gob.imss.catalogos.wsdl.sat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Regimenes complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Regimenes"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="c_Regimen" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Regimen" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Alta_Reg" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Baja_Reg" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Efec_A_Reg" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Efec_B_Reg" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Regimenes", propOrder = {
    "cRegimen",
    "dRegimen",
    "fAltaReg",
    "fBajaReg",
    "fEfecAReg",
    "fEfecBReg"
})
public class Regimenes {

    @XmlElement(name = "c_Regimen", required = true, nillable = true)
    protected String cRegimen;
    @XmlElement(name = "d_Regimen", required = true, nillable = true)
    protected String dRegimen;
    @XmlElement(name = "f_Alta_Reg", required = true, nillable = true)
    protected String fAltaReg;
    @XmlElement(name = "f_Baja_Reg", required = true, nillable = true)
    protected String fBajaReg;
    @XmlElement(name = "f_Efec_A_Reg", required = true, nillable = true)
    protected String fEfecAReg;
    @XmlElement(name = "f_Efec_B_Reg", required = true, nillable = true)
    protected String fEfecBReg;

    /**
     * Obtiene el valor de la propiedad cRegimen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCRegimen() {
        return cRegimen;
    }

    /**
     * Define el valor de la propiedad cRegimen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCRegimen(String value) {
        this.cRegimen = value;
    }

    /**
     * Obtiene el valor de la propiedad dRegimen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDRegimen() {
        return dRegimen;
    }

    /**
     * Define el valor de la propiedad dRegimen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDRegimen(String value) {
        this.dRegimen = value;
    }

    /**
     * Obtiene el valor de la propiedad fAltaReg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFAltaReg() {
        return fAltaReg;
    }

    /**
     * Define el valor de la propiedad fAltaReg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFAltaReg(String value) {
        this.fAltaReg = value;
    }

    /**
     * Obtiene el valor de la propiedad fBajaReg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFBajaReg() {
        return fBajaReg;
    }

    /**
     * Define el valor de la propiedad fBajaReg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFBajaReg(String value) {
        this.fBajaReg = value;
    }

    /**
     * Obtiene el valor de la propiedad fEfecAReg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFEfecAReg() {
        return fEfecAReg;
    }

    /**
     * Define el valor de la propiedad fEfecAReg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFEfecAReg(String value) {
        this.fEfecAReg = value;
    }

    /**
     * Obtiene el valor de la propiedad fEfecBReg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFEfecBReg() {
        return fEfecBReg;
    }

    /**
     * Define el valor de la propiedad fEfecBReg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFEfecBReg(String value) {
        this.fEfecBReg = value;
    }

}
