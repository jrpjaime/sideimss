
package mx.gob.imss.catalogos.medioscontacto;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the mx.gob.imss.catalogos.medioscontacto package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ArrayOfMedContactoRepreLegalDTOLiteral_QNAME = new QName("http://www.openuri.org/", "ArrayOfMedContactoRepreLegalDTO_literal");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: mx.gob.imss.catalogos.medioscontacto
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link MedContactoRepreLegalDTO }
     * 
     */
    public MedContactoRepreLegalDTO createMedContactoRepreLegalDTO() {
        return new MedContactoRepreLegalDTO();
    }

    /**
     * Create an instance of {@link ArrayOfMedContactoRepreLegalDTOLiteral }
     * 
     */
    public ArrayOfMedContactoRepreLegalDTOLiteral createArrayOfMedContactoRepreLegalDTOLiteral() {
        return new ArrayOfMedContactoRepreLegalDTOLiteral();
    }

    /**
     * Create an instance of {@link RecuperaMediosContactoResponse }
     * 
     */
    public RecuperaMediosContactoResponse createRecuperaMediosContactoResponse() {
        return new RecuperaMediosContactoResponse();
    }

    /**
     * Create an instance of {@link RecuperaMediosContacto }
     * 
     */
    public RecuperaMediosContacto createRecuperaMediosContacto() {
        return new RecuperaMediosContacto();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ArrayOfMedContactoRepreLegalDTOLiteral }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.openuri.org/", name = "ArrayOfMedContactoRepreLegalDTO_literal")
    public JAXBElement<ArrayOfMedContactoRepreLegalDTOLiteral> createArrayOfMedContactoRepreLegalDTOLiteral(ArrayOfMedContactoRepreLegalDTOLiteral value) {
        return new JAXBElement<ArrayOfMedContactoRepreLegalDTOLiteral>(_ArrayOfMedContactoRepreLegalDTOLiteral_QNAME, ArrayOfMedContactoRepreLegalDTOLiteral.class, null, value);
    }

}
