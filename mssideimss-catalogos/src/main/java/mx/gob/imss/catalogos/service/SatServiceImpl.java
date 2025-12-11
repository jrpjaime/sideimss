package mx.gob.imss.catalogos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.List;
import javax.xml.ws.BindingProvider;

import mx.gob.imss.catalogos.dto.RfcColegioRequestDto;
import mx.gob.imss.catalogos.dto.RfcColegioResponseDto;
import mx.gob.imss.catalogos.wsdl.sat.EntradaSAT;
import mx.gob.imss.catalogos.wsdl.sat.Identificacion;
import mx.gob.imss.catalogos.wsdl.sat.MensajeControl;
import mx.gob.imss.catalogos.wsdl.sat.SATPatrones;
import mx.gob.imss.catalogos.wsdl.sat.SATPatronesService;
import mx.gob.imss.catalogos.wsdl.sat.SalidaSAT;

@Service("satService")
public class SatServiceImpl implements SatService {
    private final static Logger logger = LoggerFactory.getLogger(SatServiceImpl.class);

    @Value("${integration.sat.wsdl-url}")
    private String wsdlUrl;

    @Override
    public RfcColegioResponseDto consultarRfc(RfcColegioRequestDto rfcColegioRequestDto) {
        String rfcBusqueda = rfcColegioRequestDto.getRfc();
        logger.info("Iniciando consumo SOAP SAT para RFC: {}", rfcBusqueda);

        RfcColegioResponseDto response = new RfcColegioResponseDto();
        // Seteamos el RFC de entrada por defecto en la respuesta
        response.setRfc(rfcBusqueda);

        try {
            // 1. Instanciar el Cliente SOAP
            URL url = new URL(this.wsdlUrl);
            SATPatronesService service = new SATPatronesService(url);
            SATPatrones port = service.getSATPatronesSoapPort();

            // (Opcional) Configurar Timeouts para no colgar el microservicio si el SAT no responde
            configurarTimeouts((BindingProvider) port);

            // 2. Preparar el Objeto de Entrada
            EntradaSAT entrada = new EntradaSAT();
            entrada.setRfc(rfcBusqueda);
            
            // IMPORTANTE: El WSDL marca estos campos como obligatorios.
            // Si el servicio valida credenciales, debes poner las reales.
            // Si es de acceso libre interno, prueba con cadenas vacías o genéricas.
            entrada.setUsuario("SISTEMAS"); 
            entrada.setPassword("SISTEMAS");
            entrada.setCurp("");
            entrada.setIdSucursal("");
            entrada.setNit("");

            // 3. Invocar al servicio
            logger.info("Enviando petición a getPatron...");
            SalidaSAT respuestaSoap = port.getPatron(entrada);

            // 4. Procesar la respuesta
            if (respuestaSoap != null) {
                MensajeControl control = respuestaSoap.getMensajeControl();
                
                // Validar si el servicio respondió éxito (flag booleana en el WSDL)
                if (control != null && control.isExito()) {
                    
                    // --- CORRECCIÓN: OBTENER RFC VIGENTE ---
                    // El RFC devuelto por el SAT está en el objeto padre (SalidaSAT), no en Identificacion.
                    // Nota: Si getRFCVigente() marca error, verifica en SalidaSAT.java si se generó como getRFC_Vigente()
                    if (respuestaSoap.getRFCVigente() != null && !respuestaSoap.getRFCVigente().isEmpty()) {
                        response.setRfc(respuestaSoap.getRFCVigente());
                    }

                    // --- OBTENER RAZÓN SOCIAL ---
                    // La información de nombres vive en la lista de Identificación
                    List<Identificacion> listaIdent = respuestaSoap.getIdentificacion();
                    
                    if (listaIdent != null && !listaIdent.isEmpty()) {
                        Identificacion identidad = listaIdent.get(0);
                        
                        // Extraer Razón Social
                        String razonSocial = identidad.getRazonSoc();
                        
                        // Lógica de respaldo: Si es Persona Física, a veces Razón Social viene null
                        // y hay que concatenar Nombre + Apellidos
                        if (razonSocial == null || razonSocial.trim().isEmpty()) {
                            String nombre = identidad.getNombre() != null ? identidad.getNombre() : "";
                            String apPaterno = identidad.getApPaterno() != null ? identidad.getApPaterno() : "";
                            String apMaterno = identidad.getApMaterno() != null ? identidad.getApMaterno() : "";
                            razonSocial = (nombre + " " + apPaterno + " " + apMaterno).trim();
                        }
                        
                        response.setNombreRazonSocial(razonSocial);
                        logger.info("Respuesta exitosa. Razón Social: {}", razonSocial);
                    } else {
                        logger.warn("El servicio retornó éxito, pero la lista de identificación está vacía.");
                    }
                } else {
                    String msgError = (control != null) ? control.getDescripcion() : "Desconocido";
                    logger.warn("El servicio SAT retornó estatus de NO éxito. Mensaje: {}", msgError);
                }
            } else {
                logger.error("El objeto de respuesta SOAP fue NULL.");
            }

        } catch (Exception e) {
            logger.error("Error crítico consumiendo servicio SAT (WSDL): ", e);
            // No lanzamos excepción para no romper el flujo del front, devolvemos el objeto con nulls o los datos por defecto
        }

        return response;
    }

    /**
     * Configura timeouts de conexión para evitar hilos colgados.
     */
    private void configurarTimeouts(BindingProvider port) {
        try {
            // 5 segundos para conectar, 10 segundos para recibir datos
            port.getRequestContext().put("com.sun.xml.ws.connect.timeout", 5000); 
            port.getRequestContext().put("com.sun.xml.ws.request.timeout", 10000);
        } catch (Exception e) {
            logger.warn("No se pudieron configurar los timeouts del WS: {}", e.getMessage());
        }
    }
}