package mx.gob.imss.catalogos.controller;

 
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; 
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 

import mx.gob.imss.catalogos.dto.MediosContactoResponseDto;
import mx.gob.imss.catalogos.dto.SdcDelegacionDto;
import mx.gob.imss.catalogos.dto.SdcSubdelegacionDto;
import mx.gob.imss.catalogos.dto.SdcSubdelegacionFiltroDto;
import mx.gob.imss.catalogos.service.FolioService;
import mx.gob.imss.catalogos.service.MediosContactoService; 
import mx.gob.imss.catalogos.service.SdcDelegacionService;
import mx.gob.imss.catalogos.service.SdcSubdelegacionService;
import mx.gob.imss.catalogos.service.TipoDatosContadorService;
import jakarta.validation.Valid; 
 


@RestController
@CrossOrigin("*") 
@RequestMapping("/mssideimss-catalogos/v1")
public class CatalogosRestController {
	private final static Logger logger = LoggerFactory.getLogger(CatalogosRestController.class);
  
 

	@Autowired
	private SdcDelegacionService sdcDelegacionService;

	@Autowired
	private SdcSubdelegacionService sdcSubdelegacionService;

    @Autowired
    private MediosContactoService mediosContactoService; 


	@Autowired
    private FolioService folioService;

	@Autowired  
    private TipoDatosContadorService tipoDatosContadorService; 
	
 
    @GetMapping("/info")
	public ResponseEntity<List<String>> info() {
		logger.info("........................mssideimss-catalogos info..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-catalogos");
		list.add("20240927");
		list.add("Catálogos");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}


	@GetMapping("/list")
	public ResponseEntity<List<String>> list() {
		logger.info("........................mssideimss-catalogos list..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-catalogos");
		list.add("20240927");
		list.add("Catálogos");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}


 
 
	 

 


    @GetMapping("/mediosContacto/{rfc}")
    public ResponseEntity<MediosContactoResponseDto> getMediosContactoByRfc(@PathVariable String rfc) {
        logger.info("Recibiendo solicitud para /mediosContacto/{}", rfc);
        MediosContactoResponseDto response = mediosContactoService.recuperarMediosContactoPorRfc(rfc); // <-- CAMBIADO
        
        if (response != null && !response.getMedios().isEmpty()) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // O un código 200 con lista vacía
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


		
	/*
	 * Consulta todo el contenido de SdcDelegacion
	*/
	@PostMapping("/listDelegacion")
	public  ResponseEntity<List<SdcDelegacionDto>> listDelegacion() {
		logger.info("/listDelegacion"); 
		List<SdcDelegacionDto> sdcDelegacionDtos = new ArrayList<SdcDelegacionDto>();  
		sdcDelegacionDtos = sdcDelegacionService.findAllSdcDelegacion();
		return new ResponseEntity<List<SdcDelegacionDto>>(sdcDelegacionDtos, HttpStatus.OK);
	} 




		
	/*
	 * Consulta todo el contenido de SdcDelegacion
	*/
	@PostMapping("/listSubdelegacion")
	public  ResponseEntity<List<SdcSubdelegacionDto>> listSubdelegacion(@Valid @RequestBody  SdcSubdelegacionFiltroDto sdcSubdelegacionFiltroDto) {
		logger.info("/listSubdelegacion"); 
		List<SdcSubdelegacionDto> sdcSubdelegacionDtos = new ArrayList<SdcSubdelegacionDto>();  
			sdcSubdelegacionDtos = sdcSubdelegacionService.findAllSdcSubdelegacion(sdcSubdelegacionFiltroDto);
		return new ResponseEntity<List<SdcSubdelegacionDto>>(sdcSubdelegacionDtos, HttpStatus.OK);
	} 

 
    /**
     * Método para obtener un folio de solicitud  .
     * Genera y devuelve un nuevo folio.
     * @return ResponseEntity con el nuevo folio generado.
     */
    @GetMapping("/getNuevoFolioSolicitud")
    public ResponseEntity<String> getNuevoFolioSolicitud() {
        logger.info("Recibiendo solicitud para generar un nuevo folio de solicitud.");

        String nuevoFolio = folioService.generarNuevoFolioSolicitud();

        if (nuevoFolio != null && !nuevoFolio.isEmpty()) {
            logger.info("Nuevo folio generado: {}", nuevoFolio);
            return new ResponseEntity<>(nuevoFolio, HttpStatus.OK);
        } else {
            logger.error("Error al generar un nuevo folio.");
            return new ResponseEntity<>("No se pudo generar un nuevo folio de solicitud.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


	/**
     * Método para obtener la lista de tipos de datos de contador.
     * @return ResponseEntity con la lista de tipos de datos de contador.
     */
    @GetMapping("/tiposDatosContador")
    public ResponseEntity<List<String>> getTiposDatosContador() {
        logger.info("Recibiendo solicitud para obtener tipos de datos de contador.");
        List<String> tipos = tipoDatosContadorService.getTiposDatosContador();
        return new ResponseEntity<>(tipos, HttpStatus.OK);
    }
 

}