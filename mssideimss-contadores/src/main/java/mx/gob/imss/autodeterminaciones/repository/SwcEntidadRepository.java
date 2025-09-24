package mx.gob.imss.autodeterminaciones.repository;

import mx.gob.imss.autodeterminaciones.entity.SwcEntidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwcEntidadRepository extends JpaRepository<SwcEntidad, Integer> {
}
