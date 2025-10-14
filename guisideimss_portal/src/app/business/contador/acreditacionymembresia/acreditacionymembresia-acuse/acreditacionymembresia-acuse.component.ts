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
import { Subscription, take } from 'rxjs';


export interface FirmaRequestBackendResponse { // <--- Asegúrate de que esto existe
  cad_original: string;
  peticionJSON: string;
  error: boolean;
  mensaje?: string; // Opcional, para mensajes de éxito/error
}

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


  isFirmaModalVisible: boolean = false;
  firmaWidgetUrl: SafeResourceUrl | null = null;
  private messageSubscription: Subscription | undefined;

  cadenaOriginalFirmada: string = ''; // Esta será la cadena que armemos en el frontend
  firmaDigital: string = '';
  folioFirma: string = '';
  curpFirma: string = '';
  desFolioFirma: string = '';

  public Object = Object;

  private windowMessageListener: ((event: MessageEvent) => void) | undefined;

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
    super.ngOnInit(); // Asegura que BaseComponent inicie sus Observables
    this.recargaParametros(); // Inicia la carga de datos del usuario desde el SharedService

    // 1. Obtener los datos previos
    this.datosFormularioPrevio = this.acreditacionMembresiaDataService.datosFormularioPrevio;


     this.descargarAcusePreview();

    // REGISTRAR EL LISTENER PARA MENSAJES DEL IFRAME
    this.windowMessageListener = this.respuestaCHFECyN.bind(this);
    window.addEventListener('message', this.windowMessageListener);
  }

  // AGREGAR ngOnDestroy PARA LIMPIAR EL LISTENER
  override ngOnDestroy(): void { // Usa 'override' si BaseComponent tiene ngOnDestroy
    if (this.windowMessageListener) {
      window.removeEventListener('message', this.windowMessageListener);
    }
    super.ngOnDestroy(); // Llama al ngOnDestroy de BaseComponent
  }



  descargarAcusePreview(): void {
    this.loadingAcusePreview = true;
    this.acusePreviewError = null;
    this.alertService.clear();

    this.datosFormularioPrevio.vistaPrevia = "SI";

    // Asegúrate de que los datos de sesión ya estén en datosFormularioPrevio aquí
    // El nombreCompleto, RFC y CURP se habrán añadido en el subscribe de ngOnInit
    const datosJson = JSON.stringify(this.datosFormularioPrevio);


    // Crear el DTO para enviar al backend
    const plantillaDatoDto: PlantillaDatoDto = {
      cveIdPlantillaDatos: null,
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
          errorMessage = 'No se encontró el acuse para los datos proporcionados.';
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





  iniciarProcesoFirma(): void {
    console.log("iniciarProcesoFirma - Solicitando JSON de firma al backend.");
    this.alertService.clear();

    const rfcUsuario = this.rfcSesion; // Obtener el RFC del usuario de la sesión

    if (!rfcUsuario) {
      this.alertService.error('No se pudo obtener el RFC del usuario. Por favor, inténtalo de nuevo.', { autoClose: false });
      return;
    }

    this.alertService.info('Preparando proceso de firma electrónica...', { autoClose: true });

    this.acreditacionMembresiaService.generarRequestJsonFirma(rfcUsuario).subscribe({
      next: (response: FirmaRequestBackendResponse) => {
        if (!response.error) {
          this.cadenaOriginalFirmada = response.cad_original; // Almacenar la cadena original del backend
          let peticionJSON = response.peticionJSON;

          console.log("Cadena Original recibida del backend:", this.cadenaOriginalFirmada);
          console.log("Peticion JSON recibida del backend:", peticionJSON);

          // El reemplazo de caracteres especiales ya lo hace el backend,
          // pero si quieres ser doblemente seguro, podrías mantenerlo aquí.
          // peticionJSON = peticionJSON.replaceAll("ñ", "\\u00d1").replaceAll("Ñ", "\\u00D1");

          this.displayFirmaModalAndSubmitForm(peticionJSON);
        } else {
          console.error('Error del backend al generar JSON de firma:', response.mensaje);
          this.alertService.error(response.mensaje || 'Error al generar la petición de firma electrónica desde el backend.', { autoClose: false });
        }
      },
      error: (errorResponse: HttpErrorResponse) => {
        console.error('Error al conectar con el backend para generar JSON de firma:', errorResponse);
        let errorMessage = 'Error de comunicación con el servicio de firma. Por favor, inténtalo de nuevo más tarde.';
        if (errorResponse.error && typeof errorResponse.error === 'object' && errorResponse.error.mensaje) {
          errorMessage = errorResponse.error.mensaje;
        }
        this.alertService.error(errorMessage, { autoClose: false });
      }
    });
  }
  displayFirmaModalAndSubmitForm(params: string): void {
    const URL_FIRMA_DIGITAL =  'http://172.16.23.224'; // Asegúrate de que esta URL sea correcta y accesible
    const widgetActionUrl = `${URL_FIRMA_DIGITAL}/firmaElectronicaWeb/widget/chfecyn`;


    console.log("Params: "+ params);
    console.log("widgetActionUrl: "+ widgetActionUrl);

    // Crear el iframe dinámicamente o usar un modal con iframe.
    // Si ya tienes un modal que muestra un iframe, asegúrate de que el 'name' del iframe
    // coincida con el 'target' del formulario.

    // Si tu modal es un div que contendrá el iframe:
    // 1. Mostrar el modal
    this.isFirmaModalVisible = true;
    // 2. Crear el iframe dentro del modal (o si ya está en el HTML, solo establecer su src y name)
    const iframeName = 'formFirmaDigital'; // Este nombre debe ser el 'target' del formulario.

    // Asegúrate de que en tu HTML tienes un iframe con este nombre o ID
    // <div *ngIf="isFirmaModalVisible" class="modal">
    //   <iframe [src]="firmaWidgetUrl" [name]="iframeName" id="firmaIframe"></iframe>
    //   <button (click)="closeFirmaModal()">Cerrar</button>
    // </div>

    // Aquí simplemente abrimos la URL en el iframe
    this.firmaWidgetUrl = this.sanitizer.bypassSecurityTrustResourceUrl(widgetActionUrl);


    // Esperar a que el iframe se cargue y esté disponible en el DOM
    // Esto es crucial si el iframe se añade dinámicamente o si el modal tarda en renderizarse.
    setTimeout(() => {
        // Asegúrate de que el iframe ya exista en el DOM.
        // Podrías tener un iframe en tu HTML con name="formFirmaDigital"
        const iframeElement = document.querySelector(`iframe[name="${iframeName}"]`) as HTMLIFrameElement;
        if (!iframeElement) {
            console.error('El iframe con nombre "formFirmaDigital" no se encontró en el DOM.');
            this.alertService.error('Error al iniciar el proceso de firma: no se encontró el iframe.', { autoClose: false });
            this.closeFirmaModal();
            return;
        }

        const form = this.renderer.createElement('form');
        this.renderer.setAttribute(form, 'id', 'formWidgetDynamic');
        this.renderer.setAttribute(form, 'method', 'post');
        this.renderer.setAttribute(form, 'target', iframeName); // Apunta al iframe por su nombre
        this.renderer.setAttribute(form, 'action', widgetActionUrl);

        const input = this.renderer.createElement('input');
        this.renderer.setAttribute(input, 'type', 'hidden');
        this.renderer.setAttribute(input, 'name', 'params');
        this.renderer.setAttribute(input, 'value', params);
        this.renderer.appendChild(form, input);

        this.renderer.appendChild(document.body, form); // Añade el formulario al body temporalmente

        // Envía el formulario, lo que cargará el iframe con los parámetros
        (form as HTMLFormElement).submit();

        // Elimina el formulario después de enviarlo
        this.renderer.removeChild(document.body, form);
    }, 500); // Un pequeño retraso para asegurar que el iframe esté renderizado. Ajusta si es necesario.
  }


  // Esta función ahora será llamada por el event listener de window
   respuestaCHFECyN(event: MessageEvent): void {
    console.log("respuestaCHFECyN - Mensaje recibido.");
    console.log("Origin:", event.origin);
    console.log("Data:", event.data);

    const URL_FIRMA_DIGITAL =  'http://172.16.23.224'; // Debe coincidir exactamente con el origin del widget

    // Es crucial verificar el origen del mensaje para seguridad
    if (event.origin !== URL_FIRMA_DIGITAL) {
      console.warn('Mensaje de origen desconocido o no permitido:', event.origin);
      // Opcional: mostrar un mensaje de error al usuario si el origen no es el esperado.
      return;
    }

    try {
      const data = event.data;
      const resultadoJSON = JSON.parse(data);
      console.log("resultadoJSON:", resultadoJSON);

      if (resultadoJSON.resultado === 0) {
        this.alertService.success('Firma electrónica exitosa.', { autoClose: true });
        this.firmaDigital = resultadoJSON.firma;
        this.folioFirma = resultadoJSON.folio;
        this.curpFirma = resultadoJSON.curp;
        this.desFolioFirma = resultadoJSON.desFolio;

        this.isFirmaModalVisible = false;
        console.log('Firma capturada. Procediendo a enviar solicitud final.');
        this.enviarSolicitudFinalConFirma();
      } else {
        this.isFirmaModalVisible = false;
        this.alertService.error(resultadoJSON.mensaje || 'Error en el proceso de firma electrónica.', { autoClose: false });
        console.error('Error en la firma:', resultadoJSON);
      }
    } catch (e) {
      console.error('Error al parsear el mensaje del widget de firma:', e);
      this.alertService.error('Ocurrió un error al procesar la respuesta de la firma.', { autoClose: false });
      this.isFirmaModalVisible = false;
    }
  }

  enviarSolicitudFinalConFirma(): void {
    this.alertService.clear();

    if (!this.firmaDigital || !this.folioFirma || !this.curpFirma) {
        this.alertService.error('No se han obtenido los datos completos de la firma electrónica. Por favor, inténtalo de nuevo.', { autoClose: false });
        this.resetFirmaData();
        return;
    }

    const datosParaEnviar = {
      ...this.datosFormularioPrevio,
      cadenaOriginal: this.cadenaOriginalFirmada, // La que generamos en el backend y almacenamos aquí
      firmaDigital: this.firmaDigital,
      folioFirma: this.folioFirma,
      curp: this.curpFirma,
      desFolio: this.desFolioFirma,
      indAceptarBuzon: 1
    };

    this.modalService.showDialog(
      'confirm',
      'info',
      'Confirmar Envío Final',
      '¿Estás seguro de que deseas enviar esta solicitud de Acreditación y Membresía con la firma electrónica?',
      (confirmed: boolean) => {
        if (confirmed) {
          this.alertService.info('Enviando solicitud final con firma...', { autoClose: true });
          this.acreditacionMembresiaService.enviarDatosFinales(datosParaEnviar).subscribe({
            next: (response) => {
              console.log('Respuesta de envío final con firma:', response);
              if (response.codigo === 0) {
                this.alertService.success('Solicitud enviada exitosamente. Folio: ' + response.folio, { autoClose: false });
                this.acreditacionMembresiaDataService.setDatosFormularioPrevio({});
                this.router.navigate(['/home']);
              } else {
                this.alertService.error(response.mensaje || 'Error al enviar la solicitud final con firma.', { autoClose: false });
              }
            },
            error: (err) => {
              console.error('Error al enviar solicitud final con firma:', err);
              this.alertService.error('Ocurrió un error al enviar la solicitud final con firma. Por favor, inténtalo de nuevo.', { autoClose: false });
            }
          });
        } else {
          this.alertService.info('Envío de solicitud cancelado.', { autoClose: true });
          this.resetFirmaData();
        }
      },
      'Sí, Enviar',
      'No, Volver'
    );
  }

  enviarSolicitudFinal(): void {
    // Aquí es donde se inicia el proceso de firma, que a su vez llama al backend
    this.iniciarProcesoFirma();
  }

  volver(): void {
    this.router.navigate(['/acreditacion-membresia']);
  }

  closeFirmaModal(): void {
    this.isFirmaModalVisible = false;
    this.alertService.info('Proceso de firma cancelado.', { autoClose: true });
    this.resetFirmaData();
  }

  private resetFirmaData(): void {
    this.cadenaOriginalFirmada = '';
    this.firmaDigital = '';
    this.folioFirma = '';
    this.curpFirma = '';
    this.desFolioFirma = '';
  }

}
