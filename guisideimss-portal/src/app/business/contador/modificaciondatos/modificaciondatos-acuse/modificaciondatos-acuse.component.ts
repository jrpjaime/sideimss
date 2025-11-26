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
import { ModalService } from '../../../../shared/services/modal.service';
import { ModificacionDatosDataService } from '../../services/ModificacionDatosDataService';
import { ContadorPublicoAutorizadoService } from '../../services/contador-publico-autorizado.service';
import { AcreditacionMembresiaService } from '../../services/acreditacion-membresia.service';
import { SharedService } from '../../../../shared/services/shared.service';
import { PlantillaDatoDto } from '../../model/PlantillaDatoDto';
import { FirmaRequestFrontendDto } from '../../model/FirmaRequestFrontendDto';
import { FirmaRequestBackendResponse } from '../../model/FirmaRequestBackendResponse';
import { environment } from '../../../../../environments/environment';

 

// Servicios específicos
 

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
  acuseFinalError: string | null = null;
  folioSolicitud: string = '';
  
  // Configuración del Acuse
  acuseParameters: AcuseParameters | null = null;
  
  // Firma Electrónica
  isFirmaModalVisible: boolean = false;
  firmaWidgetUrl: SafeResourceUrl | null = null;
  private windowMessageListener: ((event: MessageEvent) => void) | undefined;
  
  // Datos de Firma capturados
  cadenaOriginalFirmada: string = '';
  fechaAcuse: string = '';
  fechaFirma: string = '';
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
    private modalService: ModalService,
    private modificacionDatosDataService: ModificacionDatosDataService,
    private contadorService: ContadorPublicoAutorizadoService,
    // Usamos AcreditacionService si ahí están los métodos genéricos de firma, 
    
    private acreditacionService: AcreditacionMembresiaService, 
    sharedService: SharedService
  ) {
    super(sharedService);
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.recargaParametros();

    // 1. Obtener datos
    this.datosFormularioPrevio = this.modificacionDatosDataService.getDatosFormularioPrevio();
    
    // Validación: Si no hay datos, regresar al inicio
    if (!this.datosFormularioPrevio || Object.keys(this.datosFormularioPrevio).length === 0) {
      this.router.navigate(['/home']);
      return;
    }

    this.folioSolicitud = this.datosFormularioPrevio.folioSolicitud;

    // 2. Cargar configuración y preview
    // Nota: Usamos el tipoTramite que definimos en el componente anterior (ej: 'MODIFICACION_CONTACTO')
    // Asegúrate de que este string exista en tu BD de plantillas o usa uno genérico 'MODIFICACION_DATOS'
    const tipoAcuse = this.datosFormularioPrevio.tipoTramite || 'MODIFICACION_DATOS';
    this.obtenerConfiguracionYDescargarAcusePreview(tipoAcuse);

    // 3. Registrar listener para firma
    this.windowMessageListener = this.respuestaCHFECyN.bind(this);
    window.addEventListener('message', this.windowMessageListener);
  }

  override ngOnDestroy(): void {
    if (this.windowMessageListener) {
      window.removeEventListener('message', this.windowMessageListener);
    }
    super.ngOnDestroy();
  }

  // --- LÓGICA DE PREVISUALIZACIÓN ---

  obtenerConfiguracionYDescargarAcusePreview(tipoAcuse: string): void {
    this.loaderService.show();
    // Usamos el servicio del contador (o el que tenga getAcuseConfig)
    this.contadorService.getAcuseConfig(tipoAcuse).pipe(take(1)).subscribe({
      next: (params: AcuseParameters) => {
        this.acuseParameters = params;
        this.descargarAcusePreview();
        this.loaderService.hide();
      },
      error: (err) => {
        this.loaderService.hide();
        console.error('Error config acuse', err);
        this.acusePreviewError = 'Error al cargar configuración del acuse.';
        this.alertService.error(this.acusePreviewError);
      }
    });
  }

  descargarAcusePreview(): void {
    if (!this.acuseParameters) return;

    this.loadingAcusePreview = true;
    this.acusePreviewError = null;
    this.datosFormularioPrevio.vistaPrevia = "SI";

    const datosCompletos = {
      ...this.datosFormularioPrevio,
      ...this.acuseParameters
    };

    const plantillaDatoDto: PlantillaDatoDto = {
      nomDocumento: this.acuseParameters['nomDocumento'],
      desVersion: this.acuseParameters['desVersion'],
      cveIdPlantillaDatos: null,
      datosJson: JSON.stringify(datosCompletos),
      tipoAcuse: this.datosFormularioPrevio.tipoTramite // O el que corresponda
    };

    this.contadorService.descargarAcusePreview(plantillaDatoDto).subscribe({
      next: (response: HttpResponse<Blob>) => {
        this.loadingAcusePreview = false;
        if (response.body) {
          const url = window.URL.createObjectURL(new Blob([response.body], { type: 'application/pdf' }));
          this.acusePdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        }
      },
      error: (err) => {
        this.loadingAcusePreview = false;
        this.acusePreviewError = 'Error al generar la previsualización.';
        this.alertService.error(this.acusePreviewError);
      }
    });
  }

  // --- LÓGICA DE FIRMA ---

  iniciarProcesoFirma(): void {
    this.alertService.clear();
    this.loaderService.show();

    const requestDto: FirmaRequestFrontendDto = {
      rfcUsuario: this.rfcSesion,
      desFolio: this.folioSolicitud, // Usamos folio de solicitud o generar uno nuevo de firma
      desCurp: this.curpSesion,
      nombreCompleto: this.nombreCompletoSync
    };

    // Usamos el servicio que tenga generarRequestJsonFirma
    this.contadorService.generarRequestJsonFirma(requestDto).subscribe({
      next: (response: FirmaRequestBackendResponse) => {
        this.loaderService.hide();
        if (!response.error) {
          this.cadenaOriginalFirmada = response.cad_original;
          this.fechaAcuse = response.fechaParaAcuse;
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

    setTimeout(() => {
      const iframeName = 'formFirmaDigitalMod'; // Nombre único
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
        this.alertService.success('Firma exitosa.', { autoClose: true });
        this.firmaDigital = resultadoJSON.firma;
        this.folioFirma = resultadoJSON.folio;
        this.curpFirma = resultadoJSON.curp;
        this.certificado = resultadoJSON.certificado;
        this.acuse = resultadoJSON.acuse;
        
        this.isFirmaModalVisible = false;
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
    this.alertService.info('Firma cancelada.');
  }

  // --- ENVÍO FINAL ---

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
      ...this.acuseParameters
    };

    const plantillaDato: PlantillaDatoDto = {
      nomDocumento: this.acuseParameters['nomDocumento'],
      desVersion: this.acuseParameters['desVersion'],
      cveIdPlantillaDatos: null,
      datosJson: JSON.stringify(datosParaEnviar),
      tipoAcuse: this.datosFormularioPrevio.tipoTramite
    };

    this.alertService.info('Enviando solicitud de modificación...', { autoClose: true });

    // AQUÍ LLAMAS AL ENDPOINT REAL DE GUARDAR MODIFICACIÓN
    // Nota: Estoy asumiendo que existe 'guardarModificacion' en ContadorService,
    // similar a 'acreditacionmembresia' o 'solicitudBaja'.
    this.contadorService.solicitudBaja(plantillaDato).subscribe({ 
      // ^^^ OJO: CAMBIA 'solicitudBaja' por el método real para guardar modificaciones, ej: guardarModificacionDatos(plantillaDato)
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
            this.obtenerYMostrarAcuse(response.urlDocumento);
          }
        } else {
          this.alertService.error(response.mensaje || 'Error al guardar modificación.');
        }
      },
      error: (err) => {
        console.error(err);
        this.alertService.error('Error al enviar solicitud final.');
      }
    });
  }

  obtenerYMostrarAcuse(urlDocumento: string): void {
    this.loaderService.show();
    this.acusePdfUrl = null; // Limpiar preview
    
    this.contadorService.getAcuseParaVisualizar(urlDocumento).subscribe({
      next: (response: HttpResponse<Blob>) => {
        this.loaderService.hide();
        if (response.body) {
          const url = window.URL.createObjectURL(new Blob([response.body], { type: 'application/pdf' }));
          this.acusePdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
          this.mostrarAcuse = true;
        }
      },
      error: (err) => {
        this.loaderService.hide();
        this.acuseFinalError = 'No se pudo cargar el acuse final.';
        this.alertService.error(this.acuseFinalError);
      }
    });
  }

  regresar(): void {
    // Regresar a la pantalla anterior
    // Opcional: Si quieres mantener datos editados, el DataService ya los tiene.
    this.router.navigate(['/contador/modificacion-datos']); // <--- AJUSTA TU RUTA
  }
  
  salir(): void {
     this.modificacionDatosDataService.clearDatosFormularioPrevio();
     this.router.navigate(['/home']);
  }
}