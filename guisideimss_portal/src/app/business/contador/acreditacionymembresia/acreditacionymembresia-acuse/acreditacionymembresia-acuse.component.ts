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
import { LoaderService } from '../../../../shared/services/loader.service';
import { AcuseConfig } from '../../model/AcuseConfig ';
import { AcuseParameters } from '../../model/AcuseParameters';
import { NAV } from '../../../../global/navigation';
import { environment } from '../../../../../environments/environment';
import { FirmaRequestFrontendDto } from '../../model/FirmaRequestFrontendDto';
import { FirmaRequestBackendResponse } from '../../model/FirmaRequestBackendResponse';



export interface DatosAcuseExito {
  folio: string;
  fechaHora: string;
  rfc: string;
  nombre: string;
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
  acuseFinalError: string | null = null;
  //nombreCompleto: string = '';

   acuseParameters: AcuseParameters | null = null;
  private readonly TIPO_ACUSE = 'ACREDITACION_MEMBRESIA';

  isFirmaModalVisible: boolean = false;
  firmaWidgetUrl: SafeResourceUrl | null = null;
  private messageSubscription: Subscription | undefined;

  fechaAcuse: string = '';
  cadenaOriginalFirmada: string = ''; // Esta será la cadena que armemos en el frontend
  fechaFirma: string = '';
  firmaDigital: string = '';
  folioFirma: string = '';
  curpFirma: string = '';
  //firma: string = '';
  certificado: string = '';
  acuse: string = '';

  mostrarAcuse: boolean = false;

  public Object = Object;

  private windowMessageListener: ((event: MessageEvent) => void) | undefined;

    firmaExitosa: boolean = false; // Controla la visibilidad del acuse de éxito
  datosExitoAcuse: DatosAcuseExito = { // Almacenará los datos a mostrar
    folio: '',
    fechaHora: '',
    rfc: '',
    nombre: ''
  };

  constructor (
    private fb: FormBuilder,
    private router : Router,
    private renderer: Renderer2,
    private catalogosService: CatalogosService,
    private modalService: ModalService,
    private acreditacionMembresiaService: AcreditacionMembresiaService, // Inyectar el servicio
    private alertService: AlertService,
    private loaderService: LoaderService,
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

    this.obtenerConfiguracionYDescargarAcusePreview();




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

  obtenerConfiguracionYDescargarAcusePreview(): void {
    console.log('Llamando a obtenerConfiguracionYDescargarAcusePreview');
    this.loaderService.show();
    // Cambiar el tipo esperado en la suscripción
    this.acreditacionMembresiaService.getAcuseConfig(this.TIPO_ACUSE).pipe(take(1)).subscribe({
      next: (params: AcuseParameters) => { // <--- Tipo esperado cambiado a AcuseParameters
        this.acuseParameters = params; // <--- Almacenar en acuseParameters
        console.log('Parámetros del acuse recibidos:', this.acuseParameters);
        this.descargarAcusePreview();
        this.loaderService.hide();
      },
      error: (errorResponse: HttpErrorResponse) => {
        this.loaderService.hide();
        console.error('Error al obtener la configuración del acuse:', errorResponse);
        let errorMessage = 'Error al obtener la configuración del acuse. Inténtalo de nuevo más tarde.';
        if (errorResponse.error && typeof errorResponse.error === 'object' && errorResponse.error.mensaje) {
          errorMessage = errorResponse.error.mensaje;
        }
        this.acusePreviewError = errorMessage;
        this.alertService.error(this.acusePreviewError, { autoClose: false });
      }
    });
  }

  descargarAcusePreview(): void {
    console.log('descargarAcusePreview');

    if (!this.acuseParameters) {
      console.log('entro en if (!this.acuseParameters) {');
      this.acusePreviewError = 'La configuración del acuse no ha sido cargada.';
      this.alertService.error(this.acusePreviewError, { autoClose: true });
      return;
    }

    console.log('esta antes de this.loadingAcusePreview = true;');
    this.loadingAcusePreview = true;
    this.acusePreviewError = null;
    this.alertService.clear();

    this.datosFormularioPrevio.vistaPrevia = "SI";

    // Integrar acuseParameters directamente en datosCompletos
    const datosCompletos = {
      ...this.datosFormularioPrevio,
      ...this.acuseParameters
    };

    const datosJson = JSON.stringify(datosCompletos);

    const plantillaDatoDto: PlantillaDatoDto = {
      nomDocumento: this.acuseParameters['nomDocumento'],
      desVersion: this.acuseParameters['desVersion'],
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

    const rfcUsuario = this.rfcSesion;
    const desFolio = this.folioFirma + "PRUEBAFOLIOFIRMA";
    const desCurp = this.curpSesion;
    const nombreCompleto = this.nombreCompletoSync;


    if (!rfcUsuario) {
      this.alertService.error('No se pudo obtener el RFC del usuario. Por favor, inténtalo de nuevo.', { autoClose: false });
      return;
    }

    if (!desFolio) {
        this.alertService.error('El folio de firma es requerido.', { autoClose: false });
        return;
    }

    if (!desCurp) {
        this.alertService.error('La CURP de firma es requerida.', { autoClose: false });
        return;
    }

    if (!nombreCompleto) {
        this.alertService.error('No se pudo obtener el nombre completo del usuario.', { autoClose: false });
        return;
    }

    this.loaderService.show();


    const firmaRequestDto: FirmaRequestFrontendDto = {
      rfcUsuario: rfcUsuario,
      desFolio: desFolio,
      desCurp: desCurp,
      nombreCompleto: nombreCompleto
    };


    this.acreditacionMembresiaService.generarRequestJsonFirma(firmaRequestDto).subscribe({
      next: (response: FirmaRequestBackendResponse) => {
        this.loaderService.hide();
        if (!response.error) {
          this.cadenaOriginalFirmada = response.cad_original; // Almacenar la cadena original del backend
          let peticionJSON = response.peticionJSON;
          this.fechaAcuse=response.fechaParaAcuse;

          console.log("Cadena Original recibida del backend:", this.cadenaOriginalFirmada);
          console.log("Peticion JSON recibida del backend:", peticionJSON);
          console.log("fechaParaAcuse:", response.fechaParaAcuse);


          this.displayFirmaModalAndSubmitForm(peticionJSON);
        } else {
          console.error('Error del backend al generar JSON de firma:', response.mensaje);
          this.alertService.error(response.mensaje || 'Error al generar la petición de firma electrónica desde el backend.', { autoClose: true });
        }
      },
      error: (errorResponse: HttpErrorResponse) => {
        this.loaderService.hide();
        console.error('Error al conectar con el backend para generar JSON de firma:', errorResponse);
        let errorMessage = 'Error de comunicación con el servicio de firma. Por favor, inténtalo de nuevo más tarde.';
        if (errorResponse.error && typeof errorResponse.error === 'object' && errorResponse.error.mensaje) {
          errorMessage = errorResponse.error.mensaje;
        }
        this.alertService.error(errorMessage, { autoClose: true });
      }
    });
  }
  displayFirmaModalAndSubmitForm(params: string): void {

    const URL_FIRMA_DIGITAL = `${environment.firmaDigitalUrl}`;
    const widgetActionUrl = `${URL_FIRMA_DIGITAL}/firmaElectronicaWeb/widget/chfecyn`;


    console.log("Params: "+ params);
    console.log("widgetActionUrl: "+ widgetActionUrl);



    // 1. Mostrar el modal
    this.isFirmaModalVisible = true;
    // 2. Crear el iframe dentro del modal (o si ya está en el HTML, solo establecer su src y name)
    const iframeName = 'formFirmaDigital'; // Este nombre debe ser el 'target' del formulario.



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

    const URL_FIRMA_DIGITAL = `${environment.firmaDigitalUrl}`;  // Debe coincidir exactamente con el origin del widget
     console.log("Debe coincidir exactamente con el origin del widget URL_FIRMA_DIGITAL:", URL_FIRMA_DIGITAL);

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
       // this.firma = resultadoJSON.firma;
        this.certificado = resultadoJSON.certificado;
        this.acuse = resultadoJSON.acuse;


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
      const nombreCompletoSesion = this.nombreCompletoSync;
      if (!this.firmaDigital || !this.folioFirma || !this.curpFirma) {
          this.alertService.error('No se han obtenido los datos completos de la firma electrónica. Por favor, inténtalo de nuevo.', { autoClose: false });
          this.resetFirmaData();
          return;
      }
      if (!this.acuseParameters) {
        this.alertService.error('La configuración del acuse no está disponible para enviar la solicitud final.', { autoClose: false });
        this.resetFirmaData();
        return;
      }

      this.datosFormularioPrevio.vistaPrevia = "NO";
      const datosParaSerializar = {
        ...this.datosFormularioPrevio,
        cadenaOriginal: this.cadenaOriginalFirmada,
        folioFirma: this.folioFirma,
        curp: this.curpFirma,
        desFolio: this.folioFirma,
        firmaElectronica:  this.firmaDigital,
        selloDigitalIMSS: "",
        certificado:  this.certificado,
        acuse:  this.acuse,
        fecha: this.fechaAcuse,
        ...this.acuseParameters
      };

      // 2. Serializar el objeto a una cadena JSON
      const datosJsonString = JSON.stringify(datosParaSerializar);

      // 3. Crear una instancia de PlantillaDatoDto
      const plantillaDato: PlantillaDatoDto = {
        nomDocumento: this.acuseParameters['nomDocumento'],
        desVersion: this.acuseParameters['desVersion'],
        cveIdPlantillaDatos: null, // O el valor que corresponda si lo tienes
        datosJson: datosJsonString, // Asignar la cadena JSON aquí
        tipoAcuse: 'ACREDITACION_MEMBRESIA' // Asegúrate de usar el valor correcto para TipoAcuse
      };

      this.alertService.info('Enviando solicitud final con firma...', { autoClose: true });
      // 4. Enviar el objeto PlantillaDatoDto
      this.acreditacionMembresiaService.acreditacionmembresia(plantillaDato).subscribe({
        next: (response) => {
          console.log('Respuesta de envío final con firma:', response);
          if (response.codigo === 0) {
            this.alertService.success('Solicitud enviada exitosamente.' , { autoClose: true });

            // Ocultar el acuse previo y mostrar los datos de éxito
            this.firmaExitosa = true;
            this.datosExitoAcuse = {
                folio: response.urlDocumento,
                fechaHora: response.fechaActual,
                rfc: this.rfcSesion,
                nombre: nombreCompletoSesion
            };
            this.acreditacionMembresiaDataService.setDatosFormularioPrevio({});
            this.acreditacionMembresiaDataService.clearDatosParaRegresar();

            if (response && response.urlDocumento) {
              this.obtenerYMostrarAcuse(response.urlDocumento);
            } else {
              this.alertService.warn('La URL del acuse final no se recibió en la respuesta. No se podrá visualizar.', { autoClose: true });
            }

          } else {
            this.alertService.error(response.mensaje || 'Error al enviar la solicitud final con firma.', { autoClose: true });
          }
        },
        error: (err) => {
          console.error('Error al enviar solicitud final con firma:', err);
          this.alertService.error('Ocurrió un error al enviar la solicitud final con firma. Por favor, inténtalo de nuevo.', { autoClose: true });
          this.resetFirmaData();
        }
      });

    }




  enviarSolicitudFinal(): void {
    // Aquí es donde se inicia el proceso de firma, que a su vez llama al backend
    this.iniciarProcesoFirma();
  }

  regresarMantenerDatos(): void {
    this.router.navigate([NAV.contadoracreditacionymembresia]);
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
    //this.firma = '';
    this.certificado = '';
  }


  salirDelTramite(): void {
      this.acreditacionMembresiaDataService.setDatosFormularioPrevio({});
      this.acreditacionMembresiaDataService.clearDatosParaRegresar(); // Asegurar limpieza total
      this.router.navigate(['/home']);
  }

obtenerYMostrarAcuse(urlDocumento: string) {
    this.alertService.clear();
    this.loaderService.show(); // Muestra el loader mientras se carga el acuse final
    this.acuseFinalError = null; // Limpiar cualquier error previo del acuse final

    // Limpia la URL del preview para no mostrar el preview mientras se carga el final
    this.acusePdfUrl = null;

    this.acreditacionMembresiaService.getAcuseParaVisualizar(urlDocumento).subscribe({
      next: (response: HttpResponse<Blob>) => { // Especificar el tipo de respuesta
        this.loaderService.hide(); // Oculta el loader
        if (response.body) {
          const file = new Blob([response.body], { type: 'application/pdf' });
          const fileURL = URL.createObjectURL(file);
          this.acusePdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(fileURL);
          this.mostrarAcuse = true; // Mostrar el iframe del acuse final
          console.log('Acuse final cargado exitosamente.');
        } else {
          this.acuseFinalError = 'No se recibió ningún documento para el acuse final.';
          this.alertService.error(this.acuseFinalError, { autoClose: false });
          console.error('No se recibió body en la respuesta del acuse final.');
        }
      },
      error: (errorResponse: HttpErrorResponse) => { // Especificar tipo de error
        this.loaderService.hide(); // Oculta el loader
        console.error('Error al obtener el acuse para visualización:', errorResponse);
        let errorMessage = 'No se pudo cargar el acuse final. Inténtalo de nuevo más tarde.';

        if (errorResponse.error instanceof Blob) {
            const reader = new FileReader();
            reader.onload = () => {
                try {
                    const errorBody = JSON.parse(reader.result as string);
                    errorMessage = errorBody.message || errorBody.error || errorMessage;
                } catch (e) {
                    console.warn('No se pudo parsear el error como JSON:', reader.result);
                }
                this.acuseFinalError = errorMessage;
                this.alertService.error(this.acuseFinalError, { autoClose: false });
            };
            reader.readAsText(errorResponse.error);
            return;
        } else if (errorResponse.message) {
          errorMessage = errorResponse.message;
        }
        this.acuseFinalError = errorMessage;
        this.alertService.error(this.acuseFinalError, { autoClose: false });
        this.mostrarAcuse = false; // Asegurarse de que el iframe no se muestre si hay un error
      }
    });
  }

}
