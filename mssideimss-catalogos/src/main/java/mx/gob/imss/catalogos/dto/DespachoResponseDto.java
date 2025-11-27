package mx.gob.imss.catalogos.dto;


import lombok.Data;
 
@Data
public class DespachoResponseDto {

    private String rfcDespacho;
    private String nombreRazonSocial;
    private String cveIdTipoSociedad;
    private String desTipoSociedad;
    private String cveIdCargoContador;
    private String desCargoContador;
    private String telefonoFijo;
    private String tieneTrabajadores;
    private String numeroTrabajadores;
}
