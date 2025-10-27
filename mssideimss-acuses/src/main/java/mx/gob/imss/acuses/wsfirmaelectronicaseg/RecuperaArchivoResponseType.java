
package mx.gob.imss.acuses.wsfirmaelectronicaseg;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RecuperaArchivoResponseType complex type.</p>
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.</p>
 * 
 * <pre>{@code
 * <complexType name="RecuperaArchivoResponseType">
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="resultado" type="{http://doctrust.metatrust.com.mx/WsFirmaElectronicaSeg.xsd}ResultadoType"/>
 *         <element name="jsonSalida" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecuperaArchivoResponseType", propOrder = {
    "resultado",
    "jsonSalida"
})
public class RecuperaArchivoResponseType {

    @XmlElement(required = true)
    protected ResultadoType resultado;
    @XmlElement(required = true)
    protected String jsonSalida;

    /**
     * Obtiene el valor de la propiedad resultado.
     * 
     * @return
     *     possible object is
     *     {@link ResultadoType }
     *     
     */
    public ResultadoType getResultado() {
        return resultado;
    }

    /**
     * Define el valor de la propiedad resultado.
     * 
     * @param value
     *     allowed object is
     *     {@link ResultadoType }
     *     
     */
    public void setResultado(ResultadoType value) {
        this.resultado = value;
    }

    /**
     * Obtiene el valor de la propiedad jsonSalida.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJsonSalida() {
        return jsonSalida;
    }

    /**
     * Define el valor de la propiedad jsonSalida.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJsonSalida(String value) {
        this.jsonSalida = value;
    }

}
