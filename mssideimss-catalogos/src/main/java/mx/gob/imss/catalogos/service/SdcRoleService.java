package mx.gob.imss.catalogos.service;

import java.util.List; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mx.gob.imss.catalogos.dto.SdcRoleDto; 

public interface  SdcRoleService { 

	public SdcRoleDto findSdcRoleByIdRole(Integer idRole);

	public boolean existsByIdRole(Integer idRole);

	public List<SdcRoleDto> findAllSdcRole();

	public Page<SdcRoleDto> findAllSdcRole(Pageable pageable);

	public List<SdcRoleDto> findSdcRoleByDesRole(String desRole);

	public SdcRoleDto saveSdcRole(SdcRoleDto sdcRoleDto);

	public void deleteSdcRole(SdcRoleDto sdcRoleDto);

}
