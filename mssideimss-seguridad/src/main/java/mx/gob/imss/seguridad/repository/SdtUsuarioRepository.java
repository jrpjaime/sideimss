package mx.gob.imss.seguridad.repository;

import java.util.List; 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import mx.gob.imss.seguridad.entity.SdtUsuario;

 
@Repository("sdtUsuarioRepository")
public interface  SdtUsuarioRepository extends JpaRepository<SdtUsuario, Integer>  { 

	public SdtUsuario findSdtUsuarioByIdUsuario(Integer idUsuario);

	public boolean existsByIdUsuario(Integer idUsuario);

	public List<SdtUsuario> findSdtUsuarioByNomNombre(String nomNombre);

	public List<SdtUsuario> findSdtUsuarioByNomApellidoPaterno(String nomApellidoPaterno);

	public List<SdtUsuario> findSdtUsuarioByNomApellidoMaterno(String nomApellidoMaterno);

	public List<SdtUsuario> findSdtUsuarioByDesUsuario(String desUsuario);

	public List<SdtUsuario> findSdtUsuarioByDesPassword(String desPassword);

	public List<SdtUsuario>  findSdtUsuarioByRefRfc(String refRfc);
	

}
