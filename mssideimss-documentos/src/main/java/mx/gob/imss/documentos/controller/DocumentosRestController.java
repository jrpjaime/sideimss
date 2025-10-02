package mx.gob.imss.documentos.controller;

 
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.RequestMapping;

 


@Controller
@CrossOrigin("*") 
@RequestMapping("/mssideimss-documentos/v1")
public class DocumentosRestController {
	private final static Logger logger = LoggerFactory.getLogger(DocumentosRestController.class);
  
 
    @GetMapping("/info")
	public ResponseEntity<List<String>> info() {
		logger.info("........................mssideimss-documentos info..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-documentos");
		list.add("20251002");
		list.add("Documentos");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}


	@GetMapping("/list")
	public ResponseEntity<List<String>> list() {
		logger.info("........................mssideimss-documentos list..............................");
		List<String> list = new ArrayList<String>();
		list.add("mssideimss-documentos");
		list.add("20251002");
		list.add("Documentos");
		return new ResponseEntity<List<String>>(list, HttpStatus.OK);
	}


 
 
	 

 
 

 
 
 

}