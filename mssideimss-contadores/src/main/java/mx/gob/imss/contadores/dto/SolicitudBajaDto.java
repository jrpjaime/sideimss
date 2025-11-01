package mx.gob.imss.contadores.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO principal que concentra todos los datos del formulario de Baja de Contador.
 * Requiere la librería Lombok para las anotaciones @Data, @NoArgsConstructor, @AllArgsConstructor.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudBajaDto {
    
    // Campo Folio
    private String folioSolicitud;
    
    // Secciones del Formulario
    private DatosPersonalesDto datosPersonalesDto;
    private DomicilioFiscalDto domicilioFiscalDto;
    private DatosContactoDto datosContactoDto;
    
    // Campo "Motivo de baja"
    private String motivoBaja;
}
