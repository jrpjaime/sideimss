package mx.gob.imss.contadores.repository;
import mx.gob.imss.contadores.entity.NdtR1FormaContacto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NdtR1FormaContactoRepository extends JpaRepository<NdtR1FormaContacto, Long> {
}
