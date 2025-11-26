package mx.gob.imss.contadores.controller;

 
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import mx.gob.imss.contadores.dto.AcreditacionMenbresiaResponseDto;
import mx.gob.imss.contadores.dto.ColegioContadorDto;
import mx.gob.imss.contadores.dto.PlantillaDatoDto;
import mx.gob.imss.contadores.dto.RfcRequestDto;
import mx.gob.imss.contadores.dto.SolicitudBajaDto;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.service.AcreditacionMembresiaService;
import mx.gob.imss.contadores.service.ContadorPublicoAutorizadoService;
import mx.gob.imss.contadores.service.JwtUtilService;
 

import org.springframework.security.core.Authentication;  
import java.util.Map;
 

@RestController 
@CrossOrigin("*") 
@RequestMapping("/mssideimss-contadores/v1")
public class ContadoresRestController {
	private final static Logger logger = LoggerFactory.getLogger(ContadoresRestController.class);
  
    @Autowired
    private AcreditacionMembresiaService acreditacionMembresiaService;

    @Autowired
    private  JwtUtilService jwtUtilService;

  
    @Autowired
    private ContadorPublicoAutorizadoService contadorPublicoAutorizadoService;


    @Value("${sideimss.acuses.microservice.url}")  
    private String acusesMicroserviceUrl;

 
    @GetMapping("/info")
	public ResponseEntity<List<String>> info() {
		logger.info("........................mssideimss-contadores info..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-contadores");
		list.add("20251002");
		list.add("Contadores");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}


	@GetMapping("/list")
	public ResponseEntity<List<String>> list() {
		logger.info("........................mssideimss-contadores list..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-contadores");
		list.add("20251002");
		list.add("Contadores");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}

 
 
    








   @PostMapping("/acreditacionMembresia")
    public ResponseEntity<AcreditacionMenbresiaResponseDto> acreditacionMembresia(@RequestBody PlantillaDatoDto plantillaDatoDto) {
        logger.info("Recibiendo datos de acreditación y membresía acreditacionMembresia:");
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        logger.info("plantillaDatoDto.getDatosJson():" + plantillaDatoDto.getDatosJson());
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaActualFormateada = fechaActual.format(formatter);

        // Obtención del JWT Token y manejo de errores
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = null;
        String rfc = null;
        String nombreCompleto = null;

        if (authentication != null && authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            jwtToken = (String) details.get("jwt");
        }

        if (jwtToken == null) {
            logger.warn("No se pudo obtener el token JWT del SecurityContext.");
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("No se pudo obtener el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Claims claims = jwtUtilService.extractAllClaims(jwtToken);
            rfc = (String) claims.get("rfc");
            String nombre = (String) claims.get("nombre");
            String primerApellido = (String) claims.get("primerApellido");   
            String segundoApellido = (String) claims.get("segundoApellido");
            nombreCompleto = String.format("%s %s %s", nombre, primerApellido, segundoApellido != null ? segundoApellido : "").trim();
            logger.info("RFC extraído del token JWT: {}", rfc);
        } catch (Exception e) {
            logger.error("Error al extraer datos (RFC/Nombre) del token JWT: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("Error al procesar el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }





 

        

        // Crear instancia de NdtPlantillaDato
        NdtPlantillaDato ndtPlantillaDato = new NdtPlantillaDato();
        ndtPlantillaDato.setDesRfc(rfc);
        ndtPlantillaDato.setNomDocumento(plantillaDatoDto.getNomDocumento());
        ndtPlantillaDato.setDesPathVersion(plantillaDatoDto.getDesVersion());
        ndtPlantillaDato.setDesDatos(plantillaDatoDto.getDatosJson());
        ndtPlantillaDato.setDesTipoAcuse(plantillaDatoDto.getTipoAcuse().name());
        ndtPlantillaDato.setFecRegistro(fechaActual);
       
        String urlDocumentoBase64 = null;
        String urlDocumento = null;
        String mensajeCorreo = "No se intentó enviar el correo."; // Mensaje por defecto

        try {
            // **1. INTENTO DE ENVÍO DE CORREO (no es bloqueante para el guardado):**
            logger.info("Iniciando el intento de envío del correo de notificación.");
            // Utilizamos .block() aquí para esperar el resultado del Mono<String>
            // El error si el correo no se envía no se propaga como una excepción aquí.
            mensajeCorreo = acreditacionMembresiaService.enviarCorreoAcreditacion(rfc, nombreCompleto, jwtToken).block();
            logger.info("Resultado del intento de envío de correo: {}", mensajeCorreo);

            // **2. GUARDADO DE INFORMACIÓN (Este sí debe ser exitoso):**
           logger.info("Iniciando el proceso para obtener sello digital y guardar la plantilla.");
            // Llama al nuevo método que se encarga de obtener el sello y luego guardar
            NdtPlantillaDato plantillaGuardadaConSello = acreditacionMembresiaService.obtenerSelloYGuardarPlantilla(ndtPlantillaDato, jwtToken).block();
            urlDocumento = rfc + "|" + plantillaGuardadaConSello.getCveIdPlantillaDato().toString();
            urlDocumentoBase64 = Base64.getEncoder().encodeToString(urlDocumento.getBytes("UTF-8"));
            logger.info("Plantilla de datos guardada exitosamente con ID y sello: {}", plantillaGuardadaConSello.getCveIdPlantillaDato());

        } catch (Exception e) {
            // Si el guardado falla, entonces sí devolvemos un error 500
            logger.error("Fallo durante el guardado de datos: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorDto.setMensaje("Error al guardar la plantilla de datos. Por favor, intente más tarde.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        AcreditacionMenbresiaResponseDto responseDto = new AcreditacionMenbresiaResponseDto();
        responseDto.setFechaActual(fechaActualFormateada);
        responseDto.setCodigo(0);
        // Puedes combinar el mensaje del correo con el de éxito de la operación principal
        responseDto.setMensaje("Operación realizada exitosamente. " + mensajeCorreo);
        responseDto.setUrlDocumento(urlDocumentoBase64);
        logger.info("Operación realizada exitosamente.");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }





/**
     * Endpoint para obtener todos los datos del contador (personales, fiscales, contacto)
     * a partir de su RFC extraído del token de seguridad.
     * URL: GET /mssideimss-contadores/v1/datosContador
     */
    @GetMapping("/consultaDatosContador")
    public ResponseEntity<SolicitudBajaDto> consultaDatosContador() {
        logger.info("........................Iniciando consultaDatosContador..............................");

        // 1. Obtener el token y extraer el RFC  
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = null;
        String rfc = null;

        if (authentication != null && authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            jwtToken = (String) details.get("jwt");
        }
        
        if (jwtToken == null) {
            logger.warn("Token JWT no disponible. Se requiere autenticación.");
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        try {
            Claims claims = jwtUtilService.extractAllClaims(jwtToken);
            rfc = (String) claims.get("rfc");
            logger.info("RFC extraído del token JWT para consulta: {}", rfc);
        } catch (Exception e) {
            logger.error("Error al extraer RFC del token JWT: {}", e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // 2. Llamar al servicio para obtener los datos
        try {
            SolicitudBajaDto datosContador = contadorPublicoAutorizadoService.getDatosContador(rfc);
            logger.info("Consulta de datos del contador exitosa para RFC: {}", rfc);
            return new ResponseEntity<>(datosContador, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al consultar los datos del contador con RFC {}: {}", rfc, e.getMessage(), e);
            // Dependiendo del error, podrías devolver un 404 si el contador no existe.
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    




    /**
     * Endpoint para procesar la solicitud de baja de un contador público.
     * Incluye lógica para obtener el JWT, extraer RFC/nombre, generar sello digital y guardar la plantilla.
     * URL: POST /mssideimss-contadores/v1/solicitudBaja
     */
    /*
    @PostMapping("/solicitudBaja")
    public ResponseEntity<AcreditacionMenbresiaResponseDto> solicitudBaja(@RequestBody PlantillaDatoDto plantillaDatoDto) {
        logger.info("Recibiendo datos para Solicitud de Baja:");
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        logger.info("plantillaDatoDto.getDatosJson():" + plantillaDatoDto.getDatosJson());
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaActualFormateada = fechaActual.format(formatter);

        // Obtención del JWT Token y manejo de errores
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = null;
        String rfc = null;
        String nombreCompleto = null;

        if (authentication != null && authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            jwtToken = (String) details.get("jwt");
        }

        if (jwtToken == null) {
            logger.warn("No se pudo obtener el token JWT del SecurityContext para Solicitud Baja.");
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("No se pudo obtener el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Claims claims = jwtUtilService.extractAllClaims(jwtToken);
            rfc = (String) claims.get("rfc");
            String nombre = (String) claims.get("nombre");
            String primerApellido = (String) claims.get("primerApellido");
            String segundoApellido = (String) claims.get("segundoApellido");
            nombreCompleto = String.format("%s %s %s", nombre, primerApellido, segundoApellido != null ? segundoApellido : "").trim();
            logger.info("RFC extraído del token JWT para Solicitud Baja: {}", rfc);
        } catch (Exception e) {
            logger.error("Error al extraer datos (RFC/Nombre) del token JWT para Solicitud Baja: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("Error al procesar el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Crear instancia de NdtPlantillaDato
        NdtPlantillaDato ndtPlantillaDato = new NdtPlantillaDato();
        ndtPlantillaDato.setDesRfc(rfc);
        ndtPlantillaDato.setNomDocumento(plantillaDatoDto.getNomDocumento());
        ndtPlantillaDato.setDesPathVersion(plantillaDatoDto.getDesVersion());
        ndtPlantillaDato.setDesDatos(plantillaDatoDto.getDatosJson());
        // Asegúrate de que TipoAcuse.name() sea el correcto para el enum o usa una cadena si es solo un String
        ndtPlantillaDato.setDesTipoAcuse(plantillaDatoDto.getTipoAcuse().name());
        ndtPlantillaDato.setFecRegistro(fechaActual);

        String urlDocumentoBase64 = null;
        String urlDocumento = null;
        // String mensajeCorreo = "No se intentó enviar el correo."; // Si se requiere enviar correo para baja, se puede agregar

        try {
            // ** LLAMADA AL SERVICIO DE ContadorPublicoAutorizadoService **
            // Esto reemplaza la llamada directa a ndtPlantillaDatoRepository.save()
            NdtPlantillaDato plantillaGuardadaConSello = contadorPublicoAutorizadoService.obtenerSelloYGuardarPlantilla(ndtPlantillaDato, jwtToken).block();
            urlDocumento = rfc + "|" + plantillaGuardadaConSello.getCveIdPlantillaDato().toString();
            urlDocumentoBase64 = Base64.getEncoder().encodeToString(urlDocumento.getBytes("UTF-8"));
            logger.info("Plantilla de datos de Solicitud Baja guardada exitosamente con ID y sello: {}", plantillaGuardadaConSello.getCveIdPlantillaDato());

        } catch (Exception e) {
            logger.error("Fallo durante el guardado de datos de Solicitud Baja: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorDto.setMensaje("Error al guardar la plantilla de datos de Solicitud Baja. Por favor, intente más tarde.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        AcreditacionMenbresiaResponseDto responseDto = new AcreditacionMenbresiaResponseDto();
        responseDto.setFechaActual(fechaActualFormateada);
        responseDto.setCodigo(0);
        responseDto.setMensaje("Solicitud de Baja realizada exitosamente.");
        responseDto.setUrlDocumento(urlDocumentoBase64);
        logger.info("Operación de Solicitud de Baja realizada exitosamente.");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
*/

    /**
     * endpoint para consultar los datos del colegio de un contador.
     * Recibe el RFC del contador en el cuerpo de la solicitud como un objeto JSON.
     * URL: POST /mssideimss-contadores/v1/colegioContador
     * @param rfcRequestDto Objeto con el RFC del contador.
     * @return ResponseEntity con ColegioContadorDto si se encuentra, o un error.
     */
    @PostMapping("/colegioContador")
    public ResponseEntity<ColegioContadorDto> getColegioContador(@RequestBody RfcRequestDto rfcRequestDto) {  
        logger.info("Recibiendo solicitud para obtener colegio de contador con RFC: {}", rfcRequestDto.getRfcContador()); 

        String rfcContador = rfcRequestDto.getRfcContador();  

        if (rfcContador == null || rfcContador.trim().isEmpty()) {
            logger.warn("RFC de contador nulo o vacío en la solicitud.");
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        try {
            ColegioContadorDto colegio = contadorPublicoAutorizadoService.getColegioByRfcContador(rfcContador);
            if (colegio != null && !"N/A".equals(colegio.getRfcColegio())) {
                logger.info("Colegio encontrado para RFC {}: {}", rfcContador, colegio.getRazonSocial());
                return new ResponseEntity<>(colegio, HttpStatus.OK);
            } else {
                logger.warn("No se encontró colegio para el RFC de contador: {}", rfcContador);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("Error al consultar el colegio para el RFC {}: {}", rfcContador, e.getMessage(), e);
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    /**
     * Endpoint para procesar la modificación de datos del contador.
     * Genera el sello digital y guarda la plantilla con los nuevos datos.
     * URL: POST /mssideimss-contadores/v1/guardarModificacionDatos
     */
    @PostMapping("/guardarModificacionDatos")
    public ResponseEntity<AcreditacionMenbresiaResponseDto> guardarModificacionDatos(@RequestBody PlantillaDatoDto plantillaDatoDto) {
        logger.info("Recibiendo solicitud para Guardar Modificación de Datos:");
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        logger.info("plantillaDatoDto.getDatosJson():" + plantillaDatoDto.getDatosJson());
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
 
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaActualFormateada = fechaActual.format(formatter);

        // Obtención del JWT Token y manejo de errores
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = null;
        String rfc = null;
        String nombreCompleto = null;
        String tipoSolicitud = null;

        if (authentication != null && authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            jwtToken = (String) details.get("jwt");
        }

        if (jwtToken == null) {
            logger.warn("No se pudo obtener el token JWT del SecurityContext.");
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("No se pudo obtener el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Claims claims = jwtUtilService.extractAllClaims(jwtToken);
            rfc = (String) claims.get("rfc");
            String nombre = (String) claims.get("nombre");
            String primerApellido = (String) claims.get("primerApellido");   
            String segundoApellido = (String) claims.get("segundoApellido");
             tipoSolicitud = (String) claims.get("tipoSolicitud");
            nombreCompleto = String.format("%s %s %s", nombre, primerApellido, segundoApellido != null ? segundoApellido : "").trim();
            logger.info("RFC extraído del token JWT: {}", rfc);
        } catch (Exception e) {
            logger.error("Error al extraer datos (RFC/Nombre) del token JWT: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("Error al procesar el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }





 

        

        // Crear instancia de NdtPlantillaDato
        NdtPlantillaDato ndtPlantillaDato = new NdtPlantillaDato();
        ndtPlantillaDato.setDesRfc(rfc);
        ndtPlantillaDato.setNomDocumento(plantillaDatoDto.getNomDocumento());
        ndtPlantillaDato.setDesPathVersion(plantillaDatoDto.getDesVersion());
        ndtPlantillaDato.setDesDatos(plantillaDatoDto.getDatosJson());
        ndtPlantillaDato.setDesTipoAcuse(plantillaDatoDto.getTipoAcuse().name());
        ndtPlantillaDato.setFecRegistro(fechaActual);
       
        String urlDocumentoBase64 = null;
        String urlDocumento = null;
        String mensajeCorreo = "No se intentó enviar el correo."; // Mensaje por defecto

        try {
            // **1. INTENTO DE ENVÍO DE CORREO (no es bloqueante para el guardado):**
            logger.info("Iniciando el intento de envío del correo de notificación: {}", tipoSolicitud);
            // Utilizamos .block() aquí para esperar el resultado del Mono<String>
            // El error si el correo no se envía no se propaga como una excepción aquí.

            if (tipoSolicitud != null) {
                switch (tipoSolicitud.trim()) {
                    case "COLEGIO":
                        mensajeCorreo = acreditacionMembresiaService.enviarCorreoModificacionDatosColegio(rfc, nombreCompleto, jwtToken).block();
                        break;
                    case "DESPACHO":
                        mensajeCorreo = acreditacionMembresiaService.enviarCorreoModificacionDatosDespacho(rfc, nombreCompleto, jwtToken).block();
                        break;
                    case "CONTACTO":
                        mensajeCorreo = acreditacionMembresiaService.enviarCorreoModificacionDatosContacto(rfc, nombreCompleto, jwtToken).block();
                        break;
                    default:
                        logger.warn("Tipo de solicitud desconocido para envío de correo: {}", tipoSolicitud);
                        break;
                }
            }
            
            logger.info("Resultado del intento de envío de correo: {}", mensajeCorreo);


            // **2. GUARDADO DE INFORMACIÓN (Este sí debe ser exitoso):**
           logger.info("Iniciando el proceso para obtener sello digital y guardar la plantilla.");
            // Llama al nuevo método que se encarga de obtener el sello y luego guardar
            NdtPlantillaDato plantillaGuardadaConSello = acreditacionMembresiaService.obtenerSelloYGuardarPlantilla(ndtPlantillaDato, jwtToken).block();
            urlDocumento = rfc + "|" + plantillaGuardadaConSello.getCveIdPlantillaDato().toString();
            urlDocumentoBase64 = Base64.getEncoder().encodeToString(urlDocumento.getBytes("UTF-8"));
            logger.info("Plantilla de datos guardada exitosamente con ID y sello: {}", plantillaGuardadaConSello.getCveIdPlantillaDato());

        } catch (Exception e) {
            // Si el guardado falla, entonces sí devolvemos un error 500
            logger.error("Fallo durante el guardado de datos: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorDto.setMensaje("Error al guardar la plantilla de datos. Por favor, intente más tarde.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        AcreditacionMenbresiaResponseDto responseDto = new AcreditacionMenbresiaResponseDto();
        responseDto.setFechaActual(fechaActualFormateada);
        responseDto.setCodigo(0);
        // Puedes combinar el mensaje del correo con el de éxito de la operación principal
        responseDto.setMensaje("Operación realizada exitosamente. " + mensajeCorreo);
        responseDto.setUrlDocumento(urlDocumentoBase64);
        logger.info("Operación realizada exitosamente.");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }







    /**
     * Endpoint para procesar la solicitud de baja de un contador público.
     * Incluye lógica para obtener el JWT, extraer RFC/nombre, generar sello digital y guardar la plantilla.
     * URL: POST /mssideimss-contadores/v1/solicitudBaja
     */
    @PostMapping("/solicitudBaja")
    public ResponseEntity<AcreditacionMenbresiaResponseDto> solicitudBaja(@RequestBody PlantillaDatoDto plantillaDatoDto) {
        logger.info("Recibiendo solicitud para Guardar Modificación de Datos:");
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        logger.info("plantillaDatoDto.getDatosJson():" + plantillaDatoDto.getDatosJson());
        logger.info(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
 
        LocalDateTime fechaActual = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaActualFormateada = fechaActual.format(formatter);

        // Obtención del JWT Token y manejo de errores
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String jwtToken = null;
        String rfc = null;
        String nombreCompleto = null;

        if (authentication != null && authentication.getDetails() instanceof Map) {
            Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
            jwtToken = (String) details.get("jwt");
        }

        if (jwtToken == null) {
            logger.warn("No se pudo obtener el token JWT del SecurityContext.");
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("No se pudo obtener el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        try {
            Claims claims = jwtUtilService.extractAllClaims(jwtToken);
            rfc = (String) claims.get("rfc");
            String nombre = (String) claims.get("nombre");
            String primerApellido = (String) claims.get("primerApellido");   
            String segundoApellido = (String) claims.get("segundoApellido");
            nombreCompleto = String.format("%s %s %s", nombre, primerApellido, segundoApellido != null ? segundoApellido : "").trim();
            logger.info("RFC extraído del token JWT: {}", rfc);
        } catch (Exception e) {
            logger.error("Error al extraer datos (RFC/Nombre) del token JWT: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(500);
            errorDto.setMensaje("Error al procesar el token de seguridad.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }





 

        

        // Crear instancia de NdtPlantillaDato
        NdtPlantillaDato ndtPlantillaDato = new NdtPlantillaDato();
        ndtPlantillaDato.setDesRfc(rfc);
        ndtPlantillaDato.setNomDocumento(plantillaDatoDto.getNomDocumento());
        ndtPlantillaDato.setDesPathVersion(plantillaDatoDto.getDesVersion());
        ndtPlantillaDato.setDesDatos(plantillaDatoDto.getDatosJson());
        ndtPlantillaDato.setDesTipoAcuse(plantillaDatoDto.getTipoAcuse().name());
        ndtPlantillaDato.setFecRegistro(fechaActual);
       
        String urlDocumentoBase64 = null;
        String urlDocumento = null;
        String mensajeCorreo = "No se intentó enviar el correo."; // Mensaje por defecto

        try {
            // **1. INTENTO DE ENVÍO DE CORREO (no es bloqueante para el guardado):**
            logger.info("Iniciando el intento de envío del correo de notificación.");
            // Utilizamos .block() aquí para esperar el resultado del Mono<String>
            // El error si el correo no se envía no se propaga como una excepción aquí.
            mensajeCorreo = acreditacionMembresiaService.enviarCorreoSolicitudBaja(rfc, nombreCompleto, jwtToken).block();
            logger.info("Resultado del intento de envío de correo: {}", mensajeCorreo);

            // **2. GUARDADO DE INFORMACIÓN (Este sí debe ser exitoso):**
           logger.info("Iniciando el proceso para obtener sello digital y guardar la plantilla.");
            // Llama al nuevo método que se encarga de obtener el sello y luego guardar
            NdtPlantillaDato plantillaGuardadaConSello = acreditacionMembresiaService.obtenerSelloYGuardarPlantilla(ndtPlantillaDato, jwtToken).block();
            urlDocumento = rfc + "|" + plantillaGuardadaConSello.getCveIdPlantillaDato().toString();
            urlDocumentoBase64 = Base64.getEncoder().encodeToString(urlDocumento.getBytes("UTF-8"));
            logger.info("Plantilla de datos guardada exitosamente con ID y sello: {}", plantillaGuardadaConSello.getCveIdPlantillaDato());

        } catch (Exception e) {
            // Si el guardado falla, entonces sí devolvemos un error 500
            logger.error("Fallo durante el guardado de datos: {}", e.getMessage(), e);
            AcreditacionMenbresiaResponseDto errorDto = new AcreditacionMenbresiaResponseDto();
            errorDto.setCodigo(HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorDto.setMensaje("Error al guardar la plantilla de datos. Por favor, intente más tarde.");
            errorDto.setFechaActual(fechaActualFormateada);
            return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        AcreditacionMenbresiaResponseDto responseDto = new AcreditacionMenbresiaResponseDto();
        responseDto.setFechaActual(fechaActualFormateada);
        responseDto.setCodigo(0);
        // Puedes combinar el mensaje del correo con el de éxito de la operación principal
        responseDto.setMensaje("Operación realizada exitosamente. " + mensajeCorreo);
        responseDto.setUrlDocumento(urlDocumentoBase64);
        logger.info("Operación realizada exitosamente.");
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }






}