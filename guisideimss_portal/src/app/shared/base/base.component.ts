import { Directive, OnInit } from '@angular/core';
import { SharedService } from '../services/shared.service';

import { AuthService } from '../../core/services/auth.service';
import { Constants } from '../../global/Constants';

@Directive()
export class BaseComponent implements OnInit {
  rfc: string = '';

  
  rfcSesion: string = '';

  nombreSesion: string = '';
  primerApellidoSesion: string = '';
  segundoApellidoSesion: string = '';
  curpSesion: string = '';
   

  roles: string[] = [];
  indPatron: boolean = false;

  indFase: number = 1;

  desDelegacionSesion: string = '';
  desSubdelegacionSesion: string = '';


  selectedFile: File | null = null; // Variable para almacenar el archivo seleccionado
  fileErrorMessage: string = '';

   readonly PATTERNS = {
      cveRegistroPatronal: '^[A-Za-z0-9]{8}[0-9]{2}[0-9]{1}$',
      cveNss: '^[0-9]{11}$'

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


    this.sharedService.currentCurpSesion.subscribe(curpSesion => {
      this.curpSesion = curpSesion;
    });

    this.sharedService.currentNombreSesion.subscribe(nombreSesion => {
      this.nombreSesion = nombreSesion;
    });

    this.sharedService.currentPrimerApellidoSesion.subscribe(primerApellidoSesion => {
      this.primerApellidoSesion = primerApellidoSesion;
    });

    this.sharedService.currentSegundoApellidoSesion.subscribe(segundoApellidoSesion => {
      this.segundoApellidoSesion = segundoApellidoSesion;
    });

    this.sharedService.currentRoleSesion.subscribe(roles => {
    this.roles = roles;

    // Usa .includes() para verificar si el usuario tiene el rol de 'Patron'
     if (this.roles.includes(Constants.rolePatron)) {
     this.indPatron = true;
    } else {
     this.indPatron = false;
    }

    console.log('this.roles: ' + this.roles.join(', '));
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

