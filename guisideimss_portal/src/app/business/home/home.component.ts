 import { Component, EventEmitter, Output, OnInit, NgZone } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NAV } from '../../global/navigation';

import { NavigationExtras, Router } from '@angular/router';
import { SharedService } from '../../shared/services/shared.service';
import { CatalogosService } from '../../shared/catalogos/services/catalogos.service';
import { DitPatronGeneralFiltroDto } from '../../shared/catalogos/model/ditpatrongeneralfiltrodto';
import { DitPatronGeneralDto } from '../../shared/catalogos/model/ditpatrongeneraldto';
import { Constants } from '../../global/Constants';
import { BaseComponent } from '../../shared/base/base.component';
import { LoaderService } from '../../shared/services/loader.service';


import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../shared/services/alert.service';
import { HttpErrorResponse } from '@angular/common/http';


@Component({
    selector: 'app-home',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, FormsModule ],
    templateUrl: './home.component.html',
    styleUrls: ['./home.component.css']
})
export class HomeComponent   extends BaseComponent implements OnInit  {

   // registroActual$: Observable<RegistroPatronal | null>;

    page: number=0;
    seleccionapagina: number=1;
    size: number=2;
    order: string='registroPatronal';
    asc: boolean=true;
    paginaAll: boolean=false;

    numberOfElements!: number;
    isFirst=false;
    isLast=false;
    isEmpty=true;
    sizeManual: number=2;
    totalElements!:number;
    elementosPagina:number[]=[2,10,50,100,1000];
    totalPages: Array<number> = [];
    numeroPaginas: number=0;

    @Output() vacio = new EventEmitter<boolean>();


    desDelegacion: string = '';
    desSubdelegacion: string = '';

    ditPatronGeneralDtosPaginado: Array<DitPatronGeneralDto> = [];
    selectedRegistroPatronal: string | null = null; // Para almacenar el registro seleccionado
    formFiltro: FormGroup;
    form: FormGroup;
    formPaginador: FormGroup;



    constructor(
      private fb: FormBuilder,
      private catalogosService: CatalogosService,
      private router: Router,
      private loaderService: LoaderService,
      private alertService: AlertService,
      private zone: NgZone,
      sharedService: SharedService
    ) {

        super(sharedService);
     //   this.registroActual$ = this.contextoService.registroActual$;
        this.formFiltro = this.fb.group({
        registroPatronal: [''], // Control para el registro patronal
        desDelegacion: [''], // Control para la delegación
        desSubdelegacion: [''] // Control para la subdelegación
      });




      this.form = this.fb.group({
        registroPatronal: [null] // Control para el registro patronal seleccionado
      });

          // Inicializar el formulario
      this.formPaginador = this.fb.group({
        size: [this.size]  // Inicializar el tamaño con el valor actual
      });
    }

    onSelectionChange(registroPatronal: string) {
      this.form.get('registroPatronal')?.setValue(registroPatronal);
    }



override ngOnInit(): void {
  console.log('ngOnInit HomeComponent: ' + this.rfc);
  this.recargaParametros();


    this.cargarListPaginatedRegistrosPatronales(this.getFiltro());

}


    onSubmit(): void {
      if (this.formFiltro.valid) {
        console.log(this.formFiltro.value);
        this.cargarListPaginatedRegistrosPatronales(this.getFiltro());
        this.form.get('registroPatronal')?.reset();
      }
    }


    onClearFiltro(): void {
      this.formFiltro.reset();

    }






seleccionarYContinuar(): void {
       // this.contextoService.limpiarRegistro();
  // 1. Obtener el ID (string) del registro patronal desde el formulario.
  const registroIdSeleccionado = this.form.get('registroPatronal')?.value;

  if (registroIdSeleccionado) {
    // 2. BUSCAR el objeto completo en el array usando el ID.
    const registroCompleto = this.ditPatronGeneralDtosPaginado.find(
      item => item.registroPatronal === registroIdSeleccionado
    );

    // 3.  Asegurarse de que el objeto fue encontrado.
    if (registroCompleto) {
      console.log('Registro Patronal Objeto Completo:', registroCompleto);
       console.log('Registro Patronal: ', registroCompleto.registroPatronal);
        console.log('denominacionRazonSocial: ', registroCompleto.denominacionRazonSocial);



    } else {
      console.error("Error crítico: No se encontró el registro con ID:", registroIdSeleccionado);
    }
  }
}

    // --- CAMBIAR DE REGISTRO ---
    // Método para permitir al usuario cambiar de RP
    /*
    cambiarRegistroPatronal(): void {
      this.contextoService.limpiarRegistro();
      // Vuelve a cargar la lista para que el usuario pueda elegir de nuevo
      this.cargarListPaginatedRegistrosPatronales(this.getFiltro());
    }
*/
  private setRegistroPatronal(registroPatronal: string): void {
    sessionStorage.setItem(Constants.registroPatronal, registroPatronal);
  }



  cargarListPaginatedRegistrosPatronales(filtro: DitPatronGeneralFiltroDto): void {
    console.log("cargarListPaginatedRegistrosPatronales");
    filtro.rfc=this.rfc;
    filtro.page=this.page;
    filtro.size=this.size;
    filtro.order=this.order;
    filtro.asc=this.asc;


    console.log("filtro.rfc: " + filtro.rfc );
    console.log("filtro.page: " + filtro.page );
    console.log("filtro.size: " + filtro.size );
    console.log("filtro.order: " + filtro.order );
    console.log("filtro.asc: " + filtro.asc );

    if(!filtro.rfc || filtro.rfc.trim() === ""){
  console.log("Debe redireccionar a login")
  this.router.navigate(['/']);  // Redirigir a la página de login
  return; // Importante: Salir del método para no continuar la ejecución
}




    this.loaderService.showLoader();

    this.catalogosService.listPaginatedRegistrosPatronales(filtro).subscribe({

        next: (data: any) => {

          this.ditPatronGeneralDtosPaginado = data.content;
          this.isFirst=data.first;
          this.isLast=data.last;
          this.isEmpty=data.empty;
          this.totalPages=new Array(data['totalPages']);
          this.numeroPaginas=data.totalPages;
          this.totalElements=data.totalElements;
          this.numberOfElements=data.numberOfElements;
          this.isLast=data.last;
          this.esVacio();

          console.log("data.numberOfElements "+ data.numberOfElements);

          //this.alertService.success('Registros Patronales cargados exitosamente.', { autoClose: true });
        },
        error: (err: HttpErrorResponse) => {
          this.loaderService.closeLoader();
          console.error('Error al cargar Registros Patronales:', err);

          if (err.error && err.error.messages && Array.isArray(err.error.messages)) {
            // Accede a los mensajes de error del backend
            const errorMessages = err.error.messages.join(', ');
            console.error('Mensajes de error del backend:', errorMessages);
            // mensajes al usuario
            this.alertService.error(`<strong>Error:</strong><br>${errorMessages}`, { autoClose: false });
          } else {
            // Si no hay mensajes específicos del backend, muestra un mensaje genérico
            this.alertService.error('Ha ocurrido un error inesperado al cargar los Registros Patronales.', { autoClose: false });
          }
        },
        complete: () => {
          this.loaderService.closeLoader();
        }
      });

    }



  private  getFiltro() {
    let ditPatronGeneralFiltroDto=new DitPatronGeneralFiltroDto();
    ditPatronGeneralFiltroDto.rfc=this.rfc;
    ditPatronGeneralFiltroDto.registroPatronal=this.formFiltro.controls['registroPatronal'].value;
    ditPatronGeneralFiltroDto.desDelegacion=this.formFiltro.controls['desDelegacion'].value;
    ditPatronGeneralFiltroDto.desSubdelegacion=this.formFiltro.controls['desSubdelegacion'].value;

    console.log("getFiltro()  "   );
    console.log("rfc: " +ditPatronGeneralFiltroDto.rfc );
    console.log("registroPatronal: " + ditPatronGeneralFiltroDto.registroPatronal );
    console.log("desDelegacion: " + ditPatronGeneralFiltroDto.desDelegacion );
    console.log("desSubdelegacion:: " + ditPatronGeneralFiltroDto.desSubdelegacion);

    return ditPatronGeneralFiltroDto;
  }

  sortPage(): void{
    this.asc=!this.asc;
    this.cargarListPaginatedRegistrosPatronales(this.getFiltro());
  }

  rewindPage(): void{
    if(!this.isFirst){
      this.page--;
      this.cargarListPaginatedRegistrosPatronales(this.getFiltro());
    }
  }

  forwardPage(): void{
    if(!this.isLast){
      this.page++;
      this.cargarListPaginatedRegistrosPatronales(this.getFiltro());
    }
  }

  setPage(page: number){
    this.page=page;
    this.cargarListPaginatedRegistrosPatronales(this.getFiltro());
  }

  setOrder(order: string){
    this.order=order;
    this.cargarListPaginatedRegistrosPatronales(this.getFiltro());
  }

  setSize( ){
    this.size=this.sizeManual;
    this.cargarListPaginatedRegistrosPatronales(this.getFiltro());
  }

  setSizeCombo( ){
    this.page=0;
    this.paginaAll=false;
    /*
    const checkbox = document.getElementById('paginaAll',) as HTMLInputElement | null;
    if (checkbox != null) {
      checkbox.checked = false;
    }
    */

    this.size = this.formPaginador.get('size')?.value;
    this.cargarListPaginatedRegistrosPatronales(this.getFiltro());
  }



  esVacio() {
    this.vacio.emit(this.isEmpty);
  }




  }




