import { Component, OnInit } from '@angular/core';
import { CatalogosContadorService } from '../services/catalogos-contador.service';
import { AlertService } from '../../../shared/services/alert.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AcreditacionMembresiaService } from '../services/acreditacion-membresia.service';
import { finalize } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-modificaciondatos',
  standalone: true,
  imports: [ CommonModule,
    FormsModule ],
  templateUrl: './modificaciondatos.component.html',
  styleUrl: './modificaciondatos.component.css'
})
export class ModificaciondatosComponent  implements OnInit {

  tiposDatosContador: string[] = [];
  selectedTipoDato: string = '';
  folioSolicitud: string | null = null;
  loadingFolio: boolean = false;

  constructor(
    private catalogosContadorService: CatalogosContadorService,
    private alertService: AlertService,
    private acreditacionMembresiaService: AcreditacionMembresiaService
  ) { }

  ngOnInit(): void {
    this.generarFolioSolicitud();
    this.cargarTiposDatosContador();
  }

  cargarTiposDatosContador(): void {
    this.catalogosContadorService.getTiposDatosContador().subscribe({
      next: (data: string[]) => {
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
  }



    generarFolioSolicitud(): void {
    this.loadingFolio = true;
     console.log('generarFolioSolicitud :');
    this.acreditacionMembresiaService.getNuevoFolioSolicitud()
      .pipe(finalize(() => this.loadingFolio = false))
      .subscribe({
        next: (folio: string) => {
          this.folioSolicitud = folio;
          console.log("Folio generado y asignado:", this.folioSolicitud);
        },
        error: (error: HttpErrorResponse) => {
          console.error("Error al obtener el folio:", error);
          this.alertService.error('Error al generar el folio de solicitud. Por favor, inténtalo de nuevo.', { autoClose: false });
          this.folioSolicitud = 'No disponible'; // Asignar un valor indicativo de error
        }
      });
  }
}
