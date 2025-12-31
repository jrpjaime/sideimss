import { Component, OnInit, OnDestroy, Renderer2 } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription, take } from 'rxjs';
import { BaseComponent } from '../../../../shared/base/base.component';
import { AcuseParameters } from '../../model/AcuseParameters';
import { AlertService } from '../../../../shared/services/alert.service';
import { LoaderService } from '../../../../shared/services/loader.service';
import { ModificacionDatosDataService } from '../../services/ModificacionDatosDataService';
import { ContadorPublicoAutorizadoService } from '../../services/contador-publico-autorizado.service';
import { SharedService } from '../../../../shared/services/shared.service';
import { NAV } from '../../../../global/navigation';
import { PlantillaDatoDto } from '../../model/PlantillaDatoDto';
import { FirmaRequestFrontendDto } from '../../model/FirmaRequestFrontendDto';
import { FirmaRequestBackendResponse } from '../../model/FirmaRequestBackendResponse';
import { environment } from '../../../../../environments/environment';


export interface DatosAcuseExito {
  folio: string;
  urlDocumento: string;
  fechaHora: string;
  rfc: string;
  nombre: string;
}

@Component({
  selector: 'app-modificaciondatos-acuse',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './modificaciondatos-acuse.component.html',
  styleUrl: './modificaciondatos-acuse.component.css'
})
export class ModificaciondatosAcuseComponent extends BaseComponent implements OnInit, OnDestroy {

  datosFormularioPrevio: any = {};
  acusePdfUrl: SafeResourceUrl | null = null;
  loadingAcusePreview: boolean = false;
  acusePreviewError: string | null = null;

  // Variables para la firma y éxito
  folioSolicitud: string = '';
  acuseParameters: AcuseParameters | null = null;

  isFirmaModalVisible: boolean = false;
  firmaWidgetUrl: SafeResourceUrl | null = null;
  private windowMessageListener: ((event: MessageEvent) => void) | undefined;

  // Datos devueltos por la firma
  cadenaOriginalFirmada: string = '';
  fechaAcuse: string = '';
  firmaDigital: string = '';
  folioFirma: string = '';
  curpFirma: string = '';
  certificado: string = '';
  acuse: string = '';

  // Estado final
  firmaExitosa: boolean = false;
  mostrarAcuse: boolean = false;
  datosExitoAcuse: DatosAcuseExito = {
    folio: '',
    urlDocumento: '',
    fechaHora: '',
    rfc: '',
    nombre: ''
  };

  constructor(
    private router: Router,
    private renderer: Renderer2,
    private sanitizer: DomSanitizer,
    private alertService: AlertService,
    private loaderService: LoaderService,
    private modificacionDatosDataService: ModificacionDatosDataService,
    private contadorService: ContadorPublicoAutorizadoService,
    sharedService: SharedService
  ) {
    super(sharedService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.recargaParametros();

    // 1. Recuperar datos del servicio
    this.datosFormularioPrevio = this.modificacionDatosDataService.getDatosFormularioPrevio();

    // Si no hay datos (recarga de página), regresar
    if (!this.datosFormularioPrevio || Object.keys(this.datosFormularioPrevio).length === 0) {
      this.router.navigate([NAV.contadormodificaciondatos]);
      return;
    }

    this.folioSolicitud = this.datosFormularioPrevio.folioSolicitud;

    // 2. Cargar configuración y Previsualizar
    const tipoAcuse = this.datosFormularioPrevio.tipoTramite;
    this.obtenerConfiguracionYDescargarAcusePreview(tipoAcuse);

    // 3. Registrar Listener para firma (iframe)
    this.windowMessageListener = this.respuestaCHFECyN.bind(this);
    window.addEventListener('message', this.windowMessageListener);
  }

  override ngOnDestroy(): void {
    if (this.windowMessageListener) {
      window.removeEventListener('message', this.windowMessageListener);
    }
    super.ngOnDestroy();
  }

  // --- LÓGICA DE VISTA PREVIA ---

  obtenerConfiguracionYDescargarAcusePreview(tipoAcuse: string): void {
    this.loaderService.show();
    this.contadorService.getAcuseConfig(tipoAcuse).pipe(take(1)).subscribe({
      next: (params: AcuseParameters) => {
        this.acuseParameters = params;
        this.descargarAcusePreview(); // Una vez tenemos la config, descargamos el PDF
        this.loaderService.hide();
      },
      error: (err: HttpErrorResponse) => {
              this.loaderService.hide();
              console.error('Error al obtener parámetros del acuse:', err);

              // 1. Mensaje amigable que no menciona "configuración" ni "parámetros"
              let mensajeError = 'No se pudo preparar el documento para su visualización. Por favor, intente más tarde.';

              // 2. Validación por tipo de error técnico
              if (err.status === 0) {
                mensajeError = 'No se pudo establecer comunicación con el sistema. Verifique su conexión a internet.';
              } else if (err.status === 401 || err.status === 403) {
                mensajeError = 'Su sesión ha expirado. Por favor, ingrese nuevamente al portal.';
              } else if (err.status >= 500) {
                mensajeError = 'El servicio de documentos no está disponible temporalmente. Intente más tarde.';
              }

              // 3. Actualización de estado y alerta
              this.acusePreviewError = mensajeError;
              this.alertService.error(mensajeError);
            }
    });
  }






  descargarAcusePreview(): void {
    if (!this.acuseParameters) return;

    this.loadingAcusePreview = true;
    this.acusePreviewError = null;

    // Flag para backend indicando que es preview
    this.datosFormularioPrevio.vistaPrevia = "SI";

    // Unir datos del formulario + parámetros de configuración
    const datosCompletos = {
      ...this.datosFormularioPrevio,
      ...this.acuseParameters,
      rfc:this.rfcSesion
    };

     const datosJson = JSON.stringify(datosCompletos);

    const plantillaDatoDto: PlantillaDatoDto = {
      nomDocumento: this.acuseParameters['nomDocumento'],
      desVersion: this.acuseParameters['desVersion'],
      cveIdPlantillaDatos: null,
      datosJson: datosJson,
      tipoAcuse: this.datosFormularioPrevio.tipoTramite
    };

    this.contadorService.descargarAcusePreview(plantillaDatoDto).subscribe({
      next: (response: HttpResponse<Blob>) => {
        this.loadingAcusePreview = false;
        if (response.body) {
          const blob = new Blob([response.body], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          this.acusePdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        }
      },
      error: (err: HttpErrorResponse) => {
              this.loadingAcusePreview = false;
              console.error('Error al generar preview:', err);

              // 1. Definimos un mensaje base amigable
              let mensajeError = 'No se pudo generar la vista previa del documento en este momento.';

              // 2. Personalizamos según el tipo de falla técnica
              if (err.status === 0) {
                mensajeError = 'No hay conexión con el servidor de documentos. Verifique su internet.';
              } else if (err.status === 401 || err.status === 403) {
                mensajeError = 'Su sesión ha expirado. Por favor, vuelva a ingresar al sistema.';
              } else if (err.status >= 500) {
                mensajeError = 'Hubo un inconveniente en el servidor al generar el PDF. Intente más tarde.';
              }

              // 3. Asignamos a la variable de la UI y mostramos la alerta
              this.acusePreviewError = mensajeError;
              this.alertService.error(mensajeError);
            }
    });
  }

  // --- LÓGICA DE FIRMA ---

  iniciarProcesoFirma(): void {
    this.alertService.clear();
    this.loaderService.show();

    const requestDto: FirmaRequestFrontendDto = {
      rfcUsuario: this.rfcSesion,
      desFolio: this.folioSolicitud,
      desCurp: this.curpSesion,
      nombreCompleto: this.nombreCompletoSync,
      acto: "Modificación datos"
    };

    this.contadorService.generarRequestJsonFirma(requestDto).subscribe({
      next: (response: FirmaRequestBackendResponse) => {
        this.loaderService.hide();
        if (!response.error) {
          this.cadenaOriginalFirmada = response.cad_original;
          this.fechaAcuse = response.fechaParaAcuse;
          // Mostrar Modal con Iframe
          this.displayFirmaModalAndSubmitForm(response.peticionJSON);
        } else {
          this.alertService.error(response.mensaje || 'Error al generar petición de firma.');
        }
      },
      error: (err) => {
        this.loaderService.hide();
        this.alertService.error('Error de comunicación con servicio de firma.');
      }
    });
  }

  displayFirmaModalAndSubmitForm(params: string): void {
    const URL_FIRMA_DIGITAL = `${environment.firmaDigitalUrl}`;
    const widgetActionUrl = `${URL_FIRMA_DIGITAL}/firmaElectronicaWeb/widget/chfecyn`;

    this.isFirmaModalVisible = true;
    this.firmaWidgetUrl = this.sanitizer.bypassSecurityTrustResourceUrl(widgetActionUrl);

    // Pequeño delay para asegurar que el iframe existe en el DOM (si está en un *ngIf)
    setTimeout(() => {
      const iframeName = 'formFirmaDigitalMod';
      // Crear form dinámico para hacer POST al iframe
      const form = this.renderer.createElement('form');
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
    const URL_FIRMA_DIGITAL = `${environment.firmaDigitalUrl}`;
    if (event.origin !== URL_FIRMA_DIGITAL) return;

    try {
      const resultadoJSON = JSON.parse(event.data);
      if (resultadoJSON.resultado === 0) {
        this.alertService.success('Firma capturada correctamente.', { autoClose: true });

        // Guardar datos de firma
        this.firmaDigital = resultadoJSON.firma;
        this.folioFirma = resultadoJSON.folio;
        this.curpFirma = resultadoJSON.curp;
        this.certificado = resultadoJSON.certificado;
        this.acuse = resultadoJSON.acuse;

        this.isFirmaModalVisible = false;
        // PROCEDER A GUARDAR EN BD
        this.enviarSolicitudFinalConFirma();
      } else {
        this.isFirmaModalVisible = false;
        this.alertService.error(resultadoJSON.mensaje || 'Error en firma.');
      }
    } catch (e) {
      this.isFirmaModalVisible = false;
    }
  }

  closeFirmaModal(): void {
    this.isFirmaModalVisible = false;
    this.alertService.info('Proceso de firma cancelado.');
  }

  // --- ENVÍO FINAL (GUARDADO EN BD) ---

  enviarSolicitudFinalConFirma(): void {
    if (!this.acuseParameters) return;

    this.datosFormularioPrevio.vistaPrevia = "NO";

    const datosParaEnviar = {
      ...this.datosFormularioPrevio,
      cadenaOriginal: this.cadenaOriginalFirmada,
      folioFirma: this.folioFirma,
      curp: this.curpFirma,
      firmaElectronica: this.firmaDigital,
      certificado: this.certificado,
      acuse: this.acuse,
      fecha: this.fechaAcuse,
      folio: this.folioSolicitud,
      ...this.acuseParameters
    };

    const plantillaDato: PlantillaDatoDto = {
      nomDocumento: this.acuseParameters['nomDocumento'],
      desVersion: this.acuseParameters['desVersion'],
      cveIdPlantillaDatos: null,
      datosJson: JSON.stringify(datosParaEnviar),
      tipoAcuse: this.datosFormularioPrevio.tipoTramite
    };

    this.alertService.info('Enviando solicitud final...', { autoClose: true });

    // LLAMADA AL SERVICIO PARA GUARDAR
    this.contadorService.guardarModificacionDatos(plantillaDato).subscribe({
      next: (response) => {
        if (response.codigo === 0) {
          this.alertService.success('Modificación realizada exitosamente.');
          this.firmaExitosa = true;

          this.datosExitoAcuse = {
            folio: this.folioSolicitud,
            urlDocumento: response.urlDocumento,
            fechaHora: response.fechaActual,
            rfc: this.rfcSesion,
            nombre: this.nombreCompletoSync
          };

          this.modificacionDatosDataService.clearDatosFormularioPrevio();

          if (response.urlDocumento) {
            this.obtenerYMostrarAcuseFinal(response.urlDocumento);
          }
        } else {
          this.alertService.error(response.mensaje || 'Error al guardar modificación.');
        }
      },
      error: (err: HttpErrorResponse) => {
        // Mantenemos el log para nosotros los desarrolladores, pero no para el usuario
        console.error('Error detallado:', err);

        let mensajeError = 'Ocurrió un error inesperado al enviar la solicitud. Por favor, intente más tarde.';

        if (err.status === 0) {
          mensajeError = 'No se pudo establecer conexión con el servidor. Verifique su conexión a internet o intente más tarde.';
        } else if (err.status === 400) {
          mensajeError = 'La información enviada es incompleta o incorrecta. Por favor, verifique sus datos.';
        } else if (err.status === 401 || err.status === 403) {
          mensajeError = 'Su sesión ha expirado o no cuenta con los permisos necesarios. Intente ingresar nuevamente.';
        } else if (err.status === 500) {
          mensajeError = 'Hubo un problema en el servidor al procesar su solicitud. Por favor, intente más tarde.';
        }

        this.alertService.error(mensajeError);
      }


    });
  }

  obtenerYMostrarAcuseFinal(urlDocumento: string): void {
    this.loaderService.show();
    this.acusePdfUrl = null;

    this.contadorService.getAcuseParaVisualizar(urlDocumento).subscribe({
      next: (response: HttpResponse<Blob>) => {
        this.loaderService.hide();
        if (response.body) {
          const blob = new Blob([response.body], { type: 'application/pdf' });
          const url = window.URL.createObjectURL(blob);
          this.acusePdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
          this.mostrarAcuse = true;
        }
      },
      error: (err: HttpErrorResponse) => {
              this.loaderService.hide();
              console.error('Error al visualizar acuse:', err);

              // 1. Mensaje amigable.
              // Nota que confirmamos que el trámite fue exitoso aunque el PDF no cargue.
              let mensajeError = 'Su trámite se registró con éxito, pero no se pudo visualizar el acuse en este momento.';

              // 2. Personalización por error técnico
              if (err.status === 0) {
                mensajeError = 'No hay conexión para descargar su acuse.';
              } else if (err.status === 404) {
                mensajeError = 'No se puede descargar su acuse reintente mas tarde.';
              } else if (err.status >= 500) {
                mensajeError = 'No se puede descargar su acuse reintente mas tarde.';
              }

              this.alertService.error(mensajeError);
            }
    });
  }

  regresar(): void {
    this.router.navigate([NAV.contadormodificaciondatos]);
  }

  salirDelTramite(): void {
     this.modificacionDatosDataService.clearDatosFormularioPrevio();
     this.router.navigate(['/home']);
  }
}
