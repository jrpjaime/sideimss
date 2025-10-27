
package mx.gob.imss.acuses.wsfirmaelectronicaseg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para FirmaXMLSegRequestType complex type.</p>
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.</p>
 * 
 * <pre>{@code
 * <complexType name="FirmaXMLSegRequestType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="jsonParms" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FirmaXMLSegRequestType", propOrder = {
    "jsonParms"
})
public class FirmaXMLSegRequestType {

    @XmlElement(required = true)
    protected String jsonParms;

    /**
     * Obtiene el valor de la propiedad jsonParms.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJsonParms() {
        return jsonParms;
    }

    /**
     * Define el valor de la propiedad jsonParms.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJsonParms(String value) {
        this.jsonParms = value;
    }

}
