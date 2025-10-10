package mx.gob.imss.documentos.service;



import java.io.IOException;
import java.util.Date;

import org.springframework.core.io.Resource;

import mx.gob.imss.documentos.dto.DocumentoIndividualDto;
import mx.gob.imss.documentos.dto.DownloadFileDto;

public interface  CargaDocumentoService {  
public DocumentoIndividualDto cargaDocumentoHadoop(DocumentoIndividualDto documentoIndividualVO ) throws Exception;
public String saveDocumentoHdfs(byte[] file,  String pathHdfs, String namefile ) throws Exception;
public Resource readFileHdfs(String pathHdfs ) throws IOException ;    
public DownloadFileDto downloadDocumentoHdfs(String fullHdfsPathBase64) throws IOException, IllegalArgumentException;
public void deleteDocumentoHdfs(String fullHdfsPathBase64) throws IOException, IllegalArgumentException;
}
