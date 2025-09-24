package mx.gob.imss.autodeterminaciones.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.autodeterminaciones.entity.SwtCredito;

@Repository
public interface SwtCreditoRepository extends JpaRepository<SwtCredito, Long> {
}
