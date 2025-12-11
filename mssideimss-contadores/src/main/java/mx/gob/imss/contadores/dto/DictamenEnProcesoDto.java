package mx.gob.imss.contadores.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DictamenEnProcesoDto {

    private Long idPatron;          // PD.CVE_ID_PATRON_DICTAMEN
    private String rfc;             // PD.DES_RFC
    private String razonSocial;     // PD.DES_NOMBRE_RAZON_SOCIAL
    private String ejercicio;       // EF.DES_EJER_FISCAL
    private String desEstadoDictamen; // E.DES_ESTADO_DICTAMEN
    private Date fecRegistroAlta;   // PD.FEC_REGISTRO_ALTA
    private String curpCpa;         // CPA.CURP
    private Integer registroImss;   // CPA.NUM_REGISTRO_CPA

  
    
}
