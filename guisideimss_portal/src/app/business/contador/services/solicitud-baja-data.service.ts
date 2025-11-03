import { Injectable } from '@angular/core';

export interface SolicitudBajaFormData {
  folioSolicitud: string | null;
  datosPersonalesDto: any;
  domicilioFiscalDto: any;
  datosContactoDto: any;
  motivoBaja: string;

}

@Injectable({
  providedIn: 'root'
})
export class SolicitudBajaDataService {

  private _solicitudBajaData: SolicitudBajaFormData | null = null;
  private _datosParaRegresar: SolicitudBajaFormData | null = null; // Para la navegación de regreso

  constructor() { }

  setSolicitudBajaData(data: SolicitudBajaFormData) {
    this._solicitudBajaData = data;
  }

  getSolicitudBajaData(): SolicitudBajaFormData | null {
    return this._solicitudBajaData;
  }

  clearSolicitudBajaData() {
    this._solicitudBajaData = null;
  }

  setDatosParaRegresar(datos: SolicitudBajaFormData) {
    this._datosParaRegresar = datos;
  }

  getDatosParaRegresar(): SolicitudBajaFormData | null {
    return this._datosParaRegresar;
  }

  clearDatosParaRegresar() {
    this._datosParaRegresar = null;
  }
}
