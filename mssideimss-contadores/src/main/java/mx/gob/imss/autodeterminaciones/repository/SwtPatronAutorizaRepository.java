package mx.gob.imss.autodeterminaciones.repository;

import mx.gob.imss.autodeterminaciones.entity.SwtPatronAutoriza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwtPatronAutorizaRepository extends JpaRepository<SwtPatronAutoriza, Long> {
}
