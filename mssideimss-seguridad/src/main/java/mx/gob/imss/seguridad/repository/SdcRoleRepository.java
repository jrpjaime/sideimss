package mx.gob.imss.seguridad.repository;

import java.util.List; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mx.gob.imss.seguridad.entity.SdcRole;

 
@Repository("sdcRoleRepository")
public interface  SdcRoleRepository extends JpaRepository<SdcRole, Integer>  { 

	public SdcRole findSdcRoleByIdRole(Integer idRole);

	public boolean existsByIdRole(Integer idRole);

	public List<SdcRole> findSdcRoleByDesRole(String desRole);

}
