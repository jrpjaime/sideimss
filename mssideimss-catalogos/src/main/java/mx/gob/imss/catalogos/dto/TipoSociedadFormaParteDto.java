package mx.gob.imss.catalogos.dto;


import lombok.Data;

@Data
public class TipoSociedadFormaParteDto {
    private String cveIdTipoSociedad;
    private String desTipoSociedad;

    public TipoSociedadFormaParteDto() {
    }

    public TipoSociedadFormaParteDto(String cveIdTipoSociedad, String desTipoSociedad) {
        this.cveIdTipoSociedad = cveIdTipoSociedad;
        this.desTipoSociedad = desTipoSociedad;
    }
}
