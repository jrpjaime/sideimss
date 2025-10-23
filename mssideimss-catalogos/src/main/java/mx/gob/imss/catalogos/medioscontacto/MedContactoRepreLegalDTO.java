
package mx.gob.imss.catalogos.medioscontacto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para MedContactoRepreLegalDTO complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="MedContactoRepreLegalDTO">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TipoContacto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DesFormaContacto" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Rfc" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MedContactoRepreLegalDTO", namespace = "java:mx.gob.imss.distss.pensiones.bdtu.modelo", propOrder = {
    "tipoContacto",
    "desFormaContacto",
    "rfc"
})
public class MedContactoRepreLegalDTO {

    @XmlElement(name = "TipoContacto", required = true, nillable = true)
    protected String tipoContacto;
    @XmlElement(name = "DesFormaContacto", required = true, nillable = true)
    protected String desFormaContacto;
    @XmlElement(name = "Rfc", required = true, nillable = true)
    protected String rfc;

    /**
     * Obtiene el valor de la propiedad tipoContacto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTipoContacto() {
        return tipoContacto;
    }

    /**
     * Define el valor de la propiedad tipoContacto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTipoContacto(String value) {
        this.tipoContacto = value;
    }

    /**
     * Obtiene el valor de la propiedad desFormaContacto.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesFormaContacto() {
        return desFormaContacto;
    }

    /**
     * Define el valor de la propiedad desFormaContacto.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesFormaContacto(String value) {
        this.desFormaContacto = value;
    }

    /**
     * Obtiene el valor de la propiedad rfc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRfc() {
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
    public void setRfc(String value) {
        this.rfc = value;
    }

}
