package mx.gob.imss.documentos.dto;

import lombok.Builder;  
import lombok.Data;
import org.springframework.core.io.Resource;

@Data
@Builder 
public class DownloadFileDto {
    private Resource resource;
    private String filename;
    private String mediaType; 
}