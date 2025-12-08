package mx.gob.imss.catalogos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.UUID;
  
@Service("folioService")
public class  FolioServiceImpl implements FolioService  { 
	private final static Logger logger = LoggerFactory.getLogger(FolioServiceImpl.class);



public String generarNuevoFolioSolicitud() {
    // Genera un string largo único, ej: "550e8400-e29b-41d4-a716-446655440000"
    String uniqueID = UUID.randomUUID().toString();
    
    return uniqueID;
}

}
