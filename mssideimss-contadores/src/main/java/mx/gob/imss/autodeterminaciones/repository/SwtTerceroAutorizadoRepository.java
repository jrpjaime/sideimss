package mx.gob.imss.autodeterminaciones.repository;

import mx.gob.imss.autodeterminaciones.entity.SwtTerceroAutorizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwtTerceroAutorizadoRepository extends JpaRepository<SwtTerceroAutorizado, Long> {
}
