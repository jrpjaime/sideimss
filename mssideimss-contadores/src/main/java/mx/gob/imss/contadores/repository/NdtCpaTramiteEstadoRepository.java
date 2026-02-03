package mx.gob.imss.contadores.repository;

import mx.gob.imss.contadores.entity.NdtCpaTramiteEstado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NdtCpaTramiteEstadoRepository extends JpaRepository<NdtCpaTramiteEstado, Long> {

    /**
     * Busca los estados de un trámite específico ordenados por ID descendente.
     * Útil si necesitas consultar el historial de estados de un trámite.
     */
    List<NdtCpaTramiteEstado> findByCveIdCpaTramiteOrderByCveIdCpaTramiteEstadoDesc(Long cveIdCpaTramite);
}
