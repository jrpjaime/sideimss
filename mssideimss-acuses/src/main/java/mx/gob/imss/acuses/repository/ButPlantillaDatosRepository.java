package mx.gob.imss.bi.repository;

 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.bi.model.ButPlantillaDato; 

 
@Repository("butPlantillaDatosRepository")
public interface  ButPlantillaDatosRepository extends JpaRepository<ButPlantillaDato, Long>  { 

	public ButPlantillaDato findButPlantillaDatosByCveIdPlantillaDatos(Long cveIdPlantillaDatos);

	public boolean existsByCveIdPlantillaDatos(Long cveIdPlantillaDatos);

 

}
