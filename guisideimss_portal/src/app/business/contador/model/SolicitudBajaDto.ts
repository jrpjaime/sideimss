import { DatosContactoDto } from "./DatosContactoDto";
import { DatosPersonalesDto } from "./DatosPersonalesDto";
import { DomicilioFiscalDto } from "./DomicilioFiscalDto";

export interface SolicitudBajaDto {
  folioSolicitud: string;
  datosPersonalesDto: DatosPersonalesDto;
  domicilioFiscalDto: DomicilioFiscalDto;
  datosContactoDto: DatosContactoDto;
  motivoBaja: string;
}