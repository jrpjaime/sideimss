package mx.gob.imss.catalogos.dto;


import lombok.Data;

@Data
public class TipoDatoContadorDto {
    

    private String cveIdTipoDato;
    private String desTipoDatos;


    public TipoDatoContadorDto(String cveIdTipoDato, String desTipoDatos) {
        this.cveIdTipoDato = cveIdTipoDato;
        this.desTipoDatos = desTipoDatos;
    }

}
