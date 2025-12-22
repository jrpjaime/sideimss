package mx.gob.imss.contadores.service;
 
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import mx.gob.imss.contadores.entity.NdtPlantillaDato;
import mx.gob.imss.contadores.repository.NdtPlantillaDatoRepository;

@Service
@RequiredArgsConstructor
public class PlantillaPersistenceService {

    private final NdtPlantillaDatoRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(rollbackFor = Exception.class)
    public NdtPlantillaDato actualizarYGuardar(NdtPlantillaDato entity, String sello, String cadenaFinal, String jsonOriginal) throws Exception {
        ObjectNode rootNode = (ObjectNode) objectMapper.readTree(jsonOriginal);
        
        rootNode.put("selloDigitalIMSS", sello);
        rootNode.put("cadenaOriginal", cadenaFinal);
        
        entity.setDesDatos(objectMapper.writeValueAsString(rootNode));
        
        // El rollback se dispara automáticamente si falla el save (ej. secuencias)
        return repository.save(entity);
    }
}