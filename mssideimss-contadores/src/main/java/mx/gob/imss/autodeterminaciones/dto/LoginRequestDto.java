package mx.gob.imss.autodeterminaciones.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String user;
    private String password;

    public LoginRequestDto() {
    }

    public LoginRequestDto(String user, String password) {
        this.user = user;
        this.password = password;
    }
}
