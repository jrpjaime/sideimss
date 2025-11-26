import { DatosContactoDto } from "./DatosContactoDto";
import { DatosPersonalesDto } from "./DatosPersonalesDto";
import { DomicilioFiscalDto } from "./DomicilioFiscalDto";

export interface DatosContadorData {
  folioSolicitud: string;
  datosPersonalesDto: DatosPersonalesDto;
  domicilioFiscalDto: DomicilioFiscalDto;
  datosContactoDto: DatosContactoDto;
 

}
