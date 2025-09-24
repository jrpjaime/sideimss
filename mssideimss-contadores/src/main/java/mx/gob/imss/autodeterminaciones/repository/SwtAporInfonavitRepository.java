package mx.gob.imss.autodeterminaciones.repository;

import mx.gob.imss.autodeterminaciones.entity.SwtAporInfonavit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwtAporInfonavitRepository extends JpaRepository<SwtAporInfonavit, Long> {
}
