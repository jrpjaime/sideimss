import { Component, OnInit } from '@angular/core'; // Importa OnInit
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common'; // Necesario para ngIf y otros
import { ContadorPublicoAutorizadoService } from '../services/contador-publico-autorizado.service'; // Importa el servicio
import { SolicitudBajaDto } from '../model/SolicitudBajaDto';
import { SolicitudBajaDataService, SolicitudBajaFormData } from '../services/solicitud-baja-data.service';
import { NAV } from '../../../global/navigation';
import { AlertService } from '../../../shared/services/alert.service';
import { Router } from '@angular/router';
import { LoaderService } from '../../../shared/services/loader.service';

@Component({
  selector: 'app-solicitudbaja',
  standalone: true,
  imports: [CommonModule, FormsModule ], // Añade CommonModule para usar *ngIf, etc.
  templateUrl: './solicitudbaja.component.html',
  styleUrl: './solicitudbaja.component.css'
})
export class SolicitudbajaComponent implements OnInit {

  solicitudBajaData: SolicitudBajaDto | null = null;
  loading: boolean = true;
  error: string | null = null;

  motivoBaja: string = '';
  maxCaracteres: number = 1000;
  caracteresRestantes: number = this.maxCaracteres;
  folioSolicitud: string | null = null;

  constructor(
    private contadorPublicoAutorizadoService: ContadorPublicoAutorizadoService,
    private solicitudBajaDataService: SolicitudBajaDataService,
    private alertService: AlertService,
    private router: Router,
    private loaderService: LoaderService
  ) { }

  ngOnInit(): void {
    this.loaderService.show();
    this.cargarDatosPreviosYFolio();
  }

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
    this.contadorPublicoAutorizadoService.getDatosContador().subscribe({
      next: (data) => {
        // Asegúrate de que `data` no sobrescriba el folio si ya lo tiene.
        // Pero idealmente, la API debería devolver los datos del contador sin el folio
        // y nosotros lo adjuntamos.
        this.solicitudBajaData = { ...data, folioSolicitud: this.folioSolicitud!, motivoBaja: this.motivoBaja }; // Aquí usamos el operador !
        this.loading = false;
        this.loaderService.hide();
        console.log('Datos del contador cargados:', this.solicitudBajaData);
      },
      error: (err) => {
        console.error('Error al cargar los datos del contador:', err);
        this.error = 'No se pudieron cargar los datos del contador. Intente de nuevo más tarde.';
        this.loading = false;
        this.loaderService.hide();
        this.alertService.error(this.error, { autoClose: false });
      }
    });
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
