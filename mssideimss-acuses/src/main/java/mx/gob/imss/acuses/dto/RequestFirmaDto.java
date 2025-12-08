package mx.gob.imss.acuses.dto;

import lombok.Data;


@Data
public class RequestFirmaDto {
    

    private String rfcUsuario;
    private String desFolio;
	private String desCurp;
    private String nombreCompleto;
    private String acto;

}
