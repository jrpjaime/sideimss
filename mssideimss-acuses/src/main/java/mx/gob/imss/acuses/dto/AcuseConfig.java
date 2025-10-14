package mx.gob.imss.acuses.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class AcuseConfig {

    private String nomDocumento;
    private String desVersion; // Esto debería apuntar al path del .jasper sin la extensión
    private Map<String, String> imagePaths; // Para las rutas de imágenes y otros parámetros de texto
    
    public AcuseConfig() {
        this.imagePaths = new HashMap<>();
    }

    public void addImagePath(String key, String path) {
        this.imagePaths.put(key, path);
    }

}
