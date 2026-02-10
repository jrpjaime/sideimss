package mx.gob.imss.catalogos.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import mx.gob.imss.catalogos.dto.MedioContactoDto;
import mx.gob.imss.catalogos.dto.MedioContactoProjection;
import mx.gob.imss.catalogos.dto.MediosContactoResponseDto;
import mx.gob.imss.catalogos.repository.MediosContactoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediosContactoService { // Renombrado de MediosContactoSoapClientService

    private final MediosContactoRepository mediosContactoRepository; // Inyectamos el nuevo repositorio

    @Autowired
    public MediosContactoService(MediosContactoRepository mediosContactoRepository) {
        this.mediosContactoRepository = mediosContactoRepository;
    }

    public MediosContactoResponseDto recuperarMediosContactoPorRfc(String rfc) {
        MediosContactoResponseDto responseDto = new MediosContactoResponseDto();
        responseDto.setRfc(rfc);
        responseDto.setMedios(new ArrayList<>());

        try {
            // Llama al repositorio para ejecutar la consulta SQL nativa
            //List<MedioContactoProjection> resultProjections = mediosContactoRepository.findMediosContactoByRfc(rfc);


                        // --- INICIO BLOQUE MOCK PARA PRUEBAS ---
            // Comenta la línea del repositorio y usa esta lista manual:
            List<MedioContactoProjection> resultProjections = new ArrayList<>();
            
            resultProjections.add(crearMockProjection("1", "jrpjaime@gmail.com", "CONTADOR")); 
            // --- FIN BLOQUE MOCK ---

           

            if (resultProjections != null && !resultProjections.isEmpty()) {
                List<MedioContactoDto> mediosDto = resultProjections.stream()
                    .map(this::convertirMedioContactoDto) // Convertir de proyección a DTO
                    .collect(Collectors.toList());
                responseDto.setMedios(mediosDto);
            }
        } catch (Exception e) {
            // Manejo de errores de la base de datos
            System.err.println("Error al recuperar medios de contacto de la DB para RFC " + rfc + ": " + e.getMessage());
            // Podrías lanzar una excepción personalizada o devolver un objeto de error en el DTO
        }
        return responseDto;
    }

    private MedioContactoDto convertirMedioContactoDto(MedioContactoProjection projection) {
        MedioContactoDto dto = new MedioContactoDto();
        dto.setTipoContacto(projection.getTipoContacto());
        dto.setDesFormaContacto(projection.getDesFormaContacto());
        dto.setRfcAsociado(projection.getRfcAsociado());
        return dto;
    }



    
    // Método auxiliar para crear el objeto Mock que implementa la interfaz de la Proyección
    private MedioContactoProjection crearMockProjection(String tipo, String correo, String origen) {
        return new MedioContactoProjection() {
            @Override
            public String getTipoContacto() { return tipo; }
            @Override
            public String getDesFormaContacto() { return correo; }
            @Override
            public String getRfcAsociado() { return origen; }
        };
    }
}