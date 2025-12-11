
package mx.gob.imss.catalogos.wsdl.sat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para Actividades complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Actividades"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Orden" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="Porcentaje" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="c_Actividad" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="d_Actividad" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Alta_Act" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="f_Baja_Act" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Actividades", propOrder = {
    "orden",
    "porcentaje",
    "cActividad",
    "dActividad",
    "fAltaAct",
    "fBajaAct"
})
public class Actividades {

    @XmlElement(name = "Orden", required = true, nillable = true)
    protected String orden;
    @XmlElement(name = "Porcentaje", required = true, nillable = true)
    protected String porcentaje;
    @XmlElement(name = "c_Actividad", required = true, nillable = true)
    protected String cActividad;
    @XmlElement(name = "d_Actividad", required = true, nillable = true)
    protected String dActividad;
    @XmlElement(name = "f_Alta_Act", required = true, nillable = true)
    protected String fAltaAct;
    @XmlElement(name = "f_Baja_Act", required = true, nillable = true)
    protected String fBajaAct;

    /**
     * Obtiene el valor de la propiedad orden.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrden() {
        return orden;
    }

    /**
     * Define el valor de la propiedad orden.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrden(String value) {
        this.orden = value;
    }

    /**
     * Obtiene el valor de la propiedad porcentaje.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPorcentaje() {
        return porcentaje;
    }

    /**
     * Define el valor de la propiedad porcentaje.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPorcentaje(String value) {
        this.porcentaje = value;
    }

    /**
     * Obtiene el valor de la propiedad cActividad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCActividad() {
        return cActividad;
    }

    /**
     * Define el valor de la propiedad cActividad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCActividad(String value) {
        this.cActividad = value;
    }

    /**
     * Obtiene el valor de la propiedad dActividad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDActividad() {
        return dActividad;
    }

    /**
     * Define el valor de la propiedad dActividad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDActividad(String value) {
        this.dActividad = value;
    }

    /**
     * Obtiene el valor de la propiedad fAltaAct.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFAltaAct() {
        return fAltaAct;
    }

    /**
     * Define el valor de la propiedad fAltaAct.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFAltaAct(String value) {
        this.fAltaAct = value;
    }

    /**
     * Obtiene el valor de la propiedad fBajaAct.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFBajaAct() {
        return fBajaAct;
    }

    /**
     * Define el valor de la propiedad fBajaAct.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFBajaAct(String value) {
        this.fBajaAct = value;
    }

}
