import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ModificacionDatosDataService {
  private datosFormularioPrevio: any = {};

  constructor() { }

  setDatosFormularioPrevio(datos: any) {
    this.datosFormularioPrevio = datos;
  }

  getDatosFormularioPrevio(): any {
    return this.datosFormularioPrevio;
  }

  clearDatosFormularioPrevio() {
    this.datosFormularioPrevio = {};
  }
}