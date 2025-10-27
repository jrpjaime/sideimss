package mx.gob.imss.contadores.dto;

import lombok.Data;

@Data
public class SelloResponseDto {
    private String sello;
    private Integer codigo; // 1=Error 0=correcto
    private String mensaje;

    public SelloResponseDto(String sello, Integer codigo, String mensaje) {
        this.sello = sello;
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public SelloResponseDto() {
    }
}
