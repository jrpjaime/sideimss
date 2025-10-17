package mx.gob.imss.acuses.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.acuses.model.NdtPlantillaDato;
 

@Repository
public interface NdtPlantillaDatoRepository extends JpaRepository<NdtPlantillaDato, Long> {
    
}




 