package mx.gob.imss.autodeterminaciones.dto;

import lombok.Data;

@Data
public class SwtMovimientoDto {

    private Integer page; 
	private Integer size; 
	private String order; 
	private boolean asc; 

    private String idMovimiento;
    private String idAsegurado;

    private String cveRegistroPatronal;
    private String numNss;
     private String refRfc; 

    private Integer idTipoMovimiento;
    private String fecRegistroAlta;
    private String fecRegistroActualizado;
    private String fecRegistroBaja;
    private String fecInicio;
    private Integer numDias;
    private Integer indArt33;
    private String salSalarioDiarioIntegrado;
    private Integer idTipoBaja;
    private Integer idTipoDescuento;
    private String refFolio;
    private String numCredito;
    private Integer canValorDescuento;
    private Integer indTablaDisminPorcen;
    private Integer tipIncidencia;
    private Integer porPorcentajeIncapacidad;
    private String fecInicioDescuento;
    private String cveMunicipio;
    private String fecTermino;
    private String salSalarioIvCv;
    private String salSalarioOtrosSeguros;
    private Integer idRamoSeguro;
    private Integer idTipoRiesgo;
    private Integer idSecuelaConsecuencia;
    private Integer idControlIncapacidad;
    private String fecInicioIncapacidad;
    private String refFolioCertificadoInc;
    private String fecSuspension;
    private Integer idTipoTrabajador;
    private Integer idJornada;
    private Integer idTipoPension;
}
