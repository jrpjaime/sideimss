package mx.gob.imss.catalogos.service;

import java.util.List; 
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import mx.gob.imss.catalogos.dto.DitPatronGeneralDto;
import mx.gob.imss.catalogos.dto.DitPatronGeneralFiltroDto; 

public interface  DitPatronGeneralService { 

	public DitPatronGeneralDto findDitPatronGeneralByIdPatronGeneral(Integer idPatronGeneral);

	public boolean existsByIdPatronGeneral(Integer idPatronGeneral);

	public List<DitPatronGeneralDto> findAllDitPatronGeneral();

	public Page<DitPatronGeneralDto> findAllDitPatronGeneral(Pageable pageable);

	public Page<DitPatronGeneralDto> findAllPageableDitPatronGeneral(DitPatronGeneralFiltroDto ditPatronGeneralFiltroDtoP, Pageable pageable);

	public List<DitPatronGeneralDto> findDitPatronGeneralByDenominacionRazonSocial(String denominacionRazonSocial);

	public List<DitPatronGeneralDto> findDitPatronGeneralByRfc(String rfc);

	public List<DitPatronGeneralDto> findDitPatronGeneralByRegistroPatronal(String registroPatronal);
  
}
