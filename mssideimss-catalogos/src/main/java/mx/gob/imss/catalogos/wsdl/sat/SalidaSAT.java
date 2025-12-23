
package mx.gob.imss.catalogos.wsdl.sat;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para SalidaSAT complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SalidaSAT"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RFC_Original" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="RFC_Solicitado" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="RFC_Vigente" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="actividad" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}Actividades" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="identificacion" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}Identificacion" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="mensaje" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}Mensajes" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="mensajeControl" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.error}MensajeControl"/&gt;
 *         &lt;element name="obligacion" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}Obligaciones" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="regimen" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}Regimenes" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="replegal" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}RepLegales" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="rol" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}Roles" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="sucursal" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}Sucursales" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="ubicacion" type="{java:mx.gob.imss.didt.cia.interoper.sat.pojos.salida}Ubicacion" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SalidaSAT", propOrder = {
    "rfcOriginal",
    "rfcSolicitado",
    "rfcVigente",
    "actividad",
    "identificacion",
    "mensaje",
    "mensajeControl",
    "obligacion",
    "regimen",
    "replegal",
    "rol",
    "sucursal",
    "ubicacion"
})
public class SalidaSAT {

    @XmlElement(name = "RFC_Original", required = true, nillable = true)
    protected String rfcOriginal;
    @XmlElement(name = "RFC_Solicitado", required = true, nillable = true)
    protected String rfcSolicitado;
    @XmlElement(name = "RFC_Vigente", required = true, nillable = true)
    protected String rfcVigente;
    @XmlElement(nillable = true)
    protected List<Actividades> actividad;
    @XmlElement(nillable = true)
    protected List<Identificacion> identificacion;
    @XmlElement(nillable = true)
    protected List<Mensajes> mensaje;
    @XmlElement(required = true, nillable = true)
    protected MensajeControl mensajeControl;
    @XmlElement(nillable = true)
    protected List<Obligaciones> obligacion;
    @XmlElement(nillable = true)
    protected List<Regimenes> regimen;
    @XmlElement(nillable = true)
    protected List<RepLegales> replegal;
    @XmlElement(nillable = true)
    protected List<Roles> rol;
    @XmlElement(nillable = true)
    protected List<Sucursales> sucursal;
    @XmlElement(nillable = true)
    protected List<Ubicacion> ubicacion;

    /**
     * Obtiene el valor de la propiedad rfcOriginal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRFCOriginal() {
        return rfcOriginal;
    }

    /**
     * Define el valor de la propiedad rfcOriginal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRFCOriginal(String value) {
        this.rfcOriginal = value;
    }

    /**
     * Obtiene el valor de la propiedad rfcSolicitado.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRFCSolicitado() {
        return rfcSolicitado;
    }

    /**
     * Define el valor de la propiedad rfcSolicitado.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRFCSolicitado(String value) {
        this.rfcSolicitado = value;
    }

    /**
     * Obtiene el valor de la propiedad rfcVigente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRFCVigente() {
        return rfcVigente;
    }

    /**
     * Define el valor de la propiedad rfcVigente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRFCVigente(String value) {
        this.rfcVigente = value;
    }

    /**
     * Gets the value of the actividad property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the actividad property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getActividad().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Actividades }
     * 
     * 
     */
    public List<Actividades> getActividad() {
        if (actividad == null) {
            actividad = new ArrayList<Actividades>();
        }
        return this.actividad;
    }

    /**
     * Gets the value of the identificacion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the identificacion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getIdentificacion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Identificacion }
     * 
     * 
     */
    public List<Identificacion> getIdentificacion() {
        if (identificacion == null) {
            identificacion = new ArrayList<Identificacion>();
        }
        return this.identificacion;
    }

    /**
     * Gets the value of the mensaje property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the mensaje property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMensaje().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Mensajes }
     * 
     * 
     */
    public List<Mensajes> getMensaje() {
        if (mensaje == null) {
            mensaje = new ArrayList<Mensajes>();
        }
        return this.mensaje;
    }

    /**
     * Obtiene el valor de la propiedad mensajeControl.
     * 
     * @return
     *     possible object is
     *     {@link MensajeControl }
     *     
     */
    public MensajeControl getMensajeControl() {
        return mensajeControl;
    }

    /**
     * Define el valor de la propiedad mensajeControl.
     * 
     * @param value
     *     allowed object is
     *     {@link MensajeControl }
     *     
     */
    public void setMensajeControl(MensajeControl value) {
        this.mensajeControl = value;
    }

    /**
     * Gets the value of the obligacion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the obligacion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getObligacion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Obligaciones }
     * 
     * 
     */
    public List<Obligaciones> getObligacion() {
        if (obligacion == null) {
            obligacion = new ArrayList<Obligaciones>();
        }
        return this.obligacion;
    }

    /**
     * Gets the value of the regimen property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the regimen property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegimen().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Regimenes }
     * 
     * 
     */
    public List<Regimenes> getRegimen() {
        if (regimen == null) {
            regimen = new ArrayList<Regimenes>();
        }
        return this.regimen;
    }

    /**
     * Gets the value of the replegal property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the replegal property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReplegal().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RepLegales }
     * 
     * 
     */
    public List<RepLegales> getReplegal() {
        if (replegal == null) {
            replegal = new ArrayList<RepLegales>();
        }
        return this.replegal;
    }

    /**
     * Gets the value of the rol property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rol property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRol().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Roles }
     * 
     * 
     */
    public List<Roles> getRol() {
        if (rol == null) {
            rol = new ArrayList<Roles>();
        }
        return this.rol;
    }

    /**
     * Gets the value of the sucursal property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sucursal property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSucursal().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Sucursales }
     * 
     * 
     */
    public List<Sucursales> getSucursal() {
        if (sucursal == null) {
            sucursal = new ArrayList<Sucursales>();
        }
        return this.sucursal;
    }

    /**
     * Gets the value of the ubicacion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the ubicacion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUbicacion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Ubicacion }
     * 
     * 
     */
    public List<Ubicacion> getUbicacion() {
        if (ubicacion == null) {
            ubicacion = new ArrayList<Ubicacion>();
        }
        return this.ubicacion;
    }

}
