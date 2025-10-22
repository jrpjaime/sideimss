package mx.gob.imss.seguridad.controller;

 
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; 
 
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.gob.imss.seguridad.dto.UsuarioDto;
import mx.gob.imss.seguridad.dto.AuthRequestDto;
import mx.gob.imss.seguridad.dto.AuthResponseDto;
import mx.gob.imss.seguridad.dto.RefreshTokenRequest; 
import mx.gob.imss.seguridad.service.JwtUtilService;
import mx.gob.imss.seguridad.service.UsuarioService;

@RestController
@CrossOrigin("*") 
@RequestMapping("/mssideimss-seguridad/v1")
public class SeguridadRestController {

	private final static Logger logger = LoggerFactory.getLogger(SeguridadRestController.class);
	
 

    @Autowired
    private UserDetailsService userDetailsService;

 

    @Autowired
    private JwtUtilService jwtUtilService;
 
    @Autowired
    private UsuarioService usuarioService;

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
        try {
            if (authRequestDto.getUser() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error Authetication");
            }

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(authRequestDto.getUser());

            // Obtener información del usuario desde la base de datos
            Optional<UsuarioDto> optionalUsuarioDto = usuarioService.getUsuarioInfoByRfc(authRequestDto.getUser());

            if (!optionalUsuarioDto.isPresent()) {
                logger.warn("No se encontró información del usuario para el RFC: {}", authRequestDto.getUser());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado o sin datos.");
            }

            UsuarioDto usuarioDto = optionalUsuarioDto.get();

            List<String> roles = new ArrayList<String>();
            roles.add("Contador");
            roles.add("Representante");
            // roles.add("Patron");

            if (roles.isEmpty()) {
                logger.warn("El usuario {} no tiene roles asignados.", authRequestDto.getUser());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error Authetication");
            }

            logger.info("Roles para el usuario {}: {}", authRequestDto.getUser(), roles);
            // Generar token
            String jwt = this.jwtUtilService.generateToken(userDetails, roles, usuarioDto);
            String refreshToken = this.jwtUtilService.generateRefreshToken(userDetails, roles, usuarioDto);

            AuthResponseDto authResponseDto = new AuthResponseDto();
            authResponseDto.setToken(jwt);
            authResponseDto.setRefreshToken(refreshToken);

            return new ResponseEntity<AuthResponseDto>(authResponseDto, HttpStatus.OK);

        } catch (Exception e) {
            logger.info("Error Authetication:::", e); // Imprimir el stack trace para depuración
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error Authetication:::" + e.getMessage());
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> auth(@RequestBody RefreshTokenRequest request) { // Usar el DTO
        String refreshToken = request.getRefreshToken();

        try {
            String username = jwtUtilService.extractUsername(refreshToken);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            List<String> roles = new ArrayList<String>();
            roles.add("Contador");
            roles.add("Representante");

            if (roles.isEmpty()) {
                logger.warn("El usuario {} no tiene roles asignados.", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error Authetication");
            }

            // Obtener información del usuario para el refresh token
            Optional<UsuarioDto> optionalUsuarioDto = usuarioService.getUsuarioInfoByRfc(username);

            if (!optionalUsuarioDto.isPresent()) {
                logger.warn("No se encontró información del usuario para el RFC durante el refresh token: {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no encontrado o sin datos.");
            }
            UsuarioDto usuarioDto = optionalUsuarioDto.get();


            logger.info("Roles para el usuario {}: {}", username, roles);

            if (jwtUtilService.validateToken(refreshToken, userDetails)) {
                // Pasamos el registro patronal seleccionado a los métodos de generación
                String newJwt = jwtUtilService.generateToken(userDetails, roles, usuarioDto);
                String newRefreshToken = jwtUtilService.generateRefreshToken(userDetails, roles, usuarioDto);

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
}



 
 

 
