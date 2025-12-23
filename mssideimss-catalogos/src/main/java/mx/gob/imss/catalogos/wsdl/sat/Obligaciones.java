
package mx.gob.imss.catalogos.wsdl.sat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Obligaciones complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Obligaciones"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="c_Obligacion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="cve_pago" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Obligacion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_oblig_lc" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Alta_Oblig" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Baja_Oblig" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Efec_A_Oblig" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Efec_B_Oblig" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_fin_Legal" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_ini_Legal" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="t_Contribucion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Obligaciones", propOrder = {
    "cObligacion",
    "cvePago",
    "dObligacion",
    "dObligLc",
    "fAltaOblig",
    "fBajaOblig",
    "fEfecAOblig",
    "fEfecBOblig",
    "fFinLegal",
    "fIniLegal",
    "tContribucion"
})
public class Obligaciones {

    @XmlElement(name = "c_Obligacion", required = true, nillable = true)
    protected String cObligacion;
    @XmlElement(name = "cve_pago", required = true, nillable = true)
    protected String cvePago;
    @XmlElement(name = "d_Obligacion", required = true, nillable = true)
    protected String dObligacion;
    @XmlElement(name = "d_oblig_lc", required = true, nillable = true)
    protected String dObligLc;
    @XmlElement(name = "f_Alta_Oblig", required = true, nillable = true)
    protected String fAltaOblig;
    @XmlElement(name = "f_Baja_Oblig", required = true, nillable = true)
    protected String fBajaOblig;
    @XmlElement(name = "f_Efec_A_Oblig", required = true, nillable = true)
    protected String fEfecAOblig;
    @XmlElement(name = "f_Efec_B_Oblig", required = true, nillable = true)
    protected String fEfecBOblig;
    @XmlElement(name = "f_fin_Legal", required = true, nillable = true)
    protected String fFinLegal;
    @XmlElement(name = "f_ini_Legal", required = true, nillable = true)
    protected String fIniLegal;
    @XmlElement(name = "t_Contribucion", required = true, nillable = true)
    protected String tContribucion;

    /**
     * Obtiene el valor de la propiedad cObligacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCObligacion() {
        return cObligacion;
    }

    /**
     * Define el valor de la propiedad cObligacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCObligacion(String value) {
        this.cObligacion = value;
    }

    /**
     * Obtiene el valor de la propiedad cvePago.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCvePago() {
        return cvePago;
    }

    /**
     * Define el valor de la propiedad cvePago.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCvePago(String value) {
        this.cvePago = value;
    }

    /**
     * Obtiene el valor de la propiedad dObligacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDObligacion() {
        return dObligacion;
    }

    /**
     * Define el valor de la propiedad dObligacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDObligacion(String value) {
        this.dObligacion = value;
    }

    /**
     * Obtiene el valor de la propiedad dObligLc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDObligLc() {
        return dObligLc;
    }

    /**
     * Define el valor de la propiedad dObligLc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDObligLc(String value) {
        this.dObligLc = value;
    }

    /**
     * Obtiene el valor de la propiedad fAltaOblig.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFAltaOblig() {
        return fAltaOblig;
    }

    /**
     * Define el valor de la propiedad fAltaOblig.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFAltaOblig(String value) {
        this.fAltaOblig = value;
    }

    /**
     * Obtiene el valor de la propiedad fBajaOblig.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFBajaOblig() {
        return fBajaOblig;
    }

    /**
     * Define el valor de la propiedad fBajaOblig.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFBajaOblig(String value) {
        this.fBajaOblig = value;
    }

    /**
     * Obtiene el valor de la propiedad fEfecAOblig.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFEfecAOblig() {
        return fEfecAOblig;
    }

    /**
     * Define el valor de la propiedad fEfecAOblig.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFEfecAOblig(String value) {
        this.fEfecAOblig = value;
    }

    /**
     * Obtiene el valor de la propiedad fEfecBOblig.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFEfecBOblig() {
        return fEfecBOblig;
    }

    /**
     * Define el valor de la propiedad fEfecBOblig.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFEfecBOblig(String value) {
        this.fEfecBOblig = value;
    }

    /**
     * Obtiene el valor de la propiedad fFinLegal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFFinLegal() {
        return fFinLegal;
    }

    /**
     * Define el valor de la propiedad fFinLegal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFFinLegal(String value) {
        this.fFinLegal = value;
    }

    /**
     * Obtiene el valor de la propiedad fIniLegal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFIniLegal() {
        return fIniLegal;
    }

    /**
     * Define el valor de la propiedad fIniLegal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFIniLegal(String value) {
        this.fIniLegal = value;
    }

    /**
     * Obtiene el valor de la propiedad tContribucion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTContribucion() {
        return tContribucion;
    }

    /**
     * Define el valor de la propiedad tContribucion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTContribucion(String value) {
        this.tContribucion = value;
    }

}
