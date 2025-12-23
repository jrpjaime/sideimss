import { Component, OnDestroy, OnInit } from '@angular/core'; // Importa OnInit
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common'; // Necesario para ngIf y otros
import { ContadorPublicoAutorizadoService } from '../services/contador-publico-autorizado.service'; // Importa el servicio
import { SolicitudBajaDto } from '../model/SolicitudBajaDto';
import { SolicitudBajaDataService, SolicitudBajaFormData } from '../services/solicitud-baja-data.service';
import { NAV } from '../../../global/navigation';
import { AlertService } from '../../../shared/services/alert.service';
import { Router } from '@angular/router';
import { LoaderService } from '../../../shared/services/loader.service';
import { SharedService } from '../../../shared/services/shared.service';
import { Subscription } from 'rxjs';
import { BaseComponent } from '../../../shared/base/base.component';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-solicitudbaja',
  standalone: true,
  imports: [CommonModule, FormsModule ], // Añade CommonModule para usar *ngIf, etc.
  templateUrl: './solicitudbaja.component.html',
  styleUrl: './solicitudbaja.component.css'
})
export class SolicitudbajaComponent extends BaseComponent implements OnInit, OnDestroy {

   private resetSubscription!: Subscription;

  solicitudBajaData: SolicitudBajaDto | null = null;
  loading: boolean = true;
  error: string | null = null;

  motivoBaja: string = '';
  maxCaracteres: number = 1000;
  caracteresRestantes: number = this.maxCaracteres;
  folioSolicitud: string | null = null;

  tieneBloqueoDictamen: boolean = false;

constructor(
  private contadorPublicoAutorizadoService: ContadorPublicoAutorizadoService,
  private solicitudBajaDataService: SolicitudBajaDataService,
  private alertService: AlertService,
  private router: Router,
  private loaderService: LoaderService,
  private authService: AuthService,
  sharedService: SharedService
) {
  super(sharedService);
}

 override ngOnInit(): void {
  // 1. Inicializamos los datos de la sesión del padre
  this.recargaParametros();

  this.resetSubscription = this.sharedService.resetSolicitudBaja$.subscribe(() => {
    this.resetFormulario();
  });

  this.cargarDatosPreviosYFolio();
}


 override   ngOnDestroy(): void {
    if (this.resetSubscription) {
      this.resetSubscription.unsubscribe();
    }
  }


    resetFormulario(): void {
    this.motivoBaja = '';
    this.solicitudBajaData = null;
    this.folioSolicitud = null;
    this.caracteresRestantes = this.maxCaracteres;
    this.error = null;
  }

/*
  async cargarDatosPreviosYFolio(): Promise<void> {
    const datosGuardados = this.solicitudBajaDataService.getDatosParaRegresar();

    if (datosGuardados) {
      // Si hay datos guardados, los usamos
      const folioPrevio = datosGuardados.folioSolicitud ?? ''; // Usa operador nullish coalescing para asegurar string

      this.folioSolicitud = folioPrevio; // Asignamos el folio a la propiedad local

      this.solicitudBajaData = {
        folioSolicitud: folioPrevio, // Aquí ya es string gracias a '??'
        datosPersonalesDto: datosGuardados.datosPersonalesDto,
        domicilioFiscalDto: datosGuardados.domicilioFiscalDto,
        datosContactoDto: datosGuardados.datosContactoDto,
        motivoBaja: datosGuardados.motivoBaja
      };
      this.motivoBaja = datosGuardados.motivoBaja;
      this.actualizarCaracteresRestantes();
      this.loading = false;
      this.loaderService.hide();
      this.solicitudBajaDataService.clearDatosParaRegresar();
    } else {
      // Si no hay datos guardados, generamos un nuevo folio y luego cargamos los datos del contador
      await this.generarFolioSolicitud(); // Esperamos a que se genere el folio
      this.cargarDatosContador(); // Luego cargamos los datos del contador
    }
  }

  cargarDatosContador(): void {
    // Si llegamos aquí, folioSolicitud ya debería estar inicializado por generarFolioSolicitud()
    if (!this.folioSolicitud) {
      // Esto solo debería pasar si hubo un error en generarFolioSolicitud, pero es una buena salvaguarda
      this.error = 'No se pudo obtener un folio de solicitud. Intente de nuevo.';
      this.loading = false;
      this.loaderService.hide();
      this.alertService.error(this.error, { autoClose: false });
      return;
    }

    this.loading = true;
    this.error = null;
    this.tieneBloqueoDictamen = false;
    this.contadorPublicoAutorizadoService.getDatosContador().subscribe({
      next: (data) => {
        // Asegúrate de que `data` no sobrescriba el folio si ya lo tiene.
        // Pero idealmente, la API debería devolver los datos del contador sin el folio
        // y nosotros lo adjuntamos.
        this.solicitudBajaData = { ...data, folioSolicitud: this.folioSolicitud!, motivoBaja: this.motivoBaja }; // Aquí usamos el operador !
        const numRegistro = parseInt(data.datosPersonalesDto.registroIMSS);

        if (!isNaN(numRegistro)) {
          this.contadorPublicoAutorizadoService.validarDictamenEnProceso(numRegistro).subscribe({
            next: (res) => {
              if (res.tieneDictamen) {
                this.tieneBloqueoDictamen = true;
                // Mostramos el mensaje de error de la imagen
                this.alertService.error('No es posible iniciar su trámite, tiene un dictamen en proceso. Favor de concluir con la presentación respectiva.', { autoClose: false });
              }
              this.loading = false;
              this.loaderService.hide();
            },
            error: () => {
              this.loading = false;
              this.loaderService.hide();
            }
          });
        } else {
          this.loading = false;
          this.loaderService.hide();
        }
      },
      error: (err) => {
        console.error('Error al cargar los datos del contador:', err);
        this.error = 'No se pudieron cargar los datos. Intente de nuevo más tarde.';
        this.loading = false;
        this.loaderService.hide();
        this.alertService.error(this.error, { autoClose: true });
      }
    });
  }

  */

async cargarDatosPreviosYFolio(): Promise<void> {
  this.loaderService.show();
  this.error = null;
  this.tieneBloqueoDictamen = false;

  // 1. OBTENEMOS EL NÚMERO DE REGISTRO DESDE LA SESIÓN (BASE COMPONENT)
  const registroValue = this.sharedService.currentNumeroRegistroImssSesionValue;
  // 2. VALIDACIÓN DE SESIÓN: Si no hay registro, redirigimos al login inmediatamente
  if (!registroValue || registroValue.trim() === '' || registroValue === '0' || registroValue === 'null') {
    console.error("Sesión inválida: No se encontró el Número de Registro IMSS en el Token.");
    this.loaderService.hide();

    // Opcional: limpiar los datos locales antes de salir
    this.authService.logout();

    this.router.navigate(['/login']);
    return;
  }

  const numRegistro = parseInt(registroValue);


  // 2. VALIDACIÓN TEMPRANA: Solo llamamos a la validación de dictamen
  this.contadorPublicoAutorizadoService.validarDictamenEnProceso(numRegistro).subscribe({
    next: (res) => {
      if (res.tieneDictamen) {
        this.tieneBloqueoDictamen = true;
        this.loading = false;
        this.loaderService.hide();
        //this.alertService.error('No es posible iniciar su trámite, tiene un dictamen en proceso. Favor de concluir con la presentación respectiva.', { autoClose: true }    );
        // AQUÍ TERMINA EL FLUJO. No se consulta nada más.
      } else {
        // 3. SI PASA LA VALIDACIÓN, CONTINUAMOS CON EL RESTO DE LAS CONSULTAS
        this.procederACargarInformacion();
      }
    },
    error: (err) => {
      this.error = 'Ocurrió un error al validar su estatus de dictámenes.';
      this.loading = false;
      this.loaderService.hide();
    }
  });
}

// Método para continuar cuando la validación es exitosa
private async procederACargarInformacion(): Promise<void> {
  const datosGuardados = this.solicitudBajaDataService.getDatosParaRegresar();

if (datosGuardados) {
  this.solicitudBajaData = {
    folioSolicitud: datosGuardados.folioSolicitud ?? '', // Forzamos a string
    datosPersonalesDto: datosGuardados.datosPersonalesDto,
    domicilioFiscalDto: datosGuardados.domicilioFiscalDto,
    datosContactoDto: datosGuardados.datosContactoDto,
    motivoBaja: datosGuardados.motivoBaja
  };

  this.folioSolicitud = datosGuardados.folioSolicitud ?? '';
  this.motivoBaja = datosGuardados.motivoBaja;
  this.actualizarCaracteresRestantes();
  this.loading = false;
  this.loaderService.hide();
  this.solicitudBajaDataService.clearDatosParaRegresar();
}
}

// Versión simplificada de carga de datos (sin volver a validar)
private cargarDatosContadorDespuesDeValidar(): void {
  this.contadorPublicoAutorizadoService.getDatosContador().subscribe({
    next: (data) => {
      this.solicitudBajaData = { ...data, folioSolicitud: this.folioSolicitud!, motivoBaja: this.motivoBaja };
      this.loading = false;
      this.loaderService.hide();
    },
    error: (err) => {
      this.error = 'No se pudieron cargar los datos del contador.';
      this.loading = false;
      this.loaderService.hide();
    }
  });
}





// Método auxiliar para evitar repetir código de asignación
private async finalizarCargaExitosa(data: any): Promise<void> {
    if (!this.folioSolicitud) {
        await this.generarFolioSolicitud();
    }
    this.solicitudBajaData = { ...data, folioSolicitud: this.folioSolicitud!, motivoBaja: this.motivoBaja };
    this.tieneBloqueoDictamen = false;
    this.loading = false;
    this.loaderService.hide();
}


  async generarFolioSolicitud(): Promise<void> {
    this.loaderService.show();
    try {
      const folio = await this.contadorPublicoAutorizadoService.getNuevoFolioSolicitud().toPromise();
      if (folio) { // Asegurarse de que el folio no sea undefined
        this.folioSolicitud = folio;
      } else {
        throw new Error('El folio de solicitud recibido es nulo o indefinido.');
      }
      console.log("Folio de solicitud de baja generado:", this.folioSolicitud);
    } catch (error) {
      console.error("Error al obtener el folio de solicitud de baja:", error);
      this.alertService.error('Error al generar el folio de solicitud. Por favor, recargue la página.', { autoClose: false });
      this.folioSolicitud = null; // O 'N/A' si prefieres mostrar algo
    } finally {
      this.loaderService.hide();
    }
  }

  actualizarCaracteresRestantes(): void {
    const longitudActual = this.motivoBaja.length;
    this.caracteresRestantes = this.maxCaracteres - longitudActual;

    if (this.caracteresRestantes < 0) {
      this.caracteresRestantes = 0;
    }
  }

  continuarConAcuse(): void {
    this.alertService.clear();

    if (!this.motivoBaja || this.motivoBaja.trim().length === 0) {
      this.alertService.error('Por favor, ingresa el motivo de la baja antes de continuar.', { autoClose: true });
      return;
    }

    // Aseguramos que solicitudBajaData y folioSolicitud no sean null aquí
    if (!this.solicitudBajaData || !this.folioSolicitud) {
      this.alertService.error('No se han cargado completamente los datos de la solicitud. Inténtalo de nuevo.', { autoClose: true });
      return;
    }

    const datosParaAcuse: SolicitudBajaFormData = {
      folioSolicitud: this.folioSolicitud, // Aquí folioSolicitud es string
      datosPersonalesDto: this.solicitudBajaData.datosPersonalesDto,
      domicilioFiscalDto: this.solicitudBajaData.domicilioFiscalDto,
      datosContactoDto: this.solicitudBajaData.datosContactoDto,
      motivoBaja: this.motivoBaja
    };

    this.solicitudBajaDataService.setSolicitudBajaData(datosParaAcuse);
    this.solicitudBajaDataService.setDatosParaRegresar(datosParaAcuse);

    this.router.navigate([NAV.solicitudbajaacuse]);
  }

  onCancelar(): void {
    this.alertService.info('Cancelando solicitud...', { autoClose: true });
    this.solicitudBajaDataService.clearSolicitudBajaData();
    this.solicitudBajaDataService.clearDatosParaRegresar();
    this.router.navigate(['/home']);
  }
}
