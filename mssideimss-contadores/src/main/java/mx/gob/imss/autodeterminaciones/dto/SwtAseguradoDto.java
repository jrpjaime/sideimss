package mx.gob.imss.autodeterminaciones.dto;


import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class SwtAseguradoDto {

	private Integer page; 
	private Integer size; 
	private String order; 
	private boolean asc; 

    private Long idAsegurado;
     @NotBlank(message = "El campo 'Registro Patronal' es requerido y no puede estar vacío.")
    private String cveRegistroPatronal;
    private String numNss;
    private String refCurp;
    private String refRfc; 
    
    private String nomNombre;
    private String nomPrimerApellido;
    private String nomSegundoApellido;
    private LocalDate fecAlt;
    private LocalDate fecBaj;
    private String cveUbcacion;
    private String cveMunicipio;
    private Integer idTipoTrabajador;
    private Integer idJornada;
    private Integer idTipoPension;
    private Integer idTipoSalario;
    private Integer idEntidad;
    private Integer idSexo;
    private String refCppTrab;
    private LocalDate fecNac;
    private String numUmfTrab;
    private String refOcupa;
    private String numJorHoras;
    private BigDecimal salarioDiarioIntegrado;
    private String numCredito;
    private LocalDate fecInicioDescuento;
    private Integer canValorDescuento;
    private LocalDate fecSuspension;
    private LocalDate fecRegistroAlta;
    private LocalDate fecRegistroActualizado;
    private LocalDate fecRegistroBaja;
    private Integer idTipoDescuento;
    private Integer indTablaDisminucion;
  
    private Integer idPatron;
}
