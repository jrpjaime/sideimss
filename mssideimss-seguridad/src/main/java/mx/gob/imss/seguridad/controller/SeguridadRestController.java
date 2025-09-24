package mx.gob.imss.seguridad.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.gob.imss.seguridad.dto.AceptarTerminosDto;
import mx.gob.imss.seguridad.dto.AuthRequestDto;
import mx.gob.imss.seguridad.dto.AuthResponseDto;
import mx.gob.imss.seguridad.dto.RefreshTokenRequest;
import mx.gob.imss.seguridad.entity.SdtUsuario;
import mx.gob.imss.seguridad.entity.SdtUsuarioRole;
import mx.gob.imss.seguridad.repository.SdtUsuarioRepository;
import mx.gob.imss.seguridad.service.JwtUtilService;

@RestController
@CrossOrigin("*") 
@RequestMapping("/mssideimss-seguridad/v1")
public class SeguridadRestController {

	private final static Logger logger = LoggerFactory.getLogger(SeguridadRestController.class);
	
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private SdtUsuarioRepository sdtUsuarioRepository;

    @Autowired
    private JwtUtilService jwtUtilService;
 


    @GetMapping("/info")
	public ResponseEntity<List<String>> info() {
		logger.info("........................mssideimss-seguridad/info...........................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-seguridad");
		list.add("20250801");
		list.add("Seguridad");
		return new ResponseEntity<>(list, HttpStatus.OK);
	}



  

    @PostMapping("/login")
    public ResponseEntity<?> auth(@RequestBody AuthRequestDto authRequestDto) {
        logger.info("login ");
        logger.info("authRequestDto.getUser() " + authRequestDto.getUser());
        logger.info("authRequestDto.getPassword() " + authRequestDto.getPassword());
         logger.info("authRequestDto.getTipoAuth() " + authRequestDto.getTipoAuth());
        try {

            String registroPatronal=null;
 
            if( authRequestDto.getTipoAuth()!=null &&  authRequestDto.getTipoAuth().trim().toUpperCase().equals("FIRMA")){
                 logger.info("Autenticación por firma igital " );

                SdtUsuario sdtUsuario=sdtUsuarioRepository.findSdtUsuarioByRefRfc(authRequestDto.getUser()).iterator().next();  
                logger.info("getDesUsuario() " + sdtUsuario.getDesUsuario() );

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(sdtUsuario.getDesUsuario());
           
                

                String role="";

                for(SdtUsuarioRole sdtUsuarioRole: sdtUsuario.getSdtUsuarioRoles() ){
                    role=sdtUsuarioRole.getSdcRole().getDesRole(); 

                }

                // 3. Generar token
                String jwt = this.jwtUtilService.generateToken(userDetails,role, sdtUsuario,registroPatronal);
                String refreshToken = this.jwtUtilService.generateRefreshToken(userDetails, role, sdtUsuario,registroPatronal);

                AuthResponseDto authResponseDto = new AuthResponseDto();
                authResponseDto.setToken(jwt);
                authResponseDto.setRefreshToken(refreshToken);

                return new ResponseEntity<AuthResponseDto>(authResponseDto, HttpStatus.OK);

            }else 
            if( authRequestDto.getTipoAuth()!=null &&  authRequestDto.getTipoAuth().trim().toUpperCase().equals("LOGIN")     ){
                 logger.info("Autenticación por contraseña " );
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                String rawPassword = authRequestDto.getPassword();
                String encodedPassword = encoder.encode(rawPassword);
                logger.info("Salida encodedPassword: " + encodedPassword);

                // 1. Gestion authenticationManager
                this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDto.getUser(), authRequestDto.getPassword()));

                logger.info("authRequestDto.getUser() " + authRequestDto.getUser());
                logger.info("authRequestDto.getPassword() " + authRequestDto.getPassword());

                // 2. Validar el usuario en la bd
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(authRequestDto.getUser());
            // UserModel userModel = userRepository.findByName(authRequestDto.getUser());
                SdtUsuario sdtUsuario=sdtUsuarioRepository.findSdtUsuarioByDesUsuario(authRequestDto.getUser()).iterator().next(); 

                String role="";

                for(SdtUsuarioRole sdtUsuarioRole: sdtUsuario.getSdtUsuarioRoles() ){
                    role=sdtUsuarioRole.getSdcRole().getDesRole(); 

                }

                // 3. Generar token
                String jwt = this.jwtUtilService.generateToken(userDetails,role, sdtUsuario, registroPatronal);
                String refreshToken = this.jwtUtilService.generateRefreshToken(userDetails, role, sdtUsuario, registroPatronal);

                AuthResponseDto authResponseDto = new AuthResponseDto();
                authResponseDto.setToken(jwt);
                authResponseDto.setRefreshToken(refreshToken);

                return new ResponseEntity<AuthResponseDto>(authResponseDto, HttpStatus.OK);
            }

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error Authetication" );

        } catch (Exception e) {
            logger.info("Error Authetication:::");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error Authetication:::" + e.getMessage());
        }

    }

  


@PostMapping("/refresh")
public ResponseEntity<?> auth(@RequestBody RefreshTokenRequest request) { // Usar el DTO
    String refreshToken = request.getRefreshToken();
    String registroPatronalSeleccionado = request.getRegistroPatronal(); // Obtener el RP

    try {
        String username = jwtUtilService.extractUsername(refreshToken);
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        SdtUsuario sdtUsuario = sdtUsuarioRepository.findSdtUsuarioByDesUsuario(username).iterator().next();

        // --- VALIDACIÓN DE SEGURIDAD CRÍTICA ---
        // Antes de continuar, verificar que el usuario (`sdtUsuario`) tiene
        // permiso para operar con el `registroPatronalSeleccionado` que envió el cliente.
        // Esto previene que un usuario malicioso se asigne un RP que no le corresponde.
        if (registroPatronalSeleccionado != null && !usuarioTieneAccesoAlRegistro(sdtUsuario, registroPatronalSeleccionado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El usuario no tiene acceso a este Registro Patronal.");
        }

        String role = "";
        for (SdtUsuarioRole sdtUsuarioRole : sdtUsuario.getSdtUsuarioRoles()) {
            role = sdtUsuarioRole.getSdcRole().getDesRole();
        }

        if (jwtUtilService.validateToken(refreshToken, userDetails)) {
            // Pasamos el registro patronal seleccionado a los métodos de generación
            String newJwt = jwtUtilService.generateToken(userDetails, role, sdtUsuario, registroPatronalSeleccionado);
            String newRefreshToken = jwtUtilService.generateRefreshToken(userDetails, role, sdtUsuario, registroPatronalSeleccionado);

            AuthResponseDto authResponseDto = new AuthResponseDto();
            authResponseDto.setToken(newJwt);
            authResponseDto.setRefreshToken(newRefreshToken);

            logger.info("Token " + newJwt);
            logger.info("RefreshToken " + newRefreshToken);

            return new ResponseEntity<>(authResponseDto, HttpStatus.OK);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Refresh Token");
        }

    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error refresh token:::" + e.getMessage());
    }
}


/**
 * Método para la validación de seguridad. 
 * @param usuario El usuario autenticado.
 * @param registroPatronal El RP que se desea validar.
 * @return true si el usuario tiene acceso, false en caso contrario.
 */
private boolean usuarioTieneAccesoAlRegistro(SdtUsuario usuario, String registroPatronal) {
    // Lógica para verificar en la base de datos si este usuario está asociado
    // con el registro patronal proporcionado.
    // Por ejemplo: return registroPatronalRepository.existsByUsuarioAndRegistro(usuario.getId(), registroPatronal);
    return true;  
}


 




    @PostMapping("/aceptar-terminos")
    public ResponseEntity<?> aceptarTerminos(@RequestBody AceptarTerminosDto aceptarTerminosDto, Authentication authentication) {
        logger.info("aceptarTerminos ");
        
        String usernameEnSesion = aceptarTerminosDto.getRfc();
        logger.info("Usuario en sesión: " + usernameEnSesion);
        logger.info("RFC recibido del frontend: " + aceptarTerminosDto.getRfc());

        //SdtUsuario usuarioEnSesion = sdtUsuarioRepository.findSdtUsuarioByDesUsuario(usernameEnSesion).iterator().next();
        SdtUsuario usuarioEnSesion = sdtUsuarioRepository.findSdtUsuarioByRefRfc(usernameEnSesion).iterator().next();
   
        if (!usuarioEnSesion.getRefRfc().equalsIgnoreCase(aceptarTerminosDto.getRfc())) {
            logger.warn("Intento de aceptar términos con un RFC que no coincide. Sesión: {}, Enviado: {}",
                usuarioEnSesion.getRefRfc(), aceptarTerminosDto.getRfc());
            // Devolvemos un error 403 Forbidden para indicar que la acción no está permitida.
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("El RFC proporcionado no coincide con el usuario autenticado.");
        }


        return ResponseEntity.ok().build();
    }


}

 
