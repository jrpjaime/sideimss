package mx.gob.imss.autodeterminaciones.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.autodeterminaciones.entity.DicDelegacion;

@Repository
public interface DicDelegacionRepository extends JpaRepository<DicDelegacion, Integer> {
}
