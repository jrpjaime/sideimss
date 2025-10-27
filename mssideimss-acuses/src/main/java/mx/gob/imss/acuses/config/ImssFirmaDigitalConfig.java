package mx.gob.imss.acuses.config;

import mx.gob.imss.acuses.wsfirmaelectronicaseg.FirmaElectronicaSegService;
import mx.gob.imss.acuses.wsfirmaelectronicaseg.FirmaElectronicaSegPortType;  
import mx.gob.imss.acuses.wsfirmaelectronicaseg.ObjectFactory;  
 
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.namespace.QName;
import java.net.URL;

@Configuration
public class ImssFirmaDigitalConfig {

   // @Value("${imss.firma.wsdl.url:https://firmadigitalssl.imss.gob.mx/WsFirmaElectronicaSeg/FirmaElectronicaSegService?WSDL}")
   // private String wsdlUrl;

   @Value("${imss.firma-electronica.base-url}")
    private String imssFirmaBaseUrl;
 

    // Bean que proporciona la interfaz del puerto del servicio web
    @Bean
    public FirmaElectronicaSegPortType imssFirmaElectronicaSegPortType() { // Renombramos el método del bean
        try {
            URL wsdlLocation = new URL(imssFirmaBaseUrl + "/WsFirmaElectronicaSeg/FirmaElectronicaSegService?WSDL");
            
            // EL targetNamespace del Servicio es "http://doctrust.metatrust.com.mx/WsFirmaElectronicaSeg.wsdl"
            QName serviceName = new QName("http://doctrust.metatrust.com.mx/WsFirmaElectronicaSeg.wsdl", "FirmaElectronicaSegService");
            
            FirmaElectronicaSegService service = new FirmaElectronicaSegService(wsdlLocation, serviceName);
            
            // Usamos el método correcto para obtener el puerto
            FirmaElectronicaSegPortType port = service.getFirmaElectronicaSegPortTypePort();

            // Opcional: Configurar propiedades de timeout, interceptores 
            ((jakarta.xml.ws.BindingProvider) port).getRequestContext().put("jakarta.xml.ws.client.connectionTimeout", 5000); // ms
            ((jakarta.xml.ws.BindingProvider) port).getRequestContext().put("jakarta.xml.ws.client.receiveTimeout", 10000); // ms

            System.out.println("Puerto IMSS Firma Digital inicializado correctamente.");
            return port;

        } catch (Exception e) {
            System.err.println("Error al inicializar el puerto IMSS Firma Digital:");
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar el puerto SOAP para IMSS Firma Digital", e);
        }
    }

    // Bean para ObjectFactory
    @Bean
    public ObjectFactory imssObjectFactory() {
        return new ObjectFactory();
    }
}