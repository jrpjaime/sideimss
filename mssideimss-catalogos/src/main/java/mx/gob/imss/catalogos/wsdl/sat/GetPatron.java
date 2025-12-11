
package mx.gob.imss.catalogos.wsdl.sat;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="datosEntrada" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.entrada}EntradaSAT"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "datosEntrada"
})
@XmlRootElement(name = "getPatron", namespace = "http://mx/gob/imss/didt/cia/interoper/sat/ws")
public class GetPatron {

    @XmlElement(namespace = "http://mx/gob/imss/didt/cia/interoper/sat/ws", required = true)
    protected EntradaSAT datosEntrada;

    /**
     * Obtiene el valor de la propiedad datosEntrada.
     * 
     * @return
     *     possible object is
     *     {@link EntradaSAT }
     *     
     */
    public EntradaSAT getDatosEntrada() {
        return datosEntrada;
    }

    /**
     * Define el valor de la propiedad datosEntrada.
     * 
     * @param value
     *     allowed object is
     *     {@link EntradaSAT }
     *     
     */
    public void setDatosEntrada(EntradaSAT value) {
        this.datosEntrada = value;
    }

}
