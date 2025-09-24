package mx.gob.imss.autodeterminaciones.controller;

 
import java.util.ArrayList;
import java.util.List; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest; 
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin; 
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

 
import mx.gob.imss.autodeterminaciones.dto.SwtAseguradoDto;
import mx.gob.imss.autodeterminaciones.dto.SwtMovimientoDto; 
import mx.gob.imss.autodeterminaciones.service.SwtAseguradoService;

import org.springframework.data.domain.Page;
import jakarta.validation.Valid;

@Controller
@CrossOrigin("*") 
@RequestMapping("/mssideimss-autodeterminaciones/v1")
public class AutodeterminacionRestController {
 
	private final static Logger logger = LoggerFactory.getLogger(AutodeterminacionRestController.class);
	

	@Autowired
	SwtAseguradoService swtAseguradoService;

	AutodeterminacionRestController( ) { 
    }
 

    	@GetMapping("/info")
	public ResponseEntity<List<String>> info() {
		logger.info("........................mssideimss-autodeterminaciones..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-autodeterminaciones");
		list.add("20250824");
		list.add("Autodeterminaciones");
		return new ResponseEntity<>(list, HttpStatus.OK);
	}


	@GetMapping("/list1")
	public ResponseEntity<List<String>> list1() {
		logger.info("........................mssideimss-autodeterminaciones list1..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-autodeterminaciones");
		list.add("20240929");
		list.add("Autodeterminaciones");
		return new ResponseEntity<>(list, HttpStatus.OK);
	}


 
	@PostMapping(value = "/list")
	public ResponseEntity<List<String>> list() {
		logger.info("........................mssideimss-autodeterminaciones list..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-autodeterminaciones");
		list.add("20240929");
		list.add("Autodeterminaciones");
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

 


	/**
     * Endpoint POST para obtener una lista paginada de trabajadores (asegurados).
     *
     * Este método recibe un objeto `SwtAseguradoDto` en el cuerpo de la solicitud, el cual debe contener los parámetros de paginación (número de página y tamaño) y criterios de búsqueda para filtrar la lista de asegurados.
     *
     * La lista de asegurados es ordenada por el número de seguridad social (numNss) y se devuelve dentro de un objeto `Page` de Spring Data, que incluye los metadatos de paginación.
     *
     * @param swtAseguradoDto Objeto DTO que contiene los parámetros de paginación (`page`, `size`) y posibles criterios de filtrado para los asegurados.
     * @return {@code ResponseEntity<Page<SwtAseguradoDto>>} Una respuesta HTTP 200 OK con un objeto `Page` que contiene una sublista de `SwtAseguradoDto` que representa los trabajadores en la página solicitada, junto con información de paginación (total de elementos, total de páginas, etc.).
     */

    @PostMapping(value = "/listPaginatedTrabajadores") 
    public  ResponseEntity<Page< SwtAseguradoDto>> listPaginatedTrabajadores (@Valid @RequestBody SwtAseguradoDto swtAseguradoDto) {
        logger.info("\r\n ");
        logger.info("Inicia /listPaginatedTrabajadores");
        String orders = "numNss";
        Page<SwtAseguradoDto> swtAseguradoDtos =  swtAseguradoService.findAllPageableSwtAsegurado(swtAseguradoDto, PageRequest.of(swtAseguradoDto.getPage(), swtAseguradoDto.getSize(), org.springframework.data.domain.Sort.by(orders)));
        logger.info("Termina /listPaginatedTrabajadores");
        logger.info("\r\n ");
        return new ResponseEntity<Page<SwtAseguradoDto>>(swtAseguradoDtos, HttpStatus.OK);
    }


	/**
	 * Maneja la solicitud POST para recuperar una lista paginada de movimientos.
	 * Este método recupera una página de objetos {@link SwtMovimientoDto} basándose en los criterios de paginación y ordenamiento proporcionados, y los devuelve envueltos en un {@link ResponseEntity}.
	 *
	 * @param swtAseguradoDto Un objeto de tipo {@link SwtMovimientoDto} que contiene detalles de paginación (número de página, tamaño) y  otros criterios para filtrar movimientos.
	 * @return Un {@link ResponseEntity} que contiene una {@link Page} de objetos {@link SwtMovimientoDto} y un estado HTTP OK.
	 */
	@PostMapping(value = "/listPaginatedMovimientos") 
	public  ResponseEntity<Page< SwtMovimientoDto>> listPaginatedMovimientos (@RequestBody SwtMovimientoDto swtAseguradoDto) {
		logger.info("\r\n "); 
		logger.info("Inicia /listPaginatedMovimientos"); 
		String orders = "numNss";  
		Page<SwtMovimientoDto> swtAseguradoDtos =  swtAseguradoService.findAllPageableSwtMovimientos(swtAseguradoDto, PageRequest.of(swtAseguradoDto.getPage(), swtAseguradoDto.getSize(), org.springframework.data.domain.Sort.by(orders)));
		logger.info("Termina /listPaginatedMovimientos"); 
		logger.info("\r\n "); 
		return new ResponseEntity<Page<SwtMovimientoDto>>(swtAseguradoDtos, HttpStatus.OK);
	} 






}