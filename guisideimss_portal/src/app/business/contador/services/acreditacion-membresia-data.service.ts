import { Injectable } from '@angular/core';


@Injectable({
  providedIn: 'root'
})
export class AcreditacionMembresiaDataService {


  datosFormularioPrevio: any = {};

  constructor() { }


  setDatosFormularioPrevio(datos: any) {
    this.datosFormularioPrevio = datos;
  }
}
