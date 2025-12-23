package mx.gob.imss.contadores.dto;

import lombok.Data;

@Data  
public class ArchivoAdjuntoDto {
    private String nombreArchivo;
    private String contenidoB64; // El contenido del PDF en Base64
}