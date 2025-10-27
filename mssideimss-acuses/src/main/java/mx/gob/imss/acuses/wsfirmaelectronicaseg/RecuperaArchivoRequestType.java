
package mx.gob.imss.acuses.wsfirmaelectronicaseg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RecuperaArchivoRequestType complex type.</p>
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.</p>
 * 
 * <pre>{@code
 * <complexType name="RecuperaArchivoRequestType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="jsonParams" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecuperaArchivoRequestType", propOrder = {
    "jsonParams"
})
public class RecuperaArchivoRequestType {

    @XmlElement(required = true)
    protected String jsonParams;

    /**
     * Obtiene el valor de la propiedad jsonParams.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJsonParams() {
        return jsonParams;
    }

    /**
     * Define el valor de la propiedad jsonParams.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJsonParams(String value) {
        this.jsonParams = value;
    }

}
