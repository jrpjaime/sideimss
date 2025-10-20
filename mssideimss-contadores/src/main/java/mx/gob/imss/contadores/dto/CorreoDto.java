package mx.gob.imss.contadores.dto;

import java.util.List;

import lombok.Data;

@Data
public class CorreoDto {
	private List<String> correoPara;
	private String asunto;
	private String remitente;
	private String cuerpoCorreo;

    
}
