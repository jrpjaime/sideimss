package mx.gob.imss.contadores.dto;


import lombok.Data;

@Data
public class ColegioContadorDto {
    private String rfcColegio;
    private String razonSocial;

    public ColegioContadorDto() {
    }

    public ColegioContadorDto(String rfcColegio, String razonSocial) {
        this.rfcColegio = rfcColegio;
        this.razonSocial = razonSocial;
    }
    
}
