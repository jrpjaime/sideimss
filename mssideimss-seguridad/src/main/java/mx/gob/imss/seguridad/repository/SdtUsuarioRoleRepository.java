package mx.gob.imss.seguridad.repository;

import java.util.List; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mx.gob.imss.seguridad.entity.SdtUsuarioRole;
import mx.gob.imss.seguridad.entity.SdtUsuario;
import mx.gob.imss.seguridad.entity.SdcRole;
 
@Repository("sdtUsuarioRoleRepository")
public interface  SdtUsuarioRoleRepository extends JpaRepository<SdtUsuarioRole, Integer>  { 

	public SdtUsuarioRole findSdtUsuarioRoleByIdUsuarioRole(Integer idUsuarioRole);

	public boolean existsByIdUsuarioRole(Integer idUsuarioRole);

	public List<SdtUsuarioRole> findSdtUsuarioRoleBySdtUsuario(SdtUsuario sdtUsuario);

	public List<SdtUsuarioRole> findSdtUsuarioRoleBySdcRole(SdcRole sdcRole);

}
