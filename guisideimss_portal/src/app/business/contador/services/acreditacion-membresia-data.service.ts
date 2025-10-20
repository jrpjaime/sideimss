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
  datosParaRegresar: FormDataToReturn | null = null;  

  constructor() { }

  setDatosFormularioPrevio(datos: any) {
    this.datosFormularioPrevio = datos;
  }

   
  setDatosParaRegresar(datos: FormDataToReturn) {
    this.datosParaRegresar = datos;
  }

  
  getDatosParaRegresar(): FormDataToReturn | null {
    return this.datosParaRegresar;
  }

 
  clearDatosParaRegresar() {
    this.datosParaRegresar = null;
  }
}
