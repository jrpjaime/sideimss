package mx.gob.imss.seguridad.dto;

import lombok.Data;

@Data
public class UsuarioDto {

    private String rfc;
    private String curp;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String numeroRegistroImss;
    private String numRegistroCpa;
    private String cveIdCpa;
    private boolean indBaja; 
    
}
