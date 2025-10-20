import { Injectable } from '@angular/core';


export interface FormDataToReturn {
  fechaExpedicionAcreditacion: string | null;
  fechaExpedicionMembresia: string | null;

  fileUnoHdfsPath: string | null;
  fileDosHdfsPath: string | null;
  selectedFileUnoName: string | null;
  selectedFileDosName: string | null;
}



@Injectable({
  providedIn: 'root'
})
export class AcreditacionMembresiaDataService {


  datosFormularioPrevio: any = {};
  datosParaRegresar: FormDataToReturn | null = null; // Cambiado a null para que sea más explícito cuando no hay datos

  constructor() { }

  setDatosFormularioPrevio(datos: any) {
    this.datosFormularioPrevio = datos;
  }

  // Permite obtener los datos de la previsualización
  getDatosFormularioPrevio(): any {
    return this.datosFormularioPrevio;
  }

  setDatosParaRegresar(datos: FormDataToReturn) {
    this.datosParaRegresar = datos;
  }

  getDatosParaRegresar(): FormDataToReturn | null {
    return this.datosParaRegresar;
  }

  // *** IMPORTANTE: Limpia los datos guardados para el regreso ***
  clearDatosParaRegresar() {
    this.datosParaRegresar = null;
  }
}
