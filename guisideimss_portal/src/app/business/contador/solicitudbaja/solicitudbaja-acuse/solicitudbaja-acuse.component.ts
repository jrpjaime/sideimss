import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Component, OnDestroy, OnInit, Renderer2 } from '@angular/core';
import { NAV } from '../../../../global/navigation';
import { PlantillaDatoDto } from '../../model/PlantillaDatoDto';
import { environment } from '../../../../../environments/environment';
import { FirmaRequestBackendResponse } from '../../model/FirmaRequestBackendResponse';
import { FirmaRequestFrontendDto } from '../../model/FirmaRequestFrontendDto';
import { AcuseParameters } from '../../model/AcuseParameters';
import { SolicitudBajaDataService, SolicitudBajaFormData } from '../../services/solicitud-baja-data.service';
import { ContadorPublicoAutorizadoService } from '../../services/contador-publico-autorizado.service';
import { AlertService } from '../../../../shared/services/alert.service';
import { LoaderService } from '../../../../shared/services/loader.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { SharedService } from '../../../../shared/services/shared.service';
import { DatosAcuseExito } from '../../acreditacionymembresia/acreditacionymembresia-acuse/acreditacionymembresia-acuse.component';
import { BaseComponent } from '../../../../shared/base/base.component';
import { CommonModule } from '@angular/common';
import { take } from 'rxjs';


@Component({
  selector: 'app-solicitudbaja-acuse',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './solicitudbaja-acuse.component.html',
  styleUrl: './solicitudbaja-acuse.component.css'
})
export class SolicitudbajaAcuseComponent extends BaseComponent implements OnInit, OnDestroy {


  datosSolicitudBaja: SolicitudBajaFormData | null = null;
  acusePdfUrl: SafeResourceUrl | null = null;
  loadingAcusePreview: boolean = false;
  acusePreviewError: string | null = null;
  acuseFinalError: string | null = null;

  private readonly TIPO_ACUSE = 'ACUSE_SOLICITUD_BAJA'; // Define el tipo de acuse
  acuseParameters: AcuseParameters | null = null;

  isFirmaModalVisible: boolean = false;
  firmaWidgetUrl: SafeResourceUrl | null = null;
  private windowMessageListener: ((event: MessageEvent) => void) | undefined;

  cadenaOriginalFirmada: string = '';
  fechaFirma: string = '';
  firmaDigital: string = '';
  folioFirma: string = ''; // El folio de la firma electrónica
  curpFirma: string = '';
  certificado: string = '';
  acuse: string = '';
  fechaAcuse: string = ''; // Fecha proporcionada por el backend para el acuse

  firmaExitosa: boolean = false;
  datosExitoAcuse: DatosAcuseExito = {
    folio: '',
    urlDocumento: '',
    fechaHora: '',
    rfc: '',
    nombre: ''
  };
  mostrarAcuseFinal: boolean = false; // Controla la visibilidad del iframe del acuse final

  constructor(
    private solicitudBajaDataService: SolicitudBajaDataService,
    private contadorPublicoAutorizadoService: ContadorPublicoAutorizadoService,
    private alertService: AlertService,
    private loaderService: LoaderService,
    private sanitizer: DomSanitizer,
    private router: Router,
    private renderer: Renderer2, // Inyectar Renderer2 para manipulación del DOM
    sharedService: SharedService
  ) {
    super(sharedService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.recargaParametros(); // Carga los datos del usuario logueado

    this.datosSolicitudBaja = this.solicitudBajaDataService.getSolicitudBajaData();

    if (!this.datosSolicitudBaja) {
      this.alertService.error('No se encontraron datos para la solicitud de baja. Por favor, inicia el trámite desde el principio.', { autoClose: false });
      this.router.navigate([NAV.contadorsolicitudbaja]); // Redirigir al formulario
      return;
    }

    this.obtenerConfiguracionYDescargarAcusePreview();

    // REGISTRAR EL LISTENER PARA MENSAJES DEL IFRAME DE FIRMA
    this.windowMessageListener = this.respuestaCHFECyN.bind(this);
    window.addEventListener('message', this.windowMessageListener);
  }

  override ngOnDestroy(): void {
    if (this.windowMessageListener) {
      window.removeEventListener('message', this.windowMessageListener);
    }
    super.ngOnDestroy();
  }

  obtenerConfiguracionYDescargarAcusePreview(): void {
    this.loaderService.show();
    this.contadorPublicoAutorizadoService.getAcuseConfig(this.TIPO_ACUSE).pipe(take(1)).subscribe({
      next: (params: AcuseParameters) => {
        this.acuseParameters = params;
        this.descargarAcusePreview();
        this.loaderService.hide();
      },
      error: (errorResponse: HttpErrorResponse) => {
        this.loaderService.hide();
        console.error('Error al obtener la configuración del acuse de baja:', errorResponse);
        let errorMessage = 'Error al obtener la configuración del acuse de baja. Inténtalo de nuevo más tarde.';
        if (errorResponse.error && typeof errorResponse.error === 'object' && errorResponse.error.mensaje) {
          errorMessage = errorResponse.error.mensaje;
        }
        this.acusePreviewError = errorMessage;
        this.alertService.error(this.acusePreviewError, { autoClose: false });
      }
    });
  }

  descargarAcusePreview(): void {
    if (!this.acuseParameters || !this.datosSolicitudBaja) {
      this.acusePreviewError = 'La configuración del acuse o los datos de la solicitud no están disponibles.';
      this.alertService.error(this.acusePreviewError, { autoClose: true });
      return;
    }

    this.loadingAcusePreview = true;
    this.acusePreviewError = null;
    this.alertService.clear();

    // --- 1. GENERAR FECHA ACTUAL EN ESPAÑOL ---
    const now = new Date();
    const meses = [
      'enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio',
      'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'
    ];
    
    const dia = now.getDate();
    const mes = meses[now.getMonth()];
    const anio = now.getFullYear();
    // Formatear hora a dos dígitos
    const horas = now.getHours().toString().padStart(2, '0');
    const minutos = now.getMinutes().toString().padStart(2, '0');
    const segundos = now.getSeconds().toString().padStart(2, '0');

    // Resultado: "10 de diciembre de 2025, 16:50:00"
    const fechaActual = `${dia} de ${mes} de ${anio}, ${horas}:${minutos}:${segundos}`;
    // ------------------------------------------    

    const datosParaPlantilla = {
      vistaPrevia: "SI",
      ...this.datosSolicitudBaja, // Datos específicos de la solicitud de baja
      ...this.acuseParameters,    // Parámetros generales del acuse
      nombreCompleto: this.nombreCompletoSync,
      RFC: this.rfcSesion,
      CURP: this.curpSesion,
      numeroRegistroImss: this.numeroRegistroImssSesion,
      fecha: fechaActual

    };

    const datosJson = JSON.stringify(datosParaPlantilla);

    const plantillaDatoDto: PlantillaDatoDto = {
      nomDocumento: this.acuseParameters['nomDocumento'],
      desVersion: this.acuseParameters['desVersion'],
      cveIdPlantillaDatos: null,
      datosJson: datosJson,
      tipoAcuse: this.TIPO_ACUSE
    };

    console.log('PlantillaDatoDto enviado para preview de solicitud de baja:', plantillaDatoDto);

    this.contadorPublicoAutorizadoService.descargarAcusePreview(plantillaDatoDto).subscribe({
      next: (response: HttpResponse<Blob>) => {
        this.loadingAcusePreview = false;

         console.log(' this.loadingAcusePreview: ',  this.loadingAcusePreview);
        if (response.body) {

          const blob = new Blob([response.body], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
           console.log(' url: ',  url);
          this.acusePdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        } else {
          this.acusePreviewError = 'No se recibió ningún documento para previsualizar el acuse de baja.';
          this.alertService.error(this.acusePreviewError, { autoClose: false });
        }
      },
      error: (errorResponse: HttpErrorResponse) => {
        this.loadingAcusePreview = false;
        console.error('Error al descargar el preview del acuse de baja:', errorResponse);
        let errorMessage = 'Error al generar la previsualización del acuse de baja. Inténtalo de nuevo más tarde.';

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
    this.alertService.clear();

    const rfcUsuario = this.rfcSesion;
    const desFolio = this.datosSolicitudBaja?.folioSolicitud; // Usar el folio de la solicitud
    const desCurp = this.curpSesion;
    const nombreCompleto = this.nombreCompletoSync;


    if (!rfcUsuario || !desFolio || !desCurp || !nombreCompleto) {
      this.alertService.error('Faltan datos de usuario o de la solicitud para iniciar la firma. Recarga la página si persiste el problema.', { autoClose: false });
      return;
    }

    this.loaderService.show();

    const firmaRequestDto: FirmaRequestFrontendDto = {
      rfcUsuario: rfcUsuario,
      desFolio: desFolio,
      desCurp: desCurp,
      nombreCompleto: nombreCompleto,
      acto: "Solicitud de Baja"
    };

    this.contadorPublicoAutorizadoService.generarRequestJsonFirma(firmaRequestDto).subscribe({
      next: (response: FirmaRequestBackendResponse) => {
        this.loaderService.hide();
        if (!response.error) {
          this.cadenaOriginalFirmada = response.cad_original;
          let peticionJSON = response.peticionJSON;
          this.fechaAcuse = response.fechaParaAcuse; // Guardar la fecha del backend

          console.log("Cadena Original para firma (Baja) recibida del backend:", this.cadenaOriginalFirmada);
          console.log("Peticion JSON para firma (Baja) recibida del backend:", peticionJSON);
          console.log("Fecha para Acuse (Baja):", this.fechaAcuse);

          this.displayFirmaModalAndSubmitForm(peticionJSON);
        } else {
          console.error('Error del backend al generar JSON de firma para Baja:', response.mensaje);
          this.alertService.error(response.mensaje || 'Error al generar la petición de firma electrónica.', { autoClose: true });
        }
      },
      error: (errorResponse: HttpErrorResponse) => {
        this.loaderService.hide();
        console.error('Error al conectar con el backend para generar JSON de firma para Baja:', errorResponse);
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

    this.isFirmaModalVisible = true;
    this.firmaWidgetUrl = this.sanitizer.bypassSecurityTrustResourceUrl(widgetActionUrl);

    setTimeout(() => {
      const iframeName = 'formFirmaDigital';
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
      this.renderer.setAttribute(form, 'target', iframeName);
      this.renderer.setAttribute(form, 'action', widgetActionUrl);

      const input = this.renderer.createElement('input');
      this.renderer.setAttribute(input, 'type', 'hidden');
      this.renderer.setAttribute(input, 'name', 'params');
      this.renderer.setAttribute(input, 'value', params);
      this.renderer.appendChild(form, input);

      this.renderer.appendChild(document.body, form);

      (form as HTMLFormElement).submit();

      this.renderer.removeChild(document.body, form);
    }, 500);
  }

  respuestaCHFECyN(event: MessageEvent): void {
    console.log("respuestaCHFECyN - Mensaje recibido para Solicitud de Baja.");

    const URL_FIRMA_DIGITAL = `${environment.firmaDigitalUrl}`;
    if (event.origin !== URL_FIRMA_DIGITAL) {
      console.warn('Mensaje de origen desconocido o no permitido:', event.origin);
      return;
    }

    try {
      const data = event.data;
      const resultadoJSON = JSON.parse(data);
      console.log("resultadoJSON (Solicitud de Baja):", resultadoJSON);

      if (resultadoJSON.resultado === 0) {
        this.alertService.success('Firma electrónica exitosa para Solicitud de Baja.', { autoClose: true });
        this.firmaDigital = resultadoJSON.firma;
        this.folioFirma = resultadoJSON.folio; // Folio de la transacción de firma
        this.curpFirma = resultadoJSON.curp;
        this.certificado = resultadoJSON.certificado;
        this.acuse = resultadoJSON.acuse; // El acuse generado por el servicio de firma (puede ser un hash o un ID)

        this.isFirmaModalVisible = false;
        console.log('Firma capturada para Solicitud de Baja. Procediendo a enviar solicitud final.');
        this.enviarSolicitudFinalConFirma();
      } else {
        this.isFirmaModalVisible = false;
        this.alertService.error(resultadoJSON.mensaje || 'Error en el proceso de firma electrónica para Solicitud de Baja.', { autoClose: false });
        console.error('Error en la firma (Solicitud de Baja):', resultadoJSON);
      }
    } catch (e) {
      console.error('Error al parsear el mensaje del widget de firma (Solicitud de Baja):', e);
      this.alertService.error('Ocurrió un error al procesar la respuesta de la firma.', { autoClose: false });
      this.isFirmaModalVisible = false;
    }
  }

  enviarSolicitudFinalConFirma(): void {
    this.alertService.clear();
    const nombreCompletoSesion = this.nombreCompletoSync;

    if (!this.firmaDigital || !this.folioFirma || !this.curpFirma || !this.acuseParameters || !this.datosSolicitudBaja) {
      this.alertService.error('No se han obtenido los datos completos de la firma o de la solicitud para enviar. Por favor, inténtalo de nuevo.', { autoClose: false });
      this.resetFirmaData();
      return;
    }

    console.log('datosSolicitudBaja.folioSolicitud para envío final:', this.datosSolicitudBaja.folioSolicitud);

    const datosParaSerializar = {
      vistaPrevia: "NO",
      ...this.datosSolicitudBaja, // Incluye todos los datos de la solicitud de baja
      cadenaOriginal: this.cadenaOriginalFirmada,
      folioFirma: this.folioFirma, // Folio del proceso de firma
      CURP: this.curpFirma,
      RFC: this.rfcSesion,
      nombreCompleto: nombreCompletoSesion,
      numeroRegistroImss: this.numeroRegistroImssSesion,
      firmaElectronica: this.firmaDigital,
      selloDigitalIMSS: "",
      certificado: this.certificado,
      acuse: this.acuse,
      fecha: this.fechaAcuse, // Fecha proporcionada por el backend en la respuesta de firma
      numTramiteNotaria: this.datosSolicitudBaja.folioSolicitud, // Usar el folio de la solicitud de baja
      ...this.acuseParameters // Parámetros del acuse
    };

    const datosJsonString = JSON.stringify(datosParaSerializar);

    const plantillaDato: PlantillaDatoDto = {
      nomDocumento: this.acuseParameters['nomDocumento'],
      desVersion: this.acuseParameters['desVersion'],
      cveIdPlantillaDatos: null,
      datosJson: datosJsonString,
      tipoAcuse: this.TIPO_ACUSE
    };

    this.alertService.info('Enviando solicitud de baja con firma...', { autoClose: true });
    this.loaderService.show();

    this.contadorPublicoAutorizadoService.solicitudBaja(plantillaDato).subscribe({
      next: (response) => {
        this.loaderService.hide();
        console.log('Respuesta de envío final de Solicitud de Baja con firma:', response);
        if (response.codigo === 0) {
          this.alertService.success('Solicitud de Baja enviada exitosamente.', { autoClose: true });

          // Ocultar el acuse previo y mostrar los datos de éxito
          this.firmaExitosa = true;
          this.datosExitoAcuse = {
            folio:  this.datosSolicitudBaja?.folioSolicitud || 'N/A', // Usar URL o folio
            urlDocumento: response.urlDocumento,
            fechaHora: response.fechaActual ,
            rfc: this.rfcSesion,
            nombre: nombreCompletoSesion
          };
          this.solicitudBajaDataService.clearSolicitudBajaData(); // Limpiar datos temporales
          this.solicitudBajaDataService.clearDatosParaRegresar(); // Asegurar limpieza total

          if (response && response.urlDocumento) {
            this.obtenerYMostrarAcuse(response.urlDocumento);
          } else {
            this.alertService.warn('La URL del acuse final no se recibió en la respuesta. No se podrá visualizar.', { autoClose: true });
            this.mostrarAcuseFinal = false;
          }

        } else {
          this.alertService.error(response.mensaje || 'Error al enviar la solicitud de baja con firma.', { autoClose: true });
          this.resetFirmaData();
        }
      },
      error: (err) => {
        this.loaderService.hide();
        console.error('Error al enviar solicitud de baja con firma:', err);
        this.alertService.error('Ocurrió un error al enviar la solicitud de baja con firma. Por favor, inténtalo de nuevo.', { autoClose: true });
        this.resetFirmaData();
      }
    });
  }

  enviarSolicitudFinal(): void {
    // Este método inicia el proceso de firma, que a su vez llama al backend
    this.iniciarProcesoFirma();
  }

  regresarMantenerDatos(): void {
    // Si el usuario presiona regresar, los datos se guardaron en el servicio SolicitudBajaDataService
    this.router.navigate([NAV.contadorsolicitudbaja]);
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
    this.certificado = '';
    this.acuse = '';
    this.fechaAcuse = '';
  }

  salirDelTramite(): void {
    this.solicitudBajaDataService.clearSolicitudBajaData();
    this.solicitudBajaDataService.clearDatosParaRegresar();
    this.router.navigate(['/home']);
  }

  obtenerYMostrarAcuse(urlDocumento: string) {
    this.alertService.clear();
    this.loaderService.show();
    this.acuseFinalError = null;
    this.acusePdfUrl = null; // Limpia el preview

    this.contadorPublicoAutorizadoService.getAcuseParaVisualizar(urlDocumento).subscribe({
      next: (response: HttpResponse<Blob>) => {
        this.loaderService.hide();
        if (response.body) {
          const file = new Blob([response.body], { type: 'application/pdf' });
          const fileURL = URL.createObjectURL(file);
          this.acusePdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(fileURL);
          this.mostrarAcuseFinal = true; // Mostrar el iframe del acuse final
          console.log('Acuse final de Solicitud de Baja cargado exitosamente.');
        } else {
          this.acuseFinalError = 'No se recibió ningún documento para el acuse final de Solicitud de Baja.';
          this.alertService.error(this.acuseFinalError, { autoClose: false });
          console.error('No se recibió body en la respuesta del acuse final de Solicitud de Baja.');
        }
      },
      error: (errorResponse: HttpErrorResponse) => {
        this.loaderService.hide();
        console.error('Error al obtener el acuse final de Solicitud de Baja para visualización:', errorResponse);
        let errorMessage = 'No se pudo cargar el acuse final de Solicitud de Baja. Inténtalo de nuevo más tarde.';

        if (errorResponse.error instanceof Blob) {
          const reader = new FileReader();
          reader.onload = () => {
            try {
              const errorBody = JSON.parse(reader.result as string);
              errorMessage = errorBody.message || errorBody.error || errorMessage;
            } catch (e) {
              console.warn('No se pudo parsear el error de Solicitud de Baja como JSON:', reader.result);
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
        this.mostrarAcuseFinal = false; // Asegurarse de que el iframe no se muestre si hay un error
      }
    });
  }





  /**
   * Este método se encarga de descargar el acuse firmado.
   * Utiliza la URL del acuse que ya se ha obtenido del backend.
   */
  descargarAcuseFirmado(): void {
    if (!this.acusePdfUrl) {
      this.alertService.error('No hay un acuse final disponible para descargar.', { autoClose: true });
      return;
    }

    this.loaderService.show();
    this.alertService.clear();


    let urlDocumentoParaDescargar: string = '';
    if (this.datosExitoAcuse && this.datosExitoAcuse.urlDocumento) {
      urlDocumentoParaDescargar = this.datosExitoAcuse.urlDocumento;
    }

    if (!urlDocumentoParaDescargar) {
      this.loaderService.hide();
      this.alertService.error('No se pudo obtener la referencia del acuse para descargar.', { autoClose: true });
      return;
    }

    this.contadorPublicoAutorizadoService.getAcuseParaVisualizar(urlDocumentoParaDescargar).subscribe({
      next: (response: HttpResponse<Blob>) => {
        this.loaderService.hide();
        if (response.body) {
          const blob = new Blob([response.body], { type: 'application/pdf' });
          const filename = `Acuse_Solicitud_Baja_${this.datosExitoAcuse.folio || 'firmado'}.pdf`;

          // Crea un enlace temporal y simula un clic para descargar
          const a = document.createElement('a');
          const url = window.URL.createObjectURL(blob);
          a.href = url;
          a.download = filename;
          document.body.appendChild(a);
          a.click();
          window.URL.revokeObjectURL(url); // Libera la URL del objeto
          document.body.removeChild(a);    // Elimina el enlace temporal
          this.alertService.success('Acuse descargado exitosamente.', { autoClose: true });
        } else {
          this.alertService.error('No se recibió ningún documento para descargar el acuse firmado.', { autoClose: false });
        }
      },
      error: (errorResponse: HttpErrorResponse) => {
        this.loaderService.hide();
        console.error('Error al descargar el acuse firmado:', errorResponse);
        let errorMessage = 'Error al descargar el acuse firmado. Por favor, inténtalo de nuevo más tarde.';

        if (errorResponse.error instanceof Blob) {
          const reader = new FileReader();
          reader.onload = () => {
            try {
              const errorBody = JSON.parse(reader.result as string);
              errorMessage = errorBody.message || errorBody.error || errorMessage;
            } catch (e) {
              console.warn('No se pudo parsear el error como JSON:', reader.result);
            }
            this.alertService.error(errorMessage, { autoClose: false });
          };
          reader.readAsText(errorResponse.error);
        } else if (errorResponse.message) {
          errorMessage = errorResponse.message;
        }
        this.alertService.error(errorMessage, { autoClose: false });
      }
    });
  }


}
