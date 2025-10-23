
package mx.gob.imss.catalogos.medioscontacto;

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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="listaMediosContacto" type="{http://www.openuri.org/}ArrayOfMedContactoRepreLegalDTO_literal"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "listaMediosContacto"
})
@XmlRootElement(name = "recuperaMediosContactoResponse")
public class RecuperaMediosContactoResponse {

    @XmlElement(required = true)
    protected ArrayOfMedContactoRepreLegalDTOLiteral listaMediosContacto;

    /**
     * Obtiene el valor de la propiedad listaMediosContacto.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfMedContactoRepreLegalDTOLiteral }
     *     
     */
    public ArrayOfMedContactoRepreLegalDTOLiteral getListaMediosContacto() {
        return listaMediosContacto;
    }

    /**
     * Define el valor de la propiedad listaMediosContacto.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfMedContactoRepreLegalDTOLiteral }
     *     
     */
    public void setListaMediosContacto(ArrayOfMedContactoRepreLegalDTOLiteral value) {
        this.listaMediosContacto = value;
    }

}
