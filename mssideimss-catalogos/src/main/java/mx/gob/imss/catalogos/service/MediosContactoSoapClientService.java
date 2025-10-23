package mx.gob.imss.catalogos.service;

import org.springframework.stereotype.Service;

import mx.gob.imss.catalogos.medioscontacto.Jws;
import mx.gob.imss.catalogos.medioscontacto.JwsServiceSoapBindingQSService;
import mx.gob.imss.catalogos.medioscontacto.MedContactoRepreLegalDTO;
import mx.gob.imss.catalogos.dto.MedioContactoDto;
import mx.gob.imss.catalogos.dto.MediosContactoResponseDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MediosContactoSoapClientService {

    private final Jws port;

    public MediosContactoSoapClientService() {
        // Se inicializa el cliente SOAP aquí.
        // Esto es ideal para un @Service, Spring puede gestionar su ciclo de vida.
        JwsServiceSoapBindingQSService service = new JwsServiceSoapBindingQSService();
        this.port = service.getJwsServiceSoapBindingQSPort();
    }

    public MediosContactoResponseDto recuperarMediosContactoPorRfc(String rfc) {
        MediosContactoResponseDto responseDto = new MediosContactoResponseDto();
        responseDto.setRfc(rfc);
        responseDto.setMedios(new ArrayList<>());

        try {
            // Llama al servicio SOAP
            mx.gob.imss.catalogos.medioscontacto.ArrayOfMedContactoRepreLegalDTOLiteral soapResult = port.recuperaMediosContacto(rfc);

            if (soapResult != null && soapResult.getMedContactoRepreLegalDTO() != null) {
                List<MedioContactoDto> mediosDto = soapResult.getMedContactoRepreLegalDTO().stream()
                    .map(this::convertirMedioContactoDto)
                    .collect(Collectors.toList());
                responseDto.setMedios(mediosDto);
            }
        } catch (Exception e) {
            // Manejo de errores SOAP
            System.err.println("Error al recuperar medios de contacto SOAP para RFC " + rfc + ": " + e.getMessage());
            // Podrías lanzar una excepción personalizada o devolver un objeto de error en el DTO
        }
        return responseDto;
    }

    private MedioContactoDto convertirMedioContactoDto(MedContactoRepreLegalDTO soapDto) {
        MedioContactoDto dto = new MedioContactoDto();
        dto.setTipoContacto(soapDto.getTipoContacto());
        dto.setDesFormaContacto(soapDto.getDesFormaContacto());
        dto.setRfcAsociado(soapDto.getRfc());
        return dto;
    }
}