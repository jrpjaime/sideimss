package mx.gob.imss.catalogos.repository;

import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mx.gob.imss.catalogos.entity.DitPatronGeneral;

/**
* @author: LGONZALEZ
*/
@Repository("ditPatronGeneralRepository")
public interface  DitPatronGeneralRepository extends JpaRepository<DitPatronGeneral, Integer>  { 

	public DitPatronGeneral findDitPatronGeneralByIdPatronGeneral(Integer idPatronGeneral);

	public boolean existsByIdPatronGeneral(Integer idPatronGeneral);

	public List<DitPatronGeneral> findDitPatronGeneralByDenominacionRazonSocial(String denominacionRazonSocial);

	public List<DitPatronGeneral> findDitPatronGeneralByRfc(String rfc);

	public List<DitPatronGeneral> findDitPatronGeneralByRegistroPatronal(String registroPatronal);

}
