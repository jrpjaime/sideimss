package mx.gob.imss.autodeterminaciones.repository;

import mx.gob.imss.autodeterminaciones.entity.SwcTipoSalario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SwcTipoSalarioRepository extends JpaRepository<SwcTipoSalario, Integer> {
}
