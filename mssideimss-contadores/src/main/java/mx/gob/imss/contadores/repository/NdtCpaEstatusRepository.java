package mx.gob.imss.contadores.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.contadores.entity.NdtCpaEstatus;

@Repository
public interface NdtCpaEstatusRepository extends JpaRepository<NdtCpaEstatus, Long> {
    
}
