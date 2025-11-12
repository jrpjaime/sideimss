package mx.gob.imss.catalogos.dto;

import lombok.Data;

@Data
public class CargoContadorDto {
    private String cveIdCargoContador;
    private String desCargoContador;

    public CargoContadorDto() {
    }

    public CargoContadorDto(String cveIdCargoContador, String desCargoContador) {
        this.cveIdCargoContador = cveIdCargoContador;
        this.desCargoContador = desCargoContador;
    }
}
