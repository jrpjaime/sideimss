import { Component, OnInit } from '@angular/core';
import { CatalogosContadorService } from '../services/catalogos-contador.service';
import { AlertService } from '../../../shared/services/alert.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';
import { ContadorPublicoAutorizadoService } from '../services/contador-publico-autorizado.service';
import { BaseComponent } from '../../../shared/base/base.component';
import { ColegioContadorDto } from '../model/ColegioContadorDto';
import { SharedService } from '../../../shared/services/shared.service';
import { TipoDatoContadorDto } from '../model/TipoDatoContadorDto';

@Component({
  selector: 'app-modificaciondatos',
  standalone: true,
  imports: [ CommonModule,
    FormsModule ],
  templateUrl: './modificaciondatos.component.html',
  styleUrl: './modificaciondatos.component.css'
})
export class ModificaciondatosComponent extends BaseComponent implements OnInit {

  tiposDatosContador: TipoDatoContadorDto[] = [];
  selectedTipoDato: string = '';
  folioSolicitud: string | null = null;
  loadingFolio: boolean = false;

  // Propiedades para la consulta del colegio
  colegioContador: ColegioContadorDto | null = null;
  loadingColegio: boolean = false;
  mostrarSeccionColegio: boolean = false; // Controla la visibilidad de la sección del colegio



  constructor(
    private catalogosContadorService: CatalogosContadorService,
    private alertService: AlertService,
    private contadorPublicoAutorizadoService: ContadorPublicoAutorizadoService,
    sharedService: SharedService
  )  {
    super(sharedService);
    this.recargaParametros();
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.generarFolioSolicitud();
    this.cargarTiposDatosContador();

    console.log('RFC de sesión en ModificaciondatosComponent:', this.rfcSesion);
  }





  cargarTiposDatosContador(): void {
    // Cambia el tipo esperado en el subscribe
    this.catalogosContadorService.getTiposDatosContador().subscribe({
      next: (data: TipoDatoContadorDto[]) => { // <-- Ahora espera una lista de TipoDatoContadorDto
        this.tiposDatosContador = data;
        console.log('Tipos de datos de contador cargados:', this.tiposDatosContador);
      },
      error: (error) => {
        console.error('Error al cargar los tipos de datos de contador:', error);
        this.alertService.error('Error al cargar las opciones de datos. Inténtalo de nuevo más tarde.', { autoClose: false });
      }
    });
  }

  onTipoDatoChange(event: Event): void {
    this.selectedTipoDato = (event.target as HTMLSelectElement).value;
    console.log('Opción seleccionada:', this.selectedTipoDato);

    this.colegioContador = null;
    this.mostrarSeccionColegio = false;

     if (this.selectedTipoDato === '3') {
      this.mostrarSeccionColegio = true;
      this.consultarDatosColegio();
    }
  }

  generarFolioSolicitud(): void {
    this.loadingFolio = true;
    console.log('generarFolioSolicitud:');
    this.contadorPublicoAutorizadoService.getNuevoFolioSolicitud()
      .pipe(finalize(() => this.loadingFolio = false))
      .subscribe({
        next: (folio: string) => {
          this.folioSolicitud = folio;
          console.log("Folio generado y asignado:", this.folioSolicitud);
        },
        error: (error: HttpErrorResponse) => {
          console.error("Error al obtener el folio:", error);
          this.alertService.error('Error al generar el folio de solicitud. Por favor, inténtalo de nuevo.', { autoClose: false });
          this.folioSolicitud = 'No disponible';
        }
      });
  }

  consultarDatosColegio(): void {
   console.log('consultarDatosColegio');
    const rfcActualContador = this.rfcSesion;

    if (!rfcActualContador) {
      this.alertService.warn('No se pudo consultar el colegio: RFC del contador no disponible en la sesión.');
      console.warn('RFC del contador no disponible en la sesión para consultar el colegio.');
      this.colegioContador = null;
      return;
    }
    console.log('Intentando consultar colegio para RFC:', rfcActualContador);

    this.loadingColegio = true;
    this.contadorPublicoAutorizadoService.getColegioByRfcContador(rfcActualContador)
      .pipe(finalize(() => this.loadingColegio = false))
      .subscribe({
        next: (data: ColegioContadorDto) => {
          this.colegioContador = data;
          console.log('Datos del colegio obtenidos:', this.colegioContador);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error al obtener los datos del colegio:', error);
          this.alertService.error('Error al consultar los datos del colegio. Inténtalo de nuevo más tarde.');
          this.colegioContador = null; // Limpiar datos previos en caso de error
        }
      });
  }
}
