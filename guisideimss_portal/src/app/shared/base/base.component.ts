import { Directive, OnInit } from '@angular/core';
import { SharedService } from '../services/shared.service';
 
import { AuthService } from '../../core/services/auth.service'; 

@Directive()
export class BaseComponent implements OnInit {
  rfc: string = '';
  rfcSesion: string = '';
  registroPatronal: string | null = null; // Para almacenar el registro seleccionado
  role: string = '';
  indPatron: boolean = false;

  indFase: number = 1;

  desDelegacionSesion: string = '';
  desSubdelegacionSesion: string = '';
  

  selectedFile: File | null = null; // Variable para almacenar el archivo seleccionado
  fileErrorMessage: string = '';

   readonly PATTERNS = {
      cveRegistroPatronal: '^[A-Za-z0-9]{8}[0-9]{2}[0-9]{1}$',
      cveNss: '^[0-9]{11}$',
      numPorcentajeDescuento: '^[0-9]{1,2}$',
      refFolioSua: '^[0-9]{6}$',
      refFolioGefide: '^[0-9]{9}$',
      refNumeroCredito: '^[0-9]{9}$',
      numSalarioDiarioInte: '^[0-9]{3,}(\\.[0-9]{2})?$',
      numDias: '^[1-9][0-9]*$'
    };

  constructor(
     protected sharedService: SharedService) {}



  ngOnInit(): void {
   // this.recargaParametros();
  }

  recargaParametros(): void {
    console.log('.........BaseComponent ');
    this.sharedService.initializeUserData();

    this.sharedService.currentRfc.subscribe(rfc => {
      this.rfc = rfc;
      console.log('this.rfc: ', this.rfc);
    });

    this.sharedService.currentRfcSesion.subscribe(rfcSesion => {
      this.rfcSesion = rfcSesion;
    });

    this.sharedService.currentRegistroPatronal.subscribe(registroPatronal => {
          this.registroPatronal = registroPatronal;
    });


    this.sharedService.currentRoleSesion.subscribe(role => {
      this.role = role;
      if(role==='Patron'){
        this.indPatron=true;
      }
      console.log('this.role: '+ this.role);

    });

 

    this.sharedService.currentSubdelegacionSesion.subscribe(desDelegacionSesion => {
      this.desDelegacionSesion = desDelegacionSesion;
    });

    this.sharedService.currentDelegacionSesion.subscribe(desSubdelegacionSesion => {
      this.desSubdelegacionSesion = desSubdelegacionSesion;
    });


  }


  onFileSelected(event: any): void {
    const file = event.target.files[0];
    const maxSize = 5 * 1024 * 1024; // 5MB en bytes

    if (file) {
      if (file.size > maxSize) {
        this.selectedFile = null;
        this.fileErrorMessage = 'El archivo excede el tamaño máximo permitido de 5MB.';
      } else {
        this.selectedFile = file;
        this.fileErrorMessage = ''; // Limpiar mensaje de error si el archivo es válido
      }
    }
  }


// Método para formatear la fecha
formatDate(date: string): string {
  if (!date) {
    return ''; // Si la fecha es null o vacía, devuelve una cadena vacía
  }

  const [day, month, year] = date.split('/');
  const formattedDate = new Date(`${year}-${month}-${day}`);
  return formattedDate.toISOString().split('T')[0]; // Devuelve en formato "yyyy-MM-dd"
}





}

