package mx.gob.imss.documentos.service;



import java.io.IOException;
import java.util.Date;

import org.springframework.core.io.Resource;

import mx.gob.imss.documentos.dto.DocumentoIndividualVO;

public interface  CargaDocumentoService {  
public DocumentoIndividualVO cargaDocumentoHadoop(DocumentoIndividualVO documentoIndividualVO ) throws Exception;
public String saveDocumentoHdfs(byte[] file,  String pathHdfs, String namefile ) throws Exception;
public Resource readFileHdfs(String pathHdfs ) throws IOException ;    
}
