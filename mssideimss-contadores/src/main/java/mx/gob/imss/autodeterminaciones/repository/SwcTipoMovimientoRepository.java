package mx.gob.imss.autodeterminaciones.repository;

import mx.gob.imss.autodeterminaciones.entity.SwcTipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwcTipoMovimientoRepository extends JpaRepository<SwcTipoMovimiento, Integer> {
}
