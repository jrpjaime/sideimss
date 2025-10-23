package mx.gob.imss.catalogos.medioscontacto;

import javax.xml.ws.BindingProvider;
import java.util.List;
import java.util.Map;

public class MediosContactoClienteTest {

    public static void main(String[] args) {
        System.out.println("Iniciando prueba del cliente MediosContacto...");

        try {
            // 1. Crear una instancia del servicio
            // La clase generada JwsServiceSoapBindingQSService ya tiene la wsdlLocation embebida.
            JwsServiceSoapBindingQSService service = new JwsServiceSoapBindingQSService();

            // 2. Obtener la interfaz del puerto
            // La interfaz 'Jws' es la que define la operación 'recuperaMediosContacto'.
            Jws port = service.getJwsServiceSoapBindingQSPort();

            // Opcional: Configurar la URL del endpoint si necesitas apuntar a un ambiente diferente
            // Por defecto, usa la URL del WSDL que es "http://osbserviciosdigitales.imss.gob.mx:80/DictamenService/MediosContacto?wsdl"
            // Si el endpoint real es solo "http://osbserviciosdigitales.imss.gob.mx:80/DictamenService/MediosContacto"
            // o si necesitas un timeout:
            // Map<String, Object> requestContext = ((BindingProvider) port).getRequestContext();
            // requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "http://osbserviciosdigitales.imss.gob.mx:80/DictamenService/MediosContacto");
            // requestContext.put("com.sun.xml.ws.request.timeout", 10000); // 10 segundos de timeout

            // 3. Preparar los parámetros para la operación
            // La operación 'recuperaMediosContacto' requiere un String 'rfc'.
            String rfcParaBuscar = "GOAP7903086U1"; // <<-- ¡IMPORTANTE! Reemplaza con un RFC válido para el servicio.

            System.out.println("Invocando operación recuperaMediosContacto con RFC: " + rfcParaBuscar);

            // 4. Invocar la operación del servicio
            ArrayOfMedContactoRepreLegalDTOLiteral resultado = port.recuperaMediosContacto(rfcParaBuscar);

            // 5. Procesar el resultado
            if (resultado != null && resultado.getMedContactoRepreLegalDTO() != null) {
                List<MedContactoRepreLegalDTO> medios = resultado.getMedContactoRepreLegalDTO();
                if (!medios.isEmpty()) {
                    System.out.println("Medios de contacto encontrados para RFC " + rfcParaBuscar + ":");
                    for (MedContactoRepreLegalDTO medio : medios) {
                        System.out.println("  Tipo Contacto: " + medio.getTipoContacto());
                        System.out.println("  Descripción:   " + medio.getDesFormaContacto());
                        System.out.println("  RFC Asociado:  " + medio.getRfc());
                        System.out.println("  --------------------");
                    }
                } else {
                    System.out.println("No se encontraron medios de contacto para el RFC " + rfcParaBuscar + ".");
                }
            } else {
                System.out.println("La respuesta del servicio fue nula o vacía.");
            }

        } catch (Exception e) {
            System.err.println("Error al probar el cliente SOAP: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Prueba del cliente MediosContacto finalizada.");
    }
}