import { Component, OnDestroy, OnInit } from '@angular/core';
import { CatalogosContadorService } from '../services/catalogos-contador.service';
import { AlertService } from '../../../shared/services/alert.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize, Subscription } from 'rxjs';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ContadorPublicoAutorizadoService } from '../services/contador-publico-autorizado.service';
import { BaseComponent } from '../../../shared/base/base.component';
import { ColegioContadorDto } from '../model/ColegioContadorDto';
import { SharedService } from '../../../shared/services/shared.service';
import { TipoDatoContadorDto } from '../model/TipoDatoContadorDto';
import { Router } from '@angular/router';
import { RfcColegioRequestDto } from '../model/RfcColegioRequestDto';
import { RfcColegioResponseDto } from '../model/RfcColegioResponseDto';
import { ModalService } from '../../../shared/services/modal.service';
import { AcreditacionMembresiaService } from '../services/acreditacion-membresia.service';
import { DocumentoIndividualResponseDto } from '../model/DocumentoIndividualResponseDto ';
import { TipoSociedadFormaParteDto } from '../model/TipoSociedadFormaParteDto';
import { CargoContadorDto } from '../model/CargoContadorDto';
import { DespachoContadorDto } from '../model/DespachoContadorDto';
import { SolicitudBajaDto } from '../model/SolicitudBajaDto';
import { LoaderService } from '../../../shared/services/loader.service';
import { DatosContadorData } from '../model/DatosContadorData';
import { DatePipe } from '@angular/common';
import { ModificacionDatosDataService } from '../services/ModificacionDatosDataService';
import { NAV } from '../../../global/navigation';
import { DespachoRequestDto } from '../model/DespachoRequestDto';
import { DespachoResponseDto } from '../model/DespachoResponseDto';

@Component({
  selector: 'app-modificaciondatos',
  standalone: true,
  imports: [ CommonModule,
    FormsModule ],
  templateUrl: './modificaciondatos.component.html',
  styleUrl: './modificaciondatos.component.css'
})
export class ModificaciondatosComponent extends BaseComponent implements OnInit, OnDestroy {

      // Expresión regular para validar formato de correo estándar
  private emailPattern: RegExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  docMemSi: string | null = null;
  docMemNo: string | null = null;

  tiposDatosContador: TipoDatoContadorDto[] = [];
  selectedTipoDato: string = '';
  folioSolicitud: string | null = null;
  loadingFolio: boolean = false;


  colegioContador: ColegioContadorDto | null = null;
  loadingColegio: boolean = false;
  mostrarSeccionColegio: boolean = false;

  habilitarEdicionRfcColegio: boolean = false;
  nuevoRfcColegio: string = '';
  rfcColegioValido: boolean = true;

  rfcColegioOriginal: string | null = null;
  razonSocialOriginal: string | null = null;
  busquedaColegioRealizada: boolean = false;

  selectedFileConstancia: File | null = null;
  fileConstanciaUploadSuccess: boolean = false;
  fileConstanciaHdfsPath: string | null = null; // Guardará el path HDFS en Base64
  fileConstanciaError: string | null = null;
  loadingFileConstancia: boolean = false; // Para el spinner del botón Adjuntar

  formSubmitted: boolean = false; // Para controlar cuándo mostrar los mensajes de validación




  // --- PROPIEDADES para la sección de DESPACHO ---
  mostrarSeccionDespacho: boolean = false; // Controla la visibilidad de toda la sección del despacho
  deseaActualizarDespacho: boolean | null = null; // null: no ha respondido, true/false: sí/no desea actualizar

  tiposSociedad: TipoSociedadFormaParteDto[] = []; // Catálogo de tipos de sociedad
  cargosContador: CargoContadorDto[] = []; // Catálogo de cargos

  despachoContador: DespachoContadorDto | null = null; // Datos actuales/a modificar del despacho
  loadingDespacho: boolean = false;

  // Propiedades para los campos de edición/selección
  selectedTipoSociedad: string = ''; // ngModel para el select de tipo de sociedad
  nuevoRfcDespacho: string = ''; // ngModel para el RFC del despacho a buscar
  rfcDespachoValido: boolean = true; // Validación del formato del RFC
  selectedCargoDesempena: string = ''; // ngModel para el select de cargo
  telefonoFijoDespacho: string = ''; // ngModel para el teléfono fijo

  habilitarCamposDespacho: boolean = false; // Controla si los campos son editables


  datosContadorData: DatosContadorData | null = null;
  loading: boolean = true;
  error: string | null = null;


  mostrarSeccionPersonales: boolean = false;




  // --- PROPIEDADES para la sección de CONTACTO ---
   loadingContacto: boolean = false;
  deseaActualizarContacto: boolean | null = null;
  nuevoCorreoElectronico2: string = '';
  confirmarCorreoElectronico2: string = ''; //  campo de confirmación
  nuevoCorreoElectronico3: string = '';
  confirmarCorreoElectronico3: string = ''; //  campo de confirmación
  nuevoTelefono2: string = '';

  nuevacedulaprofesional: string = '';



  originalTipoSociedad: string | null = null;
  originalRfcDespacho: string | null = null;
  originalCargoDesempena: string | null = null;
  originalTelefonoFijo: string | null = null;
  originalNombreDespacho: string | null = null;

  tieneTrabajadores: string = ''; // '' | 'Si' | 'No'
  numeroTrabajadores: string = '';

  // Originales para comparación
  originalTieneTrabajadores: string | null = null;
  originalNumeroTrabajadores: string | null = null;

  busquedaDespachoRealizada: boolean = false;

  private resetSubscription: Subscription | null = null;

  constructor(
    private catalogosContadorService: CatalogosContadorService,
    private alertService: AlertService,
    private contadorPublicoAutorizadoService: ContadorPublicoAutorizadoService,
    private router: Router,
    private modalService: ModalService,
    private acreditacionMembresiaService: AcreditacionMembresiaService,
    private loaderService: LoaderService,
    private modificacionDatosDataService: ModificacionDatosDataService,
    private datePipe: DatePipe,
    sharedService: SharedService
  )  {
    super(sharedService);
    this.recargaParametros();
  }

  override ngOnInit(): void {
    super.ngOnInit();
    this.generarFolioSolicitud();
    this.cargarTiposDatosContador();

    this.cargarTiposSociedad();
    this.cargarCargosContador();
    console.log('RFC de sesión en ModificaciondatosComponent:', this.rfcSesion);
    this.verificarYRestaurarDatosPrevios();
        this.resetSubscription = this.sharedService.resetModificacionDatos$.subscribe(() => {
      console.log('Reiniciando vista de modificación de datos desde el menú...');
      this.resetearVista();
    });
  }



    // Limpiar suscripción al salir ---
  override ngOnDestroy(): void {
    super.ngOnDestroy();
    if (this.resetSubscription) {
      this.resetSubscription.unsubscribe();
    }
  }

  // Método que limpia todo ---
  resetearVista(): void {
    // 1. Reiniciar el Select principal
    this.selectedTipoDato = '';

    // 2. Ocultar todas las secciones
    this.mostrarSeccionColegio = false;
    this.mostrarSeccionDespacho = false;
    this.mostrarSeccionPersonales = false;

    // 3. Limpiar objetos de datos cargados
    this.colegioContador = null;
    this.despachoContador = null;

    // 4. Resetear banderas de UI
    this.habilitarEdicionRfcColegio = false;
    this.busquedaColegioRealizada = false;
    this.habilitarCamposDespacho = false;
    this.deseaActualizarDespacho = null;
    this.deseaActualizarContacto = null;

    // 5. Limpiar datos temporales del servicio (Para que no se autorrellene al recargar)
    this.modificacionDatosDataService.clearDatosFormularioPrevio();

    // 6. Recargar validaciones básicas o limpiar errores
    this.formSubmitted = false;
    this.alertService.clear();
  }



  /**
   * Verifica si hay datos en el servicio (al regresar del Acuse) y restaura el estado.
   */
  verificarYRestaurarDatosPrevios(): void {
    const datosPrevios = this.modificacionDatosDataService.getDatosFormularioPrevio();

    // Si no hay datos o no hay un objeto de estado guardado, no hacemos nada
    if (!datosPrevios || !datosPrevios.state) {
      return;
    }

    const estado = datosPrevios.state;
    const tipoSolicitud = datosPrevios.tipoSolicitud;

    console.log('Restaurando estado anterior:', tipoSolicitud, estado);

    // 1. Restaurar Estado para COLEGIO
    if (tipoSolicitud === 'COLEGIO') {
      this.selectedTipoDato = '3'; // Opción Colegio
      this.mostrarSeccionColegio = true;
      this.consultarDatosColegio(); // Carga datos originales

      // Restaurar variables de edición
      this.habilitarEdicionRfcColegio = true;
      this.nuevoRfcColegio = estado.nuevoRfcColegio;
      this.rfcColegioValido = true;

      // Restaurar estado del archivo (Simulamos que ya se subió para que la UI lo muestre)
      if (estado.fileConstanciaHdfsPath) {
        this.fileConstanciaHdfsPath = estado.fileConstanciaHdfsPath;
        this.fileConstanciaUploadSuccess = true;
        // Creamos un objeto dummy para que se vea el nombre, aunque el objeto File real no persiste
        this.selectedFileConstancia = { name: estado.nomArchivoConstancia } as File;
      }
    }

    // 2. Restaurar Estado para DESPACHO
    else if (tipoSolicitud === 'DESPACHO') {
      this.selectedTipoDato = '2'; // Opción Despacho
      this.mostrarSeccionDespacho = true;
      this.consultarDatosDespacho(); // Carga datos originales

      // Restaurar banderas de UI
      this.deseaActualizarDespacho = true;
      this.habilitarCamposDespacho = true;

      // Restaurar variables del formulario
      this.selectedTipoSociedad = estado.selectedTipoSociedad;
      this.tieneTrabajadores = estado.tieneTrabajadores;
      this.numeroTrabajadores = estado.numeroTrabajadores;

      // Si era despacho (no independiente), restaurar el resto
      if (estado.selectedTipoSociedad !== '2') {
        this.nuevoRfcDespacho = estado.nuevoRfcDespacho;
        this.rfcDespachoValido = true;
        this.selectedCargoDesempena = estado.selectedCargoDesempena;
        this.telefonoFijoDespacho = estado.telefonoFijoDespacho;
        // Si teníamos un nombre de despacho buscado, lo restauramos visualmente
        if (this.despachoContador && estado.razonSocialDespachoVisual) {
             this.despachoContador.nombreRazonSocial = estado.razonSocialDespachoVisual;
        }
      }
    }

    // 3. Restaurar Estado para CONTACTO (Datos Personales)
    else if (tipoSolicitud === 'CONTACTO') {
      this.selectedTipoDato = '1'; // Opción Datos Personales
      this.mostrarSeccionPersonales = true;
      this.cargarDatosContador(); // Carga datos originales

      // Restaurar banderas de UI
      this.deseaActualizarContacto = true;

      // Restaurar variables
      this.nuevoCorreoElectronico2 = estado.nuevoCorreoElectronico2;
      this.confirmarCorreoElectronico2 = estado.nuevoCorreoElectronico2; // Asumimos que coincidían
      this.nuevoCorreoElectronico3 = estado.nuevoCorreoElectronico3;
      this.confirmarCorreoElectronico3 = estado.nuevoCorreoElectronico3;
      this.nuevoTelefono2 = estado.nuevoTelefono2;
      this.nuevacedulaprofesional = estado.nuevacedulaprofesional;
    }
  }


  cargarTiposDatosContador(): void {
    this.catalogosContadorService.getTiposDatosContador().subscribe({
      next: (data: TipoDatoContadorDto[]) => {
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

    // Resetear todas las secciones
    this.busquedaDespachoRealizada = false;
    this.colegioContador = null;
    this.mostrarSeccionColegio = false;
    this.despachoContador = null; // Limpiar datos del despacho
    this.mostrarSeccionDespacho = false;
    this.deseaActualizarDespacho = null; // Resetear la respuesta
    this.habilitarCamposDespacho = false; // Deshabilitar campos de edición

    // También resetear los campos de edición
    this.selectedTipoSociedad = '';
    this.nuevoRfcDespacho = '';
    this.rfcDespachoValido = true;
    this.selectedCargoDesempena = '';
    this.telefonoFijoDespacho = '';
     this.mostrarSeccionPersonales = false; // Ocultar sección personales

    if (this.selectedTipoDato === '1') { // Datos Personales
        this.mostrarSeccionPersonales = true;
        this.cargarDatosContador();
    } else if (this.selectedTipoDato === '3') { // Datos del Colegio
      this.mostrarSeccionColegio = true;
      this.consultarDatosColegio();
    } else if (this.selectedTipoDato === '2') { // Datos del Despacho
      this.mostrarSeccionDespacho = true;
      this.consultarDatosDespacho();
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

          // GUARDAMOS LA REFERENCIA ORIGINAL ---
          this.rfcColegioOriginal = data.rfcColegio;
          this.razonSocialOriginal = data.razonSocial;
          console.log('Datos del colegio obtenidos:', this.colegioContador);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error al obtener los datos del colegio:', error);
          this.alertService.error('Error al consultar los datos del colegio. Inténtalo de nuevo más tarde.');
          this.colegioContador = null; // Limpiar datos previos en caso de error
        }
      });
  }




 /**
   * Cancela la edición. Si existe un archivo subido, lo elimina del servidor antes de salir.
   */
  cancelarEdicionColegio(): void {
    // 1. Verificamos si hay un archivo cargado en el servidor (tiene path HDFS)
    if (this.fileConstanciaHdfsPath) {

      // Opcional: Avisar al usuario que se está limpiando
      // this.alertService.info('Eliminando archivo adjunto y cancelando...', { autoClose: true });

      this.acreditacionMembresiaService.deleteDocument(this.fileConstanciaHdfsPath).subscribe({
        next: () => {
          console.log('Archivo temporal eliminado correctamente al cancelar.');
          this.resetearVariablesYSalir();
        },
        error: (error) => {
          console.error('Error al eliminar el archivo al cancelar:', error);
          // Aunque falle la eliminación (ej. error de red),
          // permitimos al usuario salir para no bloquear la navegación.
          this.resetearVariablesYSalir();
        }
      });
    } else {
      // 2. Si no hay archivo, simplemente limpiamos y salimos
      this.resetearVariablesYSalir();
    }
  }

  /**
   * Método auxiliar para limpiar variables de estado y redirigir
   */
  private resetearVariablesYSalir(): void {
    this.habilitarEdicionRfcColegio = false;
    this.limpiarNuevoRfcColegio();

    // Limpiar variables del archivo
    this.fileConstanciaUploadSuccess = false;
    this.fileConstanciaHdfsPath = null;
    this.selectedFileConstancia = null;
    this.fileConstanciaError = null;

    // Limpiar el input file del HTML si existe
    const fileInput = document.getElementById('constanciaMembresia') as HTMLInputElement;
    if (fileInput) {
        fileInput.value = '';
    }

  //  this.alertService.info('Edición cancelada.');
    this.router.navigate(['/home']);
  }







  respuestaActualizarColegio(respuesta: boolean): void {
    if (respuesta) {
      this.habilitarEdicionRfcColegio = true;
      // Precarga el RFC actual del colegio en el campo de edición
      this.nuevoRfcColegio = this.colegioContador?.rfcColegio || '';

      // 2. AL INICIAR EDICIÓN, RESETEAMOS LA BANDERA
      // Si el RFC precargado ya es válido, podríamos ponerlo en true,
      // pero como la regla es "clic en Buscar", lo dejamos en false para obligar la búsqueda.
      this.busquedaColegioRealizada = false;


    } else {
      this.habilitarEdicionRfcColegio = false;
      this.selectedFileConstancia = null;
      this.router.navigate(['/home']); // Redirigir a /home si la respuesta es No
    }
  }

buscarNuevoColegio(): void {
    if (!this.nuevoRfcColegio) {
      this.alertService.warn('Por favor, ingresa un RFC para buscar.');
      this.rfcColegioValido = false;
      return;
    }

    // Validar formato de RFC de persona moral antes de la búsqueda
    if (!this.validarRfcPersonaMoral(this.nuevoRfcColegio)) {
      // El mensaje se muestra en el HTML, aquí solo marcamos la bandera
      this.rfcColegioValido = false;

      //this.alertService.error('El RFC ingresado no cumple con el formato de 12 caracteres para Persona Moral.');
      return;
    }

    this.rfcColegioValido = true; // Resetear estado de validación si pasa el formato
    console.log('Buscando colegio con nuevo RFC:', this.nuevoRfcColegio);

    this.loadingColegio = true;
    this.busquedaColegioRealizada = false;

    const request: RfcColegioRequestDto = { rfc: this.nuevoRfcColegio };

    this.catalogosContadorService.getDatoRfcColegio(request)
      .pipe(finalize(() => this.loadingColegio = false))
      .subscribe({
        next: (data: RfcColegioResponseDto) => {
          this.colegioContador = {
            rfcColegio: data.rfc,
            razonSocial: data.nombreRazonSocial,
            // Otros campos de ColegioContadorDto se pueden inicializar aquí si es necesario
          };
          this.alertService.success('Datos del nuevo colegio cargados exitosamente.');
          this.busquedaColegioRealizada = true;
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error al obtener los datos del nuevo colegio:', error);

          if (error.status === 404) {
            this.alertService.error('No se encontraron datos de colegio para el RFC proporcionado. Por favor, verifica el RFC e intenta de nuevo.');
          } else {
            this.alertService.error('Error al consultar los datos del nuevo colegio. Inténtalo de nuevo más tarde.');
          }

          // si existe el objeto, solo limpia la razón social para mostrar que no hubo coincidencia
          if (this.colegioContador) {
             this.colegioContador.razonSocial = '';
             // Opcional: Puedes asignar el RFC que se intentó buscar, o dejar el anterior.
             // this.colegioContador.rfcColegio = this.nuevoRfcColegio;
          } else {
             // Si por alguna razón era nulo, lo inicializamos vacío para que pinte el formulario
             this.colegioContador = { rfcColegio: '', razonSocial: '' };
          }

          // Mantenemos la bandera en false para bloquear el botón de Siguiente
          this.busquedaColegioRealizada = false;
        }
      });
  }

limpiarNuevoRfcColegio(): void {
    // 1. Limpiar campos de texto (RFC y Razón Social)
    this.nuevoRfcColegio = '';
    this.rfcColegioValido = true;
    this.busquedaColegioRealizada = false;

    if (this.colegioContador) {
      this.colegioContador.razonSocial = '';
      this.colegioContador.rfcColegio = '';
    }

    // 2. Eliminar archivo del servidor si ya se había subido con éxito
    if (this.fileConstanciaHdfsPath) {
      this.acreditacionMembresiaService.deleteDocument(this.fileConstanciaHdfsPath).subscribe({
        next: () => {
          console.log('Archivo anterior eliminado del servidor al limpiar.');
        },
        error: (err) => {
          console.error('Error al intentar eliminar el archivo del servidor:', err);
        }
      });
    }

    // 3. Resetear variables del archivo localmente
    this.selectedFileConstancia = null;
    this.fileConstanciaUploadSuccess = false;
    this.fileConstanciaHdfsPath = null;
    this.fileConstanciaError = null;

    // 4. Limpiar el input file del HTML para que se vea vacío visualmente
    const fileInput = document.getElementById('constanciaMembresia') as HTMLInputElement;
    if (fileInput) {
        fileInput.value = '';
    }

    this.alertService.info('Campos y archivo adjunto limpiados.');
  }

   /**
   * Valida si el RFC ingresado corresponde a una Persona Moral (12 caracteres).
   * Formato esperado: XXXNNNNNNNNN (3 letras, 6 números, 3 alfanuméricos)
   * @param rfc El RFC a validar.
   * @returns true si el RFC es válido para persona moral, false en caso contrario.
   */
  validarRfcPersonaMoral(rfc: string): boolean {
    if (!rfc) {
      return false;
    }
    // Expresión regular para RFC de persona moral (12 caracteres)
    // ^[A-Z]{3}  -> 3 letras mayúsculas
    // [0-9]{6}  -> 6 dígitos
    // [A-Z0-9]{3}$ -> 3 caracteres alfanuméricos (homoclave)
    const rfcMoralRegex = /^[A-Z&Ñ]{3}[0-9]{6}[A-Z0-9]{3}$/;
    const isValid = rfcMoralRegex.test(rfc.toUpperCase());
    console.log(`Validando RFC '${rfc}': ${isValid ? 'Válido' : 'Inválido'}`);
    return isValid;
  }

  /**
   * Valida si el RFC ingresado corresponde a una Persona Física (13 caracteres) o Persona Moral (12 caracteres).
   * Formato Persona Moral: XXXNNNNNNNNN (3 letras, 6 números, 3 alfanuméricos)
   * Formato Persona Física: XXXXNNNNNNNNN (4 letras, 6 números, 3 alfanuméricos)
   * @param rfc El RFC a validar.
   * @returns true si el RFC es válido, false en caso contrario.
   */
  validarRfc(rfc: string): boolean {
      if (!rfc) {
          return false;
      }
      const rfcUpper = rfc.toUpperCase();

      // Expresión regular para RFC de persona moral (12 caracteres)
      const rfcMoralRegex = /^[A-Z&Ñ]{3}[0-9]{6}[A-Z0-9]{3}$/;
      // Expresión regular para RFC de persona física (13 caracteres)
      const rfcFisicaRegex = /^[A-Z&Ñ]{4}[0-9]{6}[A-Z0-9]{3}$/;

      const isValid = rfcMoralRegex.test(rfcUpper) || rfcFisicaRegex.test(rfcUpper);
      console.log(`Validando RFC '${rfc}': ${isValid ? 'Válido' : 'Inválido'}`);
      return isValid;
  }


  onFileSelected(event: any, controlName: string) {
    this.alertService.clear();

    const file: File = event.target.files[0];

    // Limpiar el estado de éxito/error del archivo previo al seleccionar uno nuevo
    if (controlName === 'constanciaMembresia') {
        this.fileConstanciaUploadSuccess = false;
        this.fileConstanciaHdfsPath = null;
        this.fileConstanciaError = null;
        this.selectedFileConstancia = null;
    }

    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        this.alertService.error('El archivo excede el tamaño máximo permitido de 5MB.', { autoClose: true });
        event.target.value = null; // Limpiar el input file
        return;
      }
      if (file.type !== 'application/pdf') {
        this.alertService.error('Favor de seleccionar un archivo de tipo .PDF', { autoClose: true });
        event.target.value = null; // Limpiar el input file
        return;
      }

      if (controlName === 'constanciaMembresia') {
        this.selectedFileConstancia = file;
      }
    } else {
      if (controlName === 'constanciaMembresia') {
        this.selectedFileConstancia = null;
      }
    }
  }

  uploadFile(controlName: string) {
    this.alertService.clear();

    let desRfcValue = this.rfcSesion;

    if (!desRfcValue) {
      this.alertService.error('No se pudo obtener el RFC. Por favor, recarga la página o inténtalo más tarde.', { autoClose: true });
      return;
    }

    let fileToUpload: File | null = null;
    let loadingFlag: 'loadingFileConstancia';
    let fileHdfsPath: 'fileConstanciaHdfsPath';
    let fileUploadSuccess: 'fileConstanciaUploadSuccess';
    let fileError: 'fileConstanciaError';
    let documentType: string;

    if (controlName === 'constanciaMembresia') {
      fileToUpload = this.selectedFileConstancia;
      loadingFlag = 'loadingFileConstancia';
      fileHdfsPath = 'fileConstanciaHdfsPath';
      fileUploadSuccess = 'fileConstanciaUploadSuccess';
      fileError = 'fileConstanciaError';
      documentType = 'Constancia de Membresía';
    } else {
      console.error('Control de archivo desconocido:', controlName);
      return;
    }

    // Resetear estados antes de la carga
    this[fileUploadSuccess] = false;
    this[fileHdfsPath] = null;
    this[fileError] = null;

    if (!fileToUpload) {
      this.alertService.error(`Por favor, selecciona un archivo para ${documentType}.`, { autoClose: true });
      return;
    }

    this[loadingFlag] = true; // Activar spinner

    const formData = new FormData();
    formData.append('archivo', fileToUpload, fileToUpload.name);
    formData.append('desRfc', desRfcValue);
    formData.append('nomArchivo', fileToUpload.name);

    this.acreditacionMembresiaService.uploadDocument(formData).subscribe({
      next: (response: DocumentoIndividualResponseDto) => {
        this[loadingFlag] = false; // Desactivar spinner
        console.log(`Respuesta de carga para ${controlName}:`, response);

        if (response.codigo === 0 && response.desPathHdfs) {
          this[fileUploadSuccess] = true;
          this[fileHdfsPath] = response.desPathHdfs; // Guardar el path HDFS en Base64
          this[fileError] = null;
          this.alertService.success(`${documentType} "${fileToUpload?.name}" cargado exitosamente.`, { autoClose: true });
        } else {
          this[fileUploadSuccess] = false;
          this[fileHdfsPath] = null;
          this[fileError] = response.mensaje || `Error desconocido al cargar el archivo de ${documentType}.`;
          this.alertService.error(this[fileError] as string, { autoClose: true });
        }
      },
      error: (errorResponse: HttpErrorResponse) => {
        this[loadingFlag] = false; // Desactivar spinner
        console.error(`Error al cargar el archivo de ${documentType}:`, errorResponse);
        let errorMessage = `Error al cargar el archivo de ${documentType}. Inténtalo de nuevo.`;

        if (errorResponse.error instanceof Object && errorResponse.error.mensaje) {
          errorMessage = errorResponse.error.mensaje;
        } else if (errorResponse.message) {
          errorMessage = errorResponse.message;
        }

        this[fileUploadSuccess] = false;
        this[fileHdfsPath] = null;
        this[fileError] = errorMessage;
        this.alertService.error(errorMessage, { autoClose: true });
      }
    });
  }

  downloadFile(hdfsPath: string | null, fileName: string) {
    if (hdfsPath) {
      this.alertService.info(`Iniciando descarga de "${fileName}"...`, { autoClose: true });

      this.acreditacionMembresiaService.downloadDocument(hdfsPath).subscribe({
        next: (response: HttpResponse<Blob>) => {
          if (response.body) {
            const contentDisposition = response.headers.get('Content-Disposition');
            let actualFileName = fileName;
            if (contentDisposition) {
              const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(contentDisposition);
              if (matches != null && matches[1]) {
                actualFileName = matches[1].replace(/['"]/g, '');
              }
            }

            const url = window.URL.createObjectURL(response.body);
            const a = document.createElement('a');
            a.href = url;
            a.download = actualFileName;
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);

            this.alertService.success(`"${actualFileName}" descargado exitosamente.`, { autoClose: true });
          } else {
            this.alertService.error('La respuesta de descarga no contiene datos.', { autoClose: true });
          }
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error al descargar el archivo:', error);
          let errorMessage = 'Error al descargar el archivo. Inténtalo de nuevo.';
          if (error.status === 404) {
            errorMessage = 'El documento solicitado no fue encontrado.';
          } else if (error.error instanceof Blob) {
              const reader = new FileReader();
              reader.onload = () => {
                  try {
                      const errorBody = JSON.parse(reader.result as string);
                      errorMessage = errorBody.message || errorBody.error || errorMessage;
                  } catch (e) {
                      console.warn('No se pudo parsear el error como JSON:', reader.result);
                  }
                  this.alertService.error(errorMessage, { autoClose: true });
              };
              reader.readAsText(error.error);
              return;
          } else if (error.message) {
              errorMessage = error.message;
          }
          this.alertService.error(errorMessage, { autoClose: true });
        }
      });
    } else {
      this.alertService.error('No hay una ruta para descargar este archivo.', { autoClose: true });
    }
  }

  deleteFile(controlName: string) {
    let hdfsPathToDelete: string | null = null;
    let fileName: string = '';
    let documentType: string = '';

    if (controlName === 'constanciaMembresia' && this.fileConstanciaHdfsPath) {
      hdfsPathToDelete = this.fileConstanciaHdfsPath;
      fileName = this.selectedFileConstancia?.name || 'Constancia de Membresía';
      documentType = 'Constancia de Membresía';
    } else {
      this.alertService.error('No hay un archivo cargado para eliminar.', { autoClose: true });
      return;
    }

    this.modalService.showDialog(
      'confirm',
      'warning',
      'Confirmar Eliminación',
      `¿Estás seguro de que quieres eliminar el archivo de ${documentType} "${fileName}"? Esta acción no se puede deshacer.`,
      (confirmed: boolean) => {
        if (confirmed) {
          this.alertService.info(`Eliminando "${fileName}"...`, { autoClose: true });

          this.acreditacionMembresiaService.deleteDocument(hdfsPathToDelete!).subscribe({
            next: () => {
              this.alertService.success(`"${fileName}" eliminado exitosamente.`, { autoClose: true });
              if (controlName === 'constanciaMembresia') {
                this.fileConstanciaUploadSuccess = false;
                this.fileConstanciaHdfsPath = null;
                this.selectedFileConstancia = null;
                const fileInput = document.getElementById('constanciaMembresia') as HTMLInputElement;
                if (fileInput) fileInput.value = '';
                this.fileConstanciaError = null;
              }
            },
            error: (error: HttpErrorResponse) => {
              console.error('Error al eliminar el archivo:', error);
              let errorMessage = `Error al eliminar "${fileName}". Por favor, inténtalo de nuevo.`;
              if (error.status === 400) {
                errorMessage = 'Solicitud inválida para eliminar el documento.';
              } else if (error.status === 404) {
                errorMessage = 'El documento a eliminar no fue encontrado en el servidor.';
              } else if (error.error && error.error.message) {
                errorMessage = error.error.message;
              }
              this.alertService.error(errorMessage, { autoClose: true });
            }
          });
        } else {
          this.alertService.info('La eliminación del archivo ha sido cancelada.', { autoClose: true });
        }
      },
      'Eliminar',
      'Cancelar'
    );
  }

  // Método para manejar la acción de "Continuar" o "Guardar" en este componente
  guardarModificacionDatos(): void {
    this.formSubmitted = true; // Marca que el formulario ha sido intentado enviar

    //  Lógica de guardado de los demás datos del formulario de modificación,
    // junto con la validación de que la constancia de membresía haya sido cargada.
    if (this.selectedTipoDato === '3' && !this.fileConstanciaUploadSuccess) {
      this.alertService.error('Debes adjuntar la constancia de membresía para continuar.', { autoClose: true });
      return;
    }

    //Lógica para guardar los datos de modificación
    // Por ejemplo, enviar el objeto colegioContador (posiblemente actualizado)
    // junto con el fileConstanciaHdfsPath al backend.
    this.alertService.success('Datos y constancia de membresía guardados con éxito (simulado).', { autoClose: true });
    // Después de guardar, podrías redirigir o mostrar un acuse.
    // this.router.navigate(['/home']);
  }









  // --- MÉTODOS para la sección de DESPACHO ---

  /**
   * Carga el catálogo de tipos de sociedad desde el servicio.
   */
  cargarTiposSociedad(): void {
    this.catalogosContadorService.getTiposSociedadFormaParte().subscribe({
      next: (data: TipoSociedadFormaParteDto[]) => {
        this.tiposSociedad = data;
        console.log('Tipos de sociedad cargados:', this.tiposSociedad);
      },
      error: (error) => {
        console.error('Error al cargar los tipos de sociedad:', error);
        this.alertService.error('Error al cargar las opciones de tipo de sociedad. Inténtalo de nuevo más tarde.', { autoClose: false });
      }
    });
  }

  /**
   * Carga el catálogo de cargos de contador desde el servicio.
   */
  cargarCargosContador(): void {
    this.catalogosContadorService.getCargosContador().subscribe({
      next: (data: CargoContadorDto[]) => {
        this.cargosContador = data;
        console.log('Cargos de contador cargados:', this.cargosContador);
      },
      error: (error) => {
        console.error('Error al cargar los cargos de contador:', error);
        this.alertService.error('Error al cargar las opciones de cargos. Inténtalo de nuevo más tarde.', { autoClose: false });
      }
    });
  }

  /**
   *  carga de datos del despacho
   */
  consultarDatosDespacho(): void {
    console.log('consultarDatosDespacho');

    // Validación de seguridad por si no hay RFC en sesión
    if (!this.rfcSesion) {
      this.alertService.error('No se pudo identificar el RFC de la sesión.');
      return;
    }

    this.loadingDespacho = true;
    this.despachoContador = null; // Limpiar datos previos

    // Crear el DTO de entrada
    const request: DespachoRequestDto = {
      rfc: this.rfcSesion
    };

    this.contadorPublicoAutorizadoService.consultarDatosDespacho(request)
      .pipe(finalize(() => this.loadingDespacho = false))
      .subscribe({
        next: (response: DespachoResponseDto) => {
          // Mapear la respuesta del backend al objeto local
          this.despachoContador = {
            rfcDespacho: response.rfcDespacho,
            nombreRazonSocial: response.nombreRazonSocial,
            cveIdTipoSociedad: response.cveIdTipoSociedad,
            desTipoSociedad: response.desTipoSociedad,
            cveIdCargoContador: response.cveIdCargoContador,
            desCargoContador: response.desCargoContador,
            telefonoFijo: response.telefonoFijo,
            tieneTrabajadores: response.tieneTrabajadores,
            numeroTrabajadores: response.numeroTrabajadores
          };

          // Inicializar los campos de edición con los datos recibidos
          this.selectedTipoSociedad = this.despachoContador.cveIdTipoSociedad || '';
          this.nuevoRfcDespacho = this.despachoContador.rfcDespacho || '';
          this.selectedCargoDesempena = this.despachoContador.cveIdCargoContador || '';
          this.telefonoFijoDespacho = this.despachoContador.telefonoFijo || '';
          this.tieneTrabajadores = this.despachoContador.tieneTrabajadores || '';
          this.numeroTrabajadores = this.despachoContador.numeroTrabajadores || '';

          // Guardar valores originales para detectar cambios
          this.originalTipoSociedad = this.despachoContador.cveIdTipoSociedad;
          this.originalRfcDespacho = this.despachoContador.rfcDespacho;
          this.originalCargoDesempena = this.despachoContador.cveIdCargoContador;
          this.originalTelefonoFijo = this.despachoContador.telefonoFijo;
          this.originalNombreDespacho = this.despachoContador.nombreRazonSocial;
          this.originalTieneTrabajadores = this.despachoContador.tieneTrabajadores || null;
          this.originalNumeroTrabajadores = this.despachoContador.numeroTrabajadores || null;
        },
        error: (error: HttpErrorResponse) => {
          // Si es 404 (Not Found) o si ocurre otro error, inicializamos vacío para permitir captura
          if (error.status === 404) {
            this.alertService.info('No se encontraron datos de despacho asociados a su RFC.', { autoClose: false });
          } else {
            console.error('Error al consultar datos del despacho:', error);
            // Opcional: Mostrar error genérico, pero permitimos continuar para capturar datos nuevos
            // this.alertService.error('Ocurrió un error al consultar el despacho.');
          }

          this.inicializarDespachoVacio();
        }
      });
  }

  /**
   * Método auxiliar para inicializar el formulario cuando no hay datos
   *
   */
  private inicializarDespachoVacio(): void {
    this.despachoContador = {
      rfcDespacho: '', nombreRazonSocial: '',
      cveIdTipoSociedad: '', desTipoSociedad: '',
      cveIdCargoContador: '', desCargoContador: '',
      telefonoFijo: '',
      tieneTrabajadores: '',
      numeroTrabajadores: ''
    };

    this.originalTipoSociedad = '';
    this.originalRfcDespacho = '';
    this.originalCargoDesempena = '';
    this.originalTelefonoFijo = '';
    this.originalNombreDespacho = '';
    this.originalTieneTrabajadores = '';
    this.originalNumeroTrabajadores = '';

    // Precargar "Profesional Independiente" (ID '2') por defecto
    this.selectedTipoSociedad = '2';

    // Limpiar otros campos
    this.nuevoRfcDespacho = '';
    this.selectedCargoDesempena = '';
    this.telefonoFijoDespacho = '';
    this.tieneTrabajadores = '';
    this.numeroTrabajadores = '';

    // Actualizar el objeto visual si existe el tipo en el catálogo
    const tipoIndependiente = this.tiposSociedad.find(t => t.cveIdTipoSociedad === '2');
    if (tipoIndependiente && this.despachoContador) {
        this.despachoContador.cveIdTipoSociedad = tipoIndependiente.cveIdTipoSociedad;
        this.despachoContador.desTipoSociedad = tipoIndependiente.desTipoSociedad;
    }
  }

  /**
   * Maneja la respuesta a la pregunta "¿Desea actualizar los datos de su despacho?".
   * @param respuesta true si desea actualizar, false si no.
   */
  respuestaActualizarDespacho(respuesta: boolean): void {
    this.deseaActualizarDespacho = respuesta;
    if (respuesta) {
      this.habilitarCamposDespacho = true;
      // Los campos de edición ya están pre-cargados desde `consultarDatosDespacho()`
      this.busquedaDespachoRealizada = false;
    } else {
      this.habilitarCamposDespacho = false;
      this.router.navigate(['/home']); // Redirigir a /home si la respuesta es No
    }
  }



  /**
   * Busca los datos de un despacho por RFC invocando al servicio del SAT (vía backend).
   * Mantiene intactos el Cargo y el Teléfono que ya estaban cargados o escritos.
   */
  buscarDatosDespacho(): void {
    // 1. Validaciones previas
    if (!this.nuevoRfcDespacho) {
      this.alertService.warn('Por favor, ingresa el RFC del despacho para buscar.');
      this.rfcDespachoValido = false;
      return;
    }

    if (!this.validarRfcPersonaMoral(this.nuevoRfcDespacho)) {
      this.alertService.error('El formato del RFC del despacho no es válido. Debe tener 12 caracteres.');
      this.rfcDespachoValido = false;
      return;
    }

    this.rfcDespachoValido = true;
    this.loadingDespacho = true; // Activar spinner

    // 2. Preparar petición (Usamos la propiedad 'rfc' para coincidir con el DTO del Backend)
    const request: RfcColegioRequestDto = { rfc: this.nuevoRfcDespacho };

    // 3. Invocar servicio SAT
    this.catalogosContadorService.getDatoRfcColegio(request)
      .pipe(finalize(() => this.loadingDespacho = false))
      .subscribe({
        next: (data: RfcColegioResponseDto) => {
          // Aseguramos que el objeto exista
          if (!this.despachoContador) {
            this.inicializarDespachoVacio();
          }

          // 4. Actualizamos SOLO la información del SAT (RFC y Nombre)
          if (this.despachoContador) {
             this.despachoContador.rfcDespacho = data.rfc;
             this.despachoContador.nombreRazonSocial = data.nombreRazonSocial;
          }

          // 5. Bloqueamos el input y el botón buscar
          this.busquedaDespachoRealizada = true;
          this.alertService.success('Datos del despacho encontrados.');

          // NOTA: No modificamos 'this.selectedCargoDesempena' ni 'this.telefonoFijoDespacho',
          // por lo que conservan los datos previos (cargados o editados por el usuario).
        },
        error: (error: HttpErrorResponse) => {
          // En error NO bloqueamos para permitir corregir
          this.busquedaDespachoRealizada = false;
          console.error('Error al buscar el RFC del despacho:', error);

          if (error.status === 404) {
             // Limpiamos solo la razón social si no se encuentra
            if (this.despachoContador){
               this.despachoContador.nombreRazonSocial = '';
              }
            this.alertService.error('No se encontró información en el SAT para el RFC proporcionado.');
          } else {
            this.alertService.error('Error al consultar el servicio del SAT.');
          }
        }
      });
  }



limpiarDatosDespacho(): void {
    this.nuevoRfcDespacho = '';
    this.rfcDespachoValido = true;

    // Limpiamos los datos visuales del despacho, pero NO el cargo ni teléfono (opcional)
    // O si prefieres limpiar todo el formulario, descomenta las líneas de cargo/telefono
    if (this.despachoContador) {
      this.despachoContador.nombreRazonSocial = '';
      this.despachoContador.rfcDespacho = '';
    }

    this.selectedCargoDesempena = '';
    this.telefonoFijoDespacho = '';

    // Reactivamos el input y el botón buscar
    this.busquedaDespachoRealizada = false;

    this.alertService.info('Campo RFC limpiado. Puede realizar una nueva búsqueda.');
  }


  cancelarEdicionDespacho(): void {
    // Si cancela, volvemos al estado inicial (preguntar si desea actualizar)
    this.deseaActualizarDespacho = null;
    this.habilitarCamposDespacho = false;
    this.alertService.info('Edición de datos del despacho cancelada.');
    // Podrías recargar los datos iniciales del despacho si quieres revertir cualquier cambio
    // this.consultarDatosDespacho();
  }




  cargarDatosContador(): void {

    if (!this.folioSolicitud) {
      // Esto solo debería pasar si hubo un error en generarFolioSolicitud
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

        this.datosContadorData = { ...data, folioSolicitud: this.folioSolicitud!  }; // Aquí usamos el operador !
        this.loading = false;
        this.loaderService.hide();
        console.log('Datos del contador cargados:', this.datosContadorData);
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






  // --- MÉTODOS para la sección de CONTACTO ---

  /**
   * Maneja la respuesta a la pregunta "¿Los datos son correctos?" para la sección de contacto.
   * @param respuesta true si no son correctos (habilitar edición), false si son correctos (solo lectura).
   */
  respuestaActualizarContacto(respuesta: boolean): void {
    this.deseaActualizarContacto = respuesta;
    this.formSubmitted = false; // Resetear el estado de validación al cambiar de modo



    if (respuesta === false) {
      this.modalService.showDialog(
        'alert', // Tipo: Al no ser 'confirm', el modal.component ocultará el botón secundario
        'info', // Estilo: Define el icono (puedes cambiar a 'warning' si prefieres el triángulo amarillo)
        'Mensaje de sistema', // Título del modal
        'Favor de llevar a cabo la actualización del domicilio fiscal ante las oficinas del Servicio de Administración Tributaria SAT.', // Texto del cuerpo
        () => {
          // Acción al pulsar "Aceptar"
          this.deseaActualizarContacto = false; // Aseguramos que se cierre la sección de edición si estaba abierta
          this.limpiarCamposEdicionContacto();
          this.router.navigate([NAV.home]);
        },
        'Aceptar' // Texto del botón principal
      );
      return; // Detenemos la ejecución aquí para que no haga nada más
    }

    if (respuesta) { // Si el usuario dice "Si" (abre la actualización)
      this.precargarDatosContacto();

    }
  }

  /**
   * Método para precargar los campos del formulario con los datos existentes
   */
  precargarDatosContacto() {
    if (this.datosContadorData && this.datosContadorData.datosContactoDto) {
      this.nuevoCorreoElectronico2 = this.datosContadorData.datosContactoDto.correoElectronico2 || '';
      this.confirmarCorreoElectronico2 = this.datosContadorData.datosContactoDto.correoElectronico2 || '';
      this.nuevoCorreoElectronico3 = this.datosContadorData.datosContactoDto.correoElectronico3 || '';
      this.confirmarCorreoElectronico3 = this.datosContadorData.datosContactoDto.correoElectronico3 || '';
      this.nuevoTelefono2 = this.datosContadorData.datosContactoDto.telefono2 || '';
      this.nuevacedulaprofesional = this.datosContadorData.datosContactoDto.cedulaprofesional || '';
    }
  }

  /**
   * Método para limpiar los campos del formulario de edición (opcional)
   */
  limpiarCamposEdicionContacto() {
    this.nuevoCorreoElectronico2 = '';
    this.confirmarCorreoElectronico2 = '';
    this.nuevoCorreoElectronico3 = '';
    this.confirmarCorreoElectronico3 = '';
    this.nuevoTelefono2 = '';
    this.nuevacedulaprofesional = '';
  }


  /**
   * Cancela la edición de los datos de contacto y revierte los cambios.
   */
  cancelarEdicionContacto(): void {
    this.deseaActualizarContacto = false;
    this.formSubmitted = false; // Resetear el estado de envío del formulario
    //this.alertService.info('Edición de datos de contacto cancelada.');

    // Opcional: Recargar los datos originales si se cancela la edición
    if (this.datosContadorData?.datosContactoDto) {
      this.nuevoCorreoElectronico2 = this.datosContadorData.datosContactoDto.correoElectronico2 || '';
      this.confirmarCorreoElectronico2 = this.datosContadorData.datosContactoDto.correoElectronico2 || ''; // Revertir confirmación también
      this.nuevoCorreoElectronico3 = this.datosContadorData.datosContactoDto.correoElectronico3 || '';
      this.confirmarCorreoElectronico3 = this.datosContadorData.datosContactoDto.correoElectronico3 || ''; // Revertir confirmación también
      this.nuevoTelefono2 = this.datosContadorData.datosContactoDto.telefono2 || '';
      this.nuevacedulaprofesional = this.datosContadorData.datosContactoDto.cedulaprofesional || '';

    }
  }


  // Métodos para el flujo general del formulario
  siguientePaso(): void {
    // Lógica para ir al siguiente paso del formulario
    this.alertService.success('Pasando al siguiente paso (simulado).');
    // this.router.navigate(['/ruta-siguiente-paso']);
  }

  cancelarGlobal(): void {
    // Lógica para cancelar el proceso completo
    this.alertService.info('Proceso de modificación de datos cancelado.', { autoClose: true });
    this.router.navigate(['/home']);
  }




  /**
   * Valida si una cadena tiene formato de correo electrónico válido
   */
  validarFormatoCorreo(correo: string): boolean {
    if (!correo) return false; // Si está vacío se maneja como "requerido"
    return this.emailPattern.test(correo);
  }

  /**
   * Valida si el teléfono tiene exactamente 10 dígitos numéricos
   */
  validarFormatoTelefono(telefono: string): boolean {
    const phonePattern = /^[0-9]{10,12}$/;
    return phonePattern.test(telefono);
  }

  /**
   * Evita que el usuario escriba letras o caracteres especiales en el input de teléfono
   */
  permitirSoloNumeros(event: KeyboardEvent): void {
    const charCode = event.which ? event.which : event.keyCode;
    // Solo permite números (códigos ASCII del 48 al 57)
    if (charCode < 48 || charCode > 57) {
      event.preventDefault();
    }
  }







/**
   * 1. GUARDAR DATOS COLEGIO
   */
  guardarDatosColegio(): void {
    this.formSubmitted = true;

    // 1. Validaciones previas
    if (!this.habilitarEdicionRfcColegio) return;

    if (!this.nuevoRfcColegio || !this.rfcColegioValido) {
      this.alertService.error('Por favor, verifique que el RFC del colegio sea válido.');
      return;
    }

    if (!this.fileConstanciaUploadSuccess || !this.fileConstanciaHdfsPath) {
      this.alertService.error('Es obligatorio adjuntar la constancia de membresía correctamente.');
      return;
    }

    // 2. Construcción de la cadena de modificaciones
    let cambios: string[] = [];

    // Función auxiliar para limpiar espacios y comparar sin importar mayúsculas/minúsculas
    const normalizar = (val: string | null | undefined) => (val || '').trim().toUpperCase();

    // --- LÓGICA DE COMPARACIÓN (Solo agrega si son diferentes al original) ---

    // Comparamos el RFC escrito (nuevo) vs el original guardado al inicio
    if (normalizar(this.nuevoRfcColegio) !== normalizar(this.rfcColegioOriginal)) {
        cambios.push(`RFC del colegio: ${this.nuevoRfcColegio}`);
    }

    // Comparamos la Razón Social del objeto actual (que pudo cambiar con "Buscar") vs la original
    if (this.colegioContador && normalizar(this.colegioContador.razonSocial) !== normalizar(this.razonSocialOriginal)) {
        cambios.push(`Razón social del colegio: ${this.colegioContador.razonSocial}`);
    }

    // El archivo siempre se cuenta como cambio si se subió uno nuevo (ya validado arriba)
   if (this.selectedFileConstancia?.name) {
        //cambios.push(`Constancia adjunta: ${this.selectedFileConstancia.name}`);
        cambios.push(` `);
    }

    // 3. Validación de "Sin modificaciones"
    if (cambios.length === 0) {
        this.alertService.info("Sin modificaciones realizadas.");
        return;
    }

    // 4. Preparación de datos para el Acuse
    const nuevosDatosContactoString = cambios.join("\n");
    const datosBase = this.obtenerDatosBaseParaAcuse();

    this.docMemSi = "X";

    const datosParaAcuse = {
      ...datosBase,
      docMemSi: this.docMemSi,

      datosContacto: nuevosDatosContactoString,
      domicilioFiscal: "",

      tipoSolicitud: 'COLEGIO',

      // Enviamos el nuevo RFC y la Razón Social actual (sea nueva o vieja, es la vigente para el trámite)
      rfcColegioNuevo: this.nuevoRfcColegio,
      razonSocialColegio: this.colegioContador?.razonSocial || '',

      desPathHdfsConstancia: this.fileConstanciaHdfsPath,
      nomArchivoConstancia: this.selectedFileConstancia?.name,

      tipoTramite: 'ACUSE_SOLICITUD_CAMBIO',
      state: {
        nuevoRfcColegio: this.nuevoRfcColegio,
        fileConstanciaHdfsPath: this.fileConstanciaHdfsPath,
        nomArchivoConstancia: this.selectedFileConstancia?.name
      }
    };

    // 5. Guardar y Navegar
    this.modificacionDatosDataService.setDatosFormularioPrevio(datosParaAcuse);
    this.router.navigate([NAV.contadormodificaciondatosacuse]);
  }


  /**
   * 2. GUARDAR DATOS DESPACHO
   */
  guardarDatosDespacho(): void {
    this.formSubmitted = true;



    const isIndependiente = this.selectedTipoSociedad === '2'; // ID 2 = Independiente

    // 1. Validaciones (Separadas por tipo)
    if (!this.selectedTipoSociedad) {
       this.alertService.error('Debe seleccionar si forma parte de un despacho o es independiente.');
       return;
    }

    if (isIndependiente) {
        // Validaciones para Independiente
        if (!this.tieneTrabajadores) {
             // Validar el primer select
             return;
        }
        if (this.tieneTrabajadores === 'Si' && (!this.numeroTrabajadores || parseInt(this.numeroTrabajadores) <= 0)) {
             // Validar número si dijo que sí
             return;
        }
    } else {
        // Validaciones para Despacho
        if (!this.nuevoRfcDespacho || !this.validarRfcPersonaMoral(this.nuevoRfcDespacho) || !this.selectedCargoDesempena || !this.telefonoFijoDespacho) {
           this.alertService.error('Verifique los campos obligatorios del despacho.');
           return;
        }
    }

    // 2. Detección de Cambios
    let cambios: string[] = [];
    const normalizar = (val: string | null | undefined) => (val || '').trim().toUpperCase();

    // -- A. Comparar Tipo de Sociedad (Común)
    if (normalizar(this.selectedTipoSociedad) !== normalizar(this.originalTipoSociedad)) {
        const nombreSociedad = this.tiposSociedad.find(t => t.cveIdTipoSociedad === this.selectedTipoSociedad)?.desTipoSociedad || '';
        cambios.push(`Contador ${nombreSociedad}`);
    }

    // -- B. Ramificación de comparación según lo que seleccionó el usuario
    if (isIndependiente) {


        // Comparar "Número de Trabajadores" (Solo si tiene trabajadores)
        if (this.tieneTrabajadores === 'Si') {
             if (normalizar(this.numeroTrabajadores) !== normalizar(this.originalNumeroTrabajadores)) {
                cambios.push(`Cuenta con ${this.numeroTrabajadores} trabajadores`);
            }
        } else {
             // Si cambió de "Si" a "No", y antes tenía número, implícitamente ya cambió "Cuenta con trabajadores".
             // No necesitamos enviar "Número de trabajadores: 0" a menos que sea requisito.
        }

    } else {
        // --- LÓGICA DESPACHO   ---

        // RFC
        if (normalizar(this.nuevoRfcDespacho) !== normalizar(this.originalRfcDespacho)) {
            cambios.push(`RFC Despacho: ${this.nuevoRfcDespacho}`);
            if (this.despachoContador?.nombreRazonSocial) {
                 cambios.push(`Despacho: ${this.despachoContador.nombreRazonSocial}`);
            }
        }
        else if (normalizar(this.despachoContador?.nombreRazonSocial) !== normalizar(this.originalNombreDespacho)) {
             cambios.push(`Despacho: ${this.despachoContador?.nombreRazonSocial}`);
        }

        // Cargo
        if (normalizar(this.selectedCargoDesempena) !== normalizar(this.originalCargoDesempena)) {
            const nombreCargo = this.cargosContador.find(c => c.cveIdCargoContador === this.selectedCargoDesempena)?.desCargoContador || '';
            cambios.push(`Cargo: ${nombreCargo}`);
        }

        // Teléfono
        if (normalizar(this.telefonoFijoDespacho) !== normalizar(this.originalTelefonoFijo)) {
            cambios.push(`Teléfono despacho: ${this.telefonoFijoDespacho}`);
        }
    }


    // 3. Verificar si hubo modificaciones
    if (cambios.length === 0) {
        this.alertService.info("Sin modificaciones realizadas.");
        return;
    }

    // 4. Preparar datos para el Acuse
    const datosDespachoString = cambios.join("\n");
    const datosBase = this.obtenerDatosBaseParaAcuse();

    this.docMemNo = "X";

    const datosParaAcuse = {
      ...datosBase,
      docMemNo: this.docMemNo,

      // Enviamos solo la lista de cambios formateada
      datosContacto: datosDespachoString,
      domicilioFiscal: "",

      // Datos sueltos por si el backend los requiere individualmente
      razonSocialDespacho: isIndependiente ? '' : this.despachoContador?.nombreRazonSocial,
      tieneTrabajadores: this.tieneTrabajadores,
      numTrabajadores: this.numeroTrabajadores,


      tipoSolicitud: 'DESPACHO',
      tipoTramite: 'ACUSE_SOLICITUD_CAMBIO',

      state: {
          selectedTipoSociedad: this.selectedTipoSociedad,
          tieneTrabajadores: this.tieneTrabajadores,
          numeroTrabajadores: this.numeroTrabajadores,
          nuevoRfcDespacho: this.nuevoRfcDespacho,
          selectedCargoDesempena: this.selectedCargoDesempena,
          telefonoFijoDespacho: this.telefonoFijoDespacho,
          // Guardamos el nombre visual para que no aparezca vacío al regresar
          razonSocialDespachoVisual: this.despachoContador?.nombreRazonSocial
      }
    };

    this.modificacionDatosDataService.setDatosFormularioPrevio(datosParaAcuse);
    this.router.navigate([NAV.contadormodificaciondatosacuse]);
  }

  /**
   * 3. GUARDAR DATOS CONTACTO
   */
  guardarDatosContacto(): void {
    this.formSubmitted = true;

    // 1. Validaciones de campos obligatorios
    if (!this.nuevoCorreoElectronico2 || !this.nuevoTelefono2) {
       return;
    }

    // 2. Validaciones de formato (si no se hicieron en el HTML)
    if (!this.validarFormatoCorreo(this.nuevoCorreoElectronico2) ||
        (this.nuevoCorreoElectronico3 && !this.validarFormatoCorreo(this.nuevoCorreoElectronico3)) ||
        !this.validarFormatoTelefono(this.nuevoTelefono2)) {
        return;
    }


    if (this.nuevoCorreoElectronico2 !== this.confirmarCorreoElectronico2) {
        return; // Al hacer return, se queda en la página y se muestra la alerta del HTML
    }

    if (this.nuevoCorreoElectronico3 && (this.nuevoCorreoElectronico3 !== this.confirmarCorreoElectronico3)) {
        return;
    }

    // Obtenemos los datos originales para comparar
    const original = this.datosContadorData?.datosContactoDto;
    let cambios: string[] = [];

    // Función auxiliar para normalizar cadenas (evita falsos positivos por espacios o nulos)
    const normalizar = (valor: string | null | undefined) => (valor || '').trim();

    // --- LÓGICA DE COMPARACIÓN ---

    // 1. Correo 2
    if (normalizar(this.nuevoCorreoElectronico2) !== normalizar(original?.correoElectronico2)) {
        cambios.push(`Correo electrónico 2: ${this.nuevoCorreoElectronico2}`);
    }

    // 2. Correo 3
    if (normalizar(this.nuevoCorreoElectronico3) !== normalizar(original?.correoElectronico3)) {
         // Si se dejó vacío a propósito o se cambió
         const valorMostrar = this.nuevoCorreoElectronico3 ? this.nuevoCorreoElectronico3 : '(Eliminado)';
         cambios.push(`Correo electrónico 3: ${valorMostrar}`);
    }

    // 3. Teléfono
    if (normalizar(this.nuevoTelefono2) !== normalizar(original?.telefono2)) {
        cambios.push(`Teléfono de contacto: ${this.nuevoTelefono2}`);
    }

    // 4. Cédula
    if (normalizar(this.nuevacedulaprofesional) !== normalizar(original?.cedulaprofesional)) {
        cambios.push(`Cédula profesional: ${this.nuevacedulaprofesional}`);
    }


    // --- VALIDACIÓN DE CAMBIOS ---

    // Si NO hay cambios, mostramos mensaje y detenemos el proceso
    if (cambios.length === 0) {
        this.alertService.info("Sin modificaciones realizadas.");
        return; // <--- ESTO DETIENE LA EJECUCIÓN Y MANTIENE AL USUARIO EN LA PANTALLA
    }


    // --- CONTINUAR SOLO SI HUBO CAMBIOS ---

    const nuevosDatosContactoString =   cambios.join("\n");
    const datosBase = this.obtenerDatosBaseParaAcuse();

    this.docMemNo = "X";

    const datosParaAcuse = {
      ...datosBase,
      docMemNo: this.docMemNo,
      domicilioFiscal:"",
      datosContacto: nuevosDatosContactoString, // Enviamos solo lo modificado
      tipoSolicitud: 'CONTACTO',
      tipoTramite: 'ACUSE_SOLICITUD_CAMBIO',
      state: {
          nuevoCorreoElectronico2: this.nuevoCorreoElectronico2,
          nuevoCorreoElectronico3: this.nuevoCorreoElectronico3,
          nuevoTelefono2: this.nuevoTelefono2,
          nuevacedulaprofesional: this.nuevacedulaprofesional
      }
    };

    this.modificacionDatosDataService.setDatosFormularioPrevio(datosParaAcuse);
    this.router.navigate([NAV.contadormodificaciondatosacuse]);
  }


private obtenerDatosBaseParaAcuse() {

    // 1. Obtenemos la fecha y hora actual del sistema
    const now = new Date();

    // 2. Definimos los meses en español manualmente para garantizar el idioma
    const meses = [
      'enero', 'febrero', 'marzo', 'abril', 'mayo', 'junio',
      'julio', 'agosto', 'septiembre', 'octubre', 'noviembre', 'diciembre'
    ];

    // 3. Extraemos las partes de la fecha
    const dia = now.getDate();
    const mes = meses[now.getMonth()];
    const anio = now.getFullYear();

    // 4. Formateamos la hora usando el DatePipe que ya tienes inyectado (formato 24h)
    // Esto devolverá algo como "12:37:47"
    const hora = this.datePipe.transform(now, 'HH:mm:ss');

    // 5. Concatenamos todo en la variable final
    // Resultado: "10 de diciembre de 2025, 12:37:47"
    const fechaActual = `${dia} de ${mes} de ${anio}, ${hora}`;



  // 1. Formatear Domicilio Fiscal a un solo String
  const d = this.datosContadorData?.domicilioFiscalDto;
  let domicilioString = '';
  if (d) {
    // Construimos la cadena como la quiere el reporte
    domicilioString = `${d.calle || ''} ${d.numeroExterior || ''} ${d.numeroInterior ? 'Int ' + d.numeroInterior : ''}, ` +
                      `${d.colonia || ''}, ${d.municipioODelegacion || ''}, ` +
                      `${d.entidadFederativa || ''}, C.P. ${d.codigoPostal || ''}`;
  }

  // 2. Formatear Datos de Contacto Actuales a un solo String (Valor por defecto)
  const c = this.datosContadorData?.datosContactoDto;
  let contactoString = '';
  if (c) {
     contactoString = `Correo: ${c.correoElectronico1 || ''}. Tel: ${c.telefono1 || ''}`;
  }

  return {
    folioSolicitud: this.folioSolicitud,
    //se solicito que no se muestre el folio en el acuse preview
    //folio: this.folioSolicitud,
    folio: "",
    fecha: fechaActual,

    // --- CORRECCIÓN DE NOMBRES (Deben ser idénticos a los <field> del XML) ---
    rfc: this.rfcSesion,            // XML espera 'rfc' (minúscula)
    curp: this.curpSesion,          // XML espera 'curp' (minúscula)
    nombre: this.nombreCompletoSync,// XML espera 'nombre'
    registroImss: this.numeroRegistroImssSesion, // XML espera 'registroImss'

    // --- CORRECCIÓN DE TIPOS (Strings formateados) ---
    domicilioFiscal: domicilioString, // Enviamos el string concatenado
    datosContacto: contactoString     // Enviamos el string concatenado por defecto
  };
}

}
