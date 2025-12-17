package mx.gob.imss.contadores.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import mx.gob.imss.contadores.entity.NdtColegio;

@Repository
public interface NdtColegioRepository extends JpaRepository<NdtColegio, Long> {
    // Buscar colegio activo por RFC del usuario
    Optional<NdtColegio> findByCveIdUsuarioAndFecRegistroBajaIsNull(String cveIdUsuario);
}
