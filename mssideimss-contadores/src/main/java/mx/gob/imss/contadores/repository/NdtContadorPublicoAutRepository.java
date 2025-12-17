package mx.gob.imss.contadores.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.contadores.entity.NdtContadorPublicoAut;

@Repository
public interface NdtContadorPublicoAutRepository extends JpaRepository<NdtContadorPublicoAut, Long> {
 

    Optional<NdtContadorPublicoAut> findByCurp(String curp);
}
