
package mx.gob.imss.catalogos.wsdl.sat;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RepLegales complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RepLegales"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Ap_Materno" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Ap_Paterno" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="CURP" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Nombre" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="RFC" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Tipo" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Inicio" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Fin" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RepLegales", propOrder = {
    "apMaterno",
    "apPaterno",
    "curp",
    "nombre",
    "rfc",
    "tipo",
    "fInicio",
    "fFin"
})
public class RepLegales {

    @XmlElement(name = "Ap_Materno", required = true, nillable = true)
    protected String apMaterno;
    @XmlElement(name = "Ap_Paterno", required = true, nillable = true)
    protected String apPaterno;
    @XmlElement(name = "CURP", required = true, nillable = true)
    protected String curp;
    @XmlElement(name = "Nombre", required = true, nillable = true)
    protected String nombre;
    @XmlElement(name = "RFC", required = true, nillable = true)
    protected String rfc;
    @XmlElement(name = "Tipo", required = true, nillable = true)
    protected String tipo;
    @XmlElement(name = "f_Inicio", required = true, nillable = true)
    protected String fInicio;
    @XmlElement(name = "f_Fin", required = true, nillable = true)
    protected String fFin;

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
     * Obtiene el valor de la propiedad rfc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRFC() {
        return rfc;
    }

    /**
     * Define el valor de la propiedad rfc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRFC(String value) {
        this.rfc = value;
    }

    /**
     * Obtiene el valor de la propiedad tipo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipo() {
        return tipo;
    }

    /**
     * Define el valor de la propiedad tipo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipo(String value) {
        this.tipo = value;
    }

    /**
     * Obtiene el valor de la propiedad fInicio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFInicio() {
        return fInicio;
    }

    /**
     * Define el valor de la propiedad fInicio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFInicio(String value) {
        this.fInicio = value;
    }

    /**
     * Obtiene el valor de la propiedad fFin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFFin() {
        return fFin;
    }

    /**
     * Define el valor de la propiedad fFin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFFin(String value) {
        this.fFin = value;
    }

}
