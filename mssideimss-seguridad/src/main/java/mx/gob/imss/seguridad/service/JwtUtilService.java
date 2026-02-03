package mx.gob.imss.seguridad.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import mx.gob.imss.seguridad.dto.UsuarioDto; 
import io.jsonwebtoken.io.Decoders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtUtilService {

    @Value("${jwt.secret}")
    private String JWT_SECRET_KEY;

  
    private static final long JWT_TIME_VALIDITY = 1000 * 60 * 60; // 60 minutos
    private static final long JWT_TIME_REFRESH_VALIDATE = 1000 * 60 * 60 * 24; // 24 horas

    // Genera el token JWT
    public String generateToken(UserDetails userDetails, List<String> roles, UsuarioDto  usuarioDto) {
        var claims = new HashMap<String, Object>();
        claims.put("rfc", usuarioDto.getRfc());
        claims.put("curp", usuarioDto.getCurp());
        claims.put("nombre", usuarioDto.getNombre());
        claims.put("primerApellido", usuarioDto.getPrimerApellido());
        claims.put("segundoApellido", usuarioDto.getSegundoApellido()); 
        claims.put("numeroRegistroImss", usuarioDto.getNumeroRegistroImss());
        
        claims.put("roles", roles);  
        claims.put("indBaja", usuarioDto.isIndBaja());
        claims.put("cveIdEstadoCpa", usuarioDto.getCveIdEstadoCpa());
 
        return Jwts.builder()
                .claims(claims) // método para establecer los claims
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TIME_VALIDITY))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    // Genera el refresh token
    public String generateRefreshToken(UserDetails userDetails, List<String> roles, UsuarioDto  usuarioDto ) {
        var claims = new HashMap<String, Object>();
        claims.put("rfc", usuarioDto.getRfc());
        claims.put("curp", usuarioDto.getCurp());
        claims.put("nombre", usuarioDto.getNombre());
        claims.put("primerApellido", usuarioDto.getPrimerApellido());
        claims.put("segundoApellido", usuarioDto.getSegundoApellido());
        claims.put("numeroRegistroImss", usuarioDto.getNumeroRegistroImss());
        claims.put("roles", roles);
        claims.put("indBaja", usuarioDto.isIndBaja());
        claims.put("cveIdEstadoCpa", usuarioDto.getCveIdEstadoCpa());
 
        return Jwts.builder()
                .claims(claims) // Nuevo método para establecer los claims
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TIME_REFRESH_VALIDATE))
                .signWith(getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }

    // Método para obtener la clave de firma segura
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Valida el token
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractClaim(token, Claims::getSubject);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Extrae los claims de un token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Extrae todos los claims del token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignInKey()) // Usa verifyWith en lugar de setSigningKey y parseClaimsJws().getBody()
                .build()
                .parseSignedClaims(token) // método para parsear claims
                .getPayload(); // Obtiene el payload directamente
    }

    // Verifica si el token ha expirado
    private Boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // Extrae el nombre de usuario del token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @SuppressWarnings("unchecked") // Para suprimir la advertencia de tipo no verificado
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("roles"));
    }
}