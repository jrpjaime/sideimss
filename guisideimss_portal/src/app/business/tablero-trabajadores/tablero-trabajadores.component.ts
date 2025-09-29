import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { BaseComponent } from '../../shared/base/base.component';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { LoaderService } from '../../shared/services/loader.service';
import { SharedService } from '../../shared/services/shared.service';
import { SwtAseguradoDto } from './model/swt-asegurado.dto';
import { TrabajadoresService } from './services/trabajadores.service';
import { HttpErrorResponse } from '@angular/common/http';
import { AlertService } from '../../shared/services/alert.service';

@Component({
  selector: 'app-tablero-trabajadores',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './tablero-trabajadores.component.html',
  styleUrl: './tablero-trabajadores.component.css'
})
export class TableroTrabajadoresComponent extends BaseComponent implements OnInit  {

  titulo: string = 'Trabajadores';


  page: number = 0;
  seleccionapagina: number = 1;
  size: number = 2;
  order: string = 'registroPatronal';
  asc: boolean = true;
  paginaAll: boolean = false;

  numberOfElements!: number;
  isFirst = false;
  isLast = false;
  isEmpty = true;
  sizeManual: number = 2;
  totalElements!: number;
  elementosPagina: number[] = [2, 10, 50, 100, 1000];
  totalPages: Array<number> = [];
  numeroPaginas: number = 0;

  @Output() vacio = new EventEmitter<boolean>();
  swtAseguradoDtosPaginado: Array<SwtAseguradoDto> = [];

  desDelegacion: string = '';
  desSubdelegacion: string = '';

  formFiltro: FormGroup;
  form: FormGroup;
  formPaginador: FormGroup;

  constructor(
    private fb: FormBuilder,
    private trabajadoresService: TrabajadoresService,
    private router: Router,
    private loaderService: LoaderService,
    private alertService: AlertService,
    sharedService: SharedService
  ) {
    super(sharedService);

    this.formFiltro = this.fb.group({
      numNss: [''], // Control para el número de seguridad social
    });

    this.form = this.fb.group({
      // El nombre del control debe coincidir con el formControlName del radio button
      registroPatronal: [null]
    });

    // Inicializar el formulario
    this.formPaginador = this.fb.group({
      size: [this.size] // Inicializar el tamaño con el valor actual
    });
  }

  onSelectionChange(registroPatronal: string) {
    this.form.get('registroPatronal')?.setValue(registroPatronal);
  }


  override ngOnInit(): void {
  console.log('ngOnInit TableroTrabajadores: ' + this.rfc);
  this.recargaParametros();


    // Si por alguna razón llega aquí sin un registro, es una condición de error.
    // El Guard ya debería haberlo prevenido, pero es una buena práctica de defensa.
    console.error("Tablero de trabajadores cargado sin un Registro Patronal en el contexto.");

}

  agregarTrabajador(): void {
    const registroSeleccionado = this.form.get('registroPatronal')?.value;
    if (registroSeleccionado) {
      console.log('Registro Patronal seleccionado:', registroSeleccionado);
      // Aquí puedes añadir la lógica para guardar el contexto y navegar a otra página
      // Ejemplo:
      // this.contextoService.establecerContexto(registroSeleccionado);
      // this.router.navigate(['/ruta-a-continuar']);
    } else {
      console.log('Ningún Registro Patronal ha sido seleccionado.');
    }
  }

  onSubmit(): void {
    if (this.formFiltro.valid) {
      console.log(this.formFiltro.value);
      this.cargarListPaginatedTrabajadores(this.getFiltro());
      this.form.get('registroPatronal')?.reset();
    }
  }

  onClearFiltro(): void {
    this.formFiltro.reset();
  }

  cargarListPaginatedTrabajadores(filtro: SwtAseguradoDto): void {
    console.log("cargarListPaginatedTrabajadores");
    filtro.refRfc = this.rfc;
    filtro.page = this.page;
    filtro.size = this.size;
    filtro.order = this.order;
    filtro.asc = this.asc;



    console.log("filtro.rfc: " + filtro.refRfc);
    console.log("filtro.page: " + filtro.page);
    console.log("filtro.size: " + filtro.size);
    console.log("filtro.order: " + filtro.order);
    console.log("filtro.asc: " + filtro.asc);

    if (!filtro.cveRegistroPatronal || filtro.cveRegistroPatronal.trim() === "") {
      console.log("Debe redireccionar a login")
      this.router.navigate(['/']); // Redirigir a la página de login
      return; // Importante: Salir del método para no continuar la ejecución
    }


    this.loaderService.showLoader();

    this.trabajadoresService.listPaginatedTrabajadores(filtro).subscribe({
      next: (data: any) => {
        this.swtAseguradoDtosPaginado = data.content;
        this.isFirst = data.first;
        this.isLast = data.last;
        this.isEmpty = data.empty;
        this.totalPages = new Array(data['totalPages']);
        this.numeroPaginas = data.totalPages;
        this.totalElements = data.totalElements;
        this.numberOfElements = data.numberOfElements;
        this.esVacio();

        console.log("data.numberOfElements " + data.numberOfElements);
        this.alertService.success('Trabajadores cargados exitosamente.', { autoClose: true });
      },
      error: (err: HttpErrorResponse) => {
        this.loaderService.closeLoader();
        console.error('Error al cargar trabajadores:', err);

        if (err.error && err.error.messages && Array.isArray(err.error.messages)) {
          // Accede a los mensajes de error del backend
          const errorMessages = err.error.messages.join(', ');
          console.error('Mensajes de error del backend:', errorMessages);
          // mensajes al usuario
          this.alertService.error(`<strong>Error:</strong><br>${errorMessages}`, { autoClose: false });
        } else {
          // Si no hay mensajes específicos del backend, muestra un mensaje genérico
          this.alertService.error('Ha ocurrido un error inesperado al cargar los trabajadores.', { autoClose: false });
        }
      },
      complete: () => {
        this.loaderService.closeLoader();
      }
    });
  }

  private getFiltro() {
    let swtAseguradoDto = new SwtAseguradoDto();
    swtAseguradoDto.refRfc = this.rfc;
    swtAseguradoDto.numNss = this.formFiltro.controls['numNss'].value;


    console.log("getFiltro()  ");
    console.log("rfc: " + swtAseguradoDto.refRfc);
    console.log("numNss: " + swtAseguradoDto.numNss);

    return swtAseguradoDto;
  }

  sortPage(): void {
    this.asc = !this.asc;
    this.cargarListPaginatedTrabajadores(this.getFiltro());
  }

  rewindPage(): void {
    if (!this.isFirst) {
      this.page--;
      this.cargarListPaginatedTrabajadores(this.getFiltro());
    }
  }

  forwardPage(): void {
    if (!this.isLast) {
      this.page++;
      this.cargarListPaginatedTrabajadores(this.getFiltro());
    }
  }

  setPage(page: number) {
    this.page = page;
    this.cargarListPaginatedTrabajadores(this.getFiltro());
  }

  setOrder(order: string) {
    this.order = order;
    this.cargarListPaginatedTrabajadores(this.getFiltro());
  }

  setSize() {
    this.size = this.sizeManual;
    this.cargarListPaginatedTrabajadores(this.getFiltro());
  }

  setSizeCombo() {
    this.page = 0;
    this.paginaAll = false;
    this.size = this.formPaginador.get('size')?.value;
    this.cargarListPaginatedTrabajadores(this.getFiltro());
  }

  esVacio() {
    this.vacio.emit(this.isEmpty);
  }
}
