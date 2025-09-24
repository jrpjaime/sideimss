package mx.gob.imss.seguridad.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    String user;
    String password;
    String tipoAuth;
}
