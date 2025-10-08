package mx.gob.imss.acuses.repository;

 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.acuses.model.PlantillaDato;

 

 
@Repository("plantillaDatosRepository")
public interface  PlantillaDatosRepository extends JpaRepository<PlantillaDato, Long>  { 

	public PlantillaDato findPlantillaDatosByCveIdPlantillaDatos(Long cveIdPlantillaDatos);

	public boolean existsByCveIdPlantillaDatos(Long cveIdPlantillaDatos);

 

}
