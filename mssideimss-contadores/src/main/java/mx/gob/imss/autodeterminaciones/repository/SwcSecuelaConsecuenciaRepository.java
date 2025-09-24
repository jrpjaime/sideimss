package mx.gob.imss.autodeterminaciones.repository;

import mx.gob.imss.autodeterminaciones.entity.SwcSecuelaConsecuencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwcSecuelaConsecuenciaRepository extends JpaRepository<SwcSecuelaConsecuencia, Integer> {
}
