package mx.gob.imss.autodeterminaciones.dto;
import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;
    private String refreshToken;

    public LoginResponseDto() {
    }
}
