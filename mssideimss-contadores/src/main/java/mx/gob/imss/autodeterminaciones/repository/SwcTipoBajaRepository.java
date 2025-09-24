package mx.gob.imss.autodeterminaciones.repository;

import mx.gob.imss.autodeterminaciones.entity.SwcTipoBaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwcTipoBajaRepository extends JpaRepository<SwcTipoBaja, Integer> {
}
