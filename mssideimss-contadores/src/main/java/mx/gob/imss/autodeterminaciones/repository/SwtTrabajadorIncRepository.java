package mx.gob.imss.autodeterminaciones.repository;

import mx.gob.imss.autodeterminaciones.entity.SwtTrabajadorInc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwtTrabajadorIncRepository extends JpaRepository<SwtTrabajadorInc, Long> {
}
