package mx.gob.imss.contadores.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import mx.gob.imss.contadores.entity.NdtR2Despacho;

@Repository
public interface NdtR2DespachoRepository extends JpaRepository<NdtR2Despacho, Long> {
    @Query("SELECT r FROM NdtR2Despacho r WHERE r.cveIdCpa = :cveIdCpa AND r.fecRegistroBaja IS NULL")
    Optional<NdtR2Despacho> findRegistroActivoByCpa(@Param("cveIdCpa") Long cveIdCpa); 
}
