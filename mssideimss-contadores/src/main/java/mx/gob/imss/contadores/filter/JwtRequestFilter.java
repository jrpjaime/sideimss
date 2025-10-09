package mx.gob.imss.contadores.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse; 

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Para manejar los roles
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map; 


 

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final static Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class); 

    @Autowired
    private mx.gob.imss.contadores.service.JwtUtilService jwtUtilService;
  
    private final WebAuthenticationDetailsSource authenticationDetailsSource = new WebAuthenticationDetailsSource(); 
    
       @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        logger.info(":::::::::SEGURIDAD:::::::::");
        logger.info("doFilterInternal ");
        final String authorizationHeader = request.getHeader("Authorization");
        logger.info("authorizationHeader: " + authorizationHeader);

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            logger.info("jwt: "+ jwt);

            try {
                if (jwtUtilService.validateToken(jwt)) {
                    username = jwtUtilService.extractUsername(jwt);
                    logger.info("username extraído del token: " + username);

                    Claims claims = jwtUtilService.extractAllClaims(jwt);

                    Object rolesClaim = claims.get("roles");
                    List<String> roles = new ArrayList<>();

                    if (rolesClaim instanceof String) {
                        roles.add((String) rolesClaim);
                        logger.info("Rol individual detectado: " + rolesClaim);
                    } else if (rolesClaim instanceof List<?>) {
                        for (Object item : (List<?>) rolesClaim) {
                            if (item instanceof String) {
                                roles.add((String) item);
                            }
                        }
                        logger.info("Lista de roles detectada: " + roles);
                    } else if (rolesClaim == null) {
                        logger.warn("La claim 'roles' no está presente en el token o es null.");
                    } else {
                        logger.warn("La claim 'roles' tiene un tipo inesperado: " + rolesClaim.getClass().getName());
                    }


                    List<SimpleGrantedAuthority> authorities = roles.stream()
                            .map(role -> "ROLE_" + role.toUpperCase())
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(username, null, authorities);

                        // PASO CLAVE: CREAMOS UN HashMap y le añadimos los detalles
                        // Obtener los detalles originales (sessionId, remoteAddress) como WebAuthenticationDetails
                        Object originalDetails = authenticationDetailsSource.buildDetails(request);

                        // Crear un HashMap para almacenar todos los detalles
                        Map<String, Object> customDetails = new HashMap<>();
                        customDetails.put("jwt", jwt); // <--- Nuestro JWT
                        customDetails.put("originalDetails", originalDetails); // <--- Los detalles originales

                        authenticationToken.setDetails(customDetails); // <--- Establecemos el mapa como detalles
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        logger.info("Autenticación establecida para el usuario: " + username + " con roles: " + authorities);
                    }
                } else {
                    logger.warn("Token JWT inválido o expirado. No se establecerá la autenticación.");
                }
            } catch (Exception e) {
                logger.error("Error al procesar el token JWT: " + e.getMessage(), e);
                throw new BadCredentialsException("Token inválido o expirado", e);
            }
        } else {
            logger.info("No se encontró encabezado de autorización Bearer o el formato es incorrecto.");
        }

        filterChain.doFilter(request, response);
    }
}