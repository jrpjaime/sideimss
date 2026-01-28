package mx.gob.imss.seguridad.dto;

import java.time.LocalDate;

public interface  DITPersonaProjection {
    String getRfc();
    String getNomNombre();
    String getNomPrimerApellido();
    String getNomSegundoApellido();
    String getCurp();
    String getNumRegistroCpa();
    String getCveIdCpa();
    LocalDate getFecRegistroBaja(); 
}
