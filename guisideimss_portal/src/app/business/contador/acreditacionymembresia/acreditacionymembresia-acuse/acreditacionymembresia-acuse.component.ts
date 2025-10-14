import { Component, OnInit, Renderer2 } from '@angular/core';
import { AcreditacionMembresiaDataService } from '../../services/acreditacion-membresia-data.service';
import { BaseComponent } from '../../../../shared/base/base.component';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { CatalogosService } from '../../../../shared/catalogos/services/catalogos.service';
import { ModalService } from '../../../../shared/services/modal.service';
import { AcreditacionMembresiaService } from '../../services/acreditacion-membresia.service';
import { AlertService } from '../../../../shared/services/alert.service';
import { SharedService } from '../../../../shared/services/shared.service';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { PlantillaDatoDto } from '../../model/PlantillaDatoDto';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { take } from 'rxjs';

@Component({
  selector: 'app-acreditacionymembresia-acuse',
  standalone: true,
  imports: [CommonModule ],
  templateUrl: './acreditacionymembresia-acuse.component.html',
  styleUrl: './acreditacionymembresia-acuse.component.css'
})
export class AcreditacionymembresiaAcuseComponent extends BaseComponent  implements OnInit  {
  datosFormularioPrevio: any = {};
  acusePdfUrl: SafeResourceUrl | null = null; // Para la URL segura del PDF
  loadingAcusePreview: boolean = false;
  acusePreviewError: string | null = null;
  nombreCompleto: string = '';


  public Object = Object;

  constructor (
    private fb: FormBuilder,
    private router : Router,
    private renderer: Renderer2,
    private catalogosService: CatalogosService,
    private modalService: ModalService,
    private acreditacionMembresiaService: AcreditacionMembresiaService, // Inyectar el servicio
    private alertService: AlertService,
    private acreditacionMembresiaDataService: AcreditacionMembresiaDataService,
    private sanitizer: DomSanitizer, // Inyectar DomSanitizer
    sharedService: SharedService
  ) {
    super(sharedService);
  }


  override ngOnInit(): void {
    super.ngOnInit(); // Importante: Asegura que BaseComponent inicie sus Observables
    this.recargaParametros(); // Inicia la carga de datos del usuario desde el SharedService

    // 1. Obtener los datos previos
    this.datosFormularioPrevio = this.acreditacionMembresiaDataService.datosFormularioPrevio;

    // 2. Suscribirse al nombreCompleto$ del BaseComponent y esperar el primer valor
    this.nombreCompleto$.pipe(take(1)).subscribe(nombre => {
      this.datosFormularioPrevio.nombreCompleto = nombre; // Cuando tengamos el nombre completo, lo añadimos a datosFormularioPrevio
      this.datosFormularioPrevio.RFC = this.rfcSesion; // rfcSesion ya debería estar actualizado por recargaParametros
      this.datosFormularioPrevio.CURP = this.curpSesion; // curpSesion ya debería estar actualizado por recargaParametros

      console.log('Datos del formulario previo en Acuse (con datos de sesión):', this.datosFormularioPrevio);

      if (nombre && this.rfcSesion && this.curpSesion) {
        this.datosFormularioPrevio.cadenaOriginal = `${nombre}|${this.rfcSesion}|${this.curpSesion}`;
      } else {
        console.warn('Faltan datos para generar la cadenaOriginal (nombreCompleto, RFC o CURP).');
        this.datosFormularioPrevio.cadenaOriginal = ''; // O manejar el error de otra manera
      }


      // 3. Después de añadir los datos de sesión, procede con la lógica del acuse
      if (Object.keys(this.datosFormularioPrevio).length > 0) {
        this.descargarAcusePreview();
      } else {
        this.alertService.warn('No se encontraron datos para generar el acuse. Por favor, regresa y completa la información.', { autoClose: false });
      }
    });
  }


  descargarAcusePreview(): void {
    this.loadingAcusePreview = true;
    this.acusePreviewError = null;
    this.alertService.clear();

    // Asegúrate de que los datos de sesión ya estén en datosFormularioPrevio aquí
    // El nombreCompleto, RFC y CURP se habrán añadido en el subscribe de ngOnInit
    const datosJson = JSON.stringify(this.datosFormularioPrevio);

    // Crear el DTO para enviar al backend
    const plantillaDatoDto: PlantillaDatoDto = {
      cveIdPlantillaDatos: null,
      nomDocumento: "prueba.pdf",
      desVersion: "reportes\\contadores\\acreditacionmenbresia\\v202512\\SolicitudAcreditacionContador",
      datosJson: datosJson,
      tipoAcuse: "ACREDITACION_MEMBRESIA"
    };

    console.log('PlantillaDatoDto enviado para preview:', plantillaDatoDto);

    this.acreditacionMembresiaService.descargarAcusePreview(plantillaDatoDto).subscribe({
      next: (response: HttpResponse<Blob>) => {
        this.loadingAcusePreview = false;
        if (response.body) {
          const blob = new Blob([response.body], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          this.acusePdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
          this.alertService.success('Acuse previsualizado correctamente.', { autoClose: true });
        } else {
          this.acusePreviewError = 'No se recibió ningún documento para previsualizar.';
          this.alertService.error(this.acusePreviewError, { autoClose: false });
        }
      },
      error: (errorResponse: HttpErrorResponse) => {
        this.loadingAcusePreview = false;
        console.error('Error al descargar el preview del acuse:', errorResponse);
        let errorMessage = 'Error al generar la previsualización del acuse. Inténtalo de nuevo más tarde.';

        if (errorResponse.status === 404) {
          errorMessage = 'No se encontró el acuse para los datos proporcionados o la plantilla no existe.';
        } else if (errorResponse.error instanceof Blob) {
																				  
            const reader = new FileReader();
            reader.onload = () => {
                try {
                    const errorBody = JSON.parse(reader.result as string);
                    errorMessage = errorBody.message || errorBody.error || errorMessage;
                } catch (e) {
                    console.warn('No se pudo parsear el error como JSON:', reader.result);
                }
                this.acusePreviewError = errorMessage;
                this.alertService.error(this.acusePreviewError, { autoClose: false });
            };
            reader.readAsText(errorResponse.error);
            return;
        } else if (errorResponse.message) {
          errorMessage = errorResponse.message;
        }
        this.acusePreviewError = errorMessage;
        this.alertService.error(this.acusePreviewError, { autoClose: false });
      }
    });
  }

  // Puedes añadir un método para enviar los datos finales si es necesario aquí
  enviarSolicitudFinal(): void {
    this.alertService.clear();
    if (Object.keys(this.datosFormularioPrevio).length > 0) {
      this.modalService.showDialog(
        'confirm',
        'info',
        'Confirmar Envío',
        '¿Estás seguro de que deseas enviar esta solicitud de Acreditación y Membresía?',
        (confirmed: boolean) => {
          if (confirmed) {
            this.alertService.info('Enviando solicitud final...', { autoClose: true });
            this.acreditacionMembresiaService.enviarDatosFinales(this.datosFormularioPrevio).subscribe({
              next: (response) => {
                console.log('Respuesta de envío final:', response);
                if (response.codigo === 0) { // Asumiendo que 0 es éxito
                  this.alertService.success('Solicitud enviada exitosamente. Folio: ' + response.folio, { autoClose: false });
                  // Opcional: Redirigir o limpiar datos
                  this.acreditacionMembresiaDataService.setDatosFormularioPrevio({}); // Limpiar datos
                  this.router.navigate(['/home']); // Ejemplo de redirección
                } else {
                  this.alertService.error(response.mensaje || 'Error al enviar la solicitud final.', { autoClose: false });
                }
              },
              error: (err) => {
                console.error('Error al enviar solicitud final:', err);
                this.alertService.error('Ocurrió un error al enviar la solicitud final. Por favor, inténtalo de nuevo.', { autoClose: false });
              }
            });
          } else {
            this.alertService.info('Envío de solicitud cancelado.', { autoClose: true });
          }
        },
        'Sí, Enviar',
        'No, Volver'
      );
    } else {
      this.alertService.error('No hay datos de formulario para enviar. Por favor, completa el formulario anterior.', { autoClose: false });
    }
  }

  volver(): void {
    this.router.navigate(['/acreditacion-membresia']); // Regresar al formulario principal
  }
}
