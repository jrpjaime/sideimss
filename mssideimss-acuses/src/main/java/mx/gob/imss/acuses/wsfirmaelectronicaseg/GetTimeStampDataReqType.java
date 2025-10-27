
package mx.gob.imss.acuses.wsfirmaelectronicaseg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para GetTimeStampDataReqType complex type.</p>
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.</p>
 * 
 * <pre>{@code
 * <complexType name="GetTimeStampDataReqType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="jsonSolicitud" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetTimeStampDataReqType", propOrder = {
    "jsonSolicitud"
})
public class GetTimeStampDataReqType {

    @XmlElement(required = true)
    protected String jsonSolicitud;

    /**
     * Obtiene el valor de la propiedad jsonSolicitud.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJsonSolicitud() {
        return jsonSolicitud;
    }

    /**
     * Define el valor de la propiedad jsonSolicitud.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJsonSolicitud(String value) {
        this.jsonSolicitud = value;
    }

}
