import { Component, OnInit } from '@angular/core';
import { CatalogosContadorService } from '../services/catalogos-contador.service';
import { AlertService } from '../../../shared/services/alert.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { finalize } from 'rxjs';
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

@Component({
  selector: 'app-modificaciondatos',
  standalone: true,
  imports: [ CommonModule,
    FormsModule ],
  templateUrl: './modificaciondatos.component.html',
  styleUrl: './modificaciondatos.component.css'
})
export class ModificaciondatosComponent extends BaseComponent implements OnInit {

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



  constructor(
    private catalogosContadorService: CatalogosContadorService,
    private alertService: AlertService,
    private contadorPublicoAutorizadoService: ContadorPublicoAutorizadoService,
    private router: Router, 
    private modalService: ModalService, 
    private acreditacionMembresiaService: AcreditacionMembresiaService,
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


    if (this.selectedTipoDato === '3') { // Datos del Colegio
      this.mostrarSeccionColegio = true;
      this.consultarDatosColegio();
    } else if (this.selectedTipoDato === '2') { // Datos del Despacho
      this.mostrarSeccionDespacho = true;
      this.consultarDatosDespacho(); // <-- Al seleccionar, cargamos los datos y los mostramos.
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
          console.log('Datos del colegio obtenidos:', this.colegioContador);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error al obtener los datos del colegio:', error);
          this.alertService.error('Error al consultar los datos del colegio. Inténtalo de nuevo más tarde.');
          this.colegioContador = null; // Limpiar datos previos en caso de error
        }
      });
  }


  
  
  respuestaActualizarColegio(respuesta: boolean): void {
    if (respuesta) {
      this.habilitarEdicionRfcColegio = true;
      // Precarga el RFC actual del colegio en el campo de edición
      this.nuevoRfcColegio = this.colegioContador?.rfcColegio || '';
    } else {
      this.habilitarEdicionRfcColegio = false;
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
      this.alertService.error('El formato del RFC ingresado no es valido. Por favor, verifícalo.');
      this.rfcColegioValido = false; // Marcar el RFC como inválido
      return;
    }

    this.rfcColegioValido = true; // Resetear estado de validación si pasa el formato
    console.log('Buscando colegio con nuevo RFC:', this.nuevoRfcColegio);

    this.loadingColegio = true;
    const request: RfcColegioRequestDto = { rfcColegio: this.nuevoRfcColegio };

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
          console.log('Datos del nuevo colegio obtenidos:', this.colegioContador);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error al obtener los datos del nuevo colegio:', error);
          if (error.status === 404) {
            this.alertService.error('No se encontraron datos de colegio para el RFC proporcionado. Por favor, verifica el RFC e intenta de nuevo.');
          } else {
            this.alertService.error('Error al consultar los datos del nuevo colegio. Inténtalo de nuevo más tarde.');
          }
          this.colegioContador = null;
        }
      });
  }

  limpiarNuevoRfcColegio(): void {
    this.nuevoRfcColegio = '';
    this.rfcColegioValido = true;  
    if (this.colegioContador) {
      this.colegioContador.razonSocial = '';
      this.colegioContador.rfcColegio = '';
    }
    this.alertService.info('Campo RFC y razón social limpiados.');
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
        this.alertService.error('Solo se permiten archivos en formato PDF.', { autoClose: true });
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

    // Aquí iría tu lógica de guardado de los demás datos del formulario de modificación,
    // junto con la validación de que la constancia de membresía haya sido cargada.
    if (this.selectedTipoDato === '3' && !this.fileConstanciaUploadSuccess) {
      this.alertService.error('Debes adjuntar la constancia de membresía para continuar.', { autoClose: true });
      return;
    }

    // Aquí iría el resto de tu lógica para guardar los datos de modificación
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
   * Simula la carga de datos del despacho (puedes reemplazar con un servicio real).
   * Por ahora, genera datos de MOC si no existen.
   */
  consultarDatosDespacho(): void {
    console.log('consultarDatosDespacho');
    this.loadingDespacho = true;
    this.despachoContador = null; // Limpiar datos previos

    // Simulación de una llamada a un servicio
    setTimeout(() => {
      if (this.rfcSesion === 'MOGF5304159BA') { // Ejemplo con un RFC de sesión fijo
        this.despachoContador = {
          rfcDespacho: 'MOSB650818PB4',
          nombreRazonSocial: 'BEATRIZ MORENO SALINAS',
          cveIdTipoSociedad: '1', // Despacho
          desTipoSociedad: 'Despacho',
          cveIdCargoContador: '2', // Director
          desCargoContador: 'Director',
          telefonoFijo: '5857564355'
        };
        this.alertService.success('Datos del despacho cargados exitosamente.', { autoClose: true });

        // IMPORTANTE: Inicializamos los campos de edición con los datos actuales
        // para que, si el usuario decide "Sí" actualizar, los campos ya estén pre-cargados.
        this.selectedTipoSociedad = this.despachoContador.cveIdTipoSociedad;
        this.nuevoRfcDespacho = this.despachoContador.rfcDespacho;
        this.selectedCargoDesempena = this.despachoContador.cveIdCargoContador;
        this.telefonoFijoDespacho = this.despachoContador.telefonoFijo;

      } else {
        this.despachoContador = null; // No se encontraron datos
        this.alertService.info('No se encontraron datos de despacho asociados a su RFC.', { autoClose: false });
        // Si no hay datos, inicializamos el despachoContador vacío para que se muestre la pregunta y la opción de captura
        this.despachoContador = {
          rfcDespacho: '', nombreRazonSocial: '',
          cveIdTipoSociedad: '', desTipoSociedad: '',
          cveIdCargoContador: '', desCargoContador: '',
          telefonoFijo: ''
        };
        // Y precargamos con "Profesional Independiente" como valor por defecto si no hay despacho
        this.selectedTipoSociedad = '2'; // ID para Profesional Independiente
        const tipoIndependiente = this.tiposSociedad.find(t => t.cveIdTipoSociedad === '2');
        if (tipoIndependiente && this.despachoContador) {
            this.despachoContador.cveIdTipoSociedad = tipoIndependiente.cveIdTipoSociedad;
            this.despachoContador.desTipoSociedad = tipoIndependiente.desTipoSociedad;
        }

      }
      this.loadingDespacho = false;
    }, 1000);
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
      this.alertService.info('Campos de despacho habilitados para edición.', { autoClose: true });
    } else {
      this.habilitarCamposDespacho = false;
      this.router.navigate(['/home']); // Redirigir a /home si la respuesta es No
    }
  }

  /**
   * Busca los datos de un despacho por RFC.
   */
  buscarDatosDespacho(): void {
    if (!this.nuevoRfcDespacho) {
      this.alertService.warn('Por favor, ingresa el RFC del despacho para buscar.');
      this.rfcDespachoValido = false;
      return;
    }

    if (!this.validarRfc(this.nuevoRfcDespacho)) {
      this.alertService.error('El formato del RFC del despacho no es válido. Por favor, verifícalo.');
      this.rfcDespachoValido = false;
      return;
    }

    this.rfcDespachoValido = true;
    this.loadingDespacho = true; // Activar spinner

    const request: RfcColegioRequestDto = { rfcColegio: this.nuevoRfcDespacho }; // Se usa el mismo DTO para RFC de persona moral

    this.catalogosContadorService.getDatoRfcColegio(request) // Usamos el mismo servicio SAT para RFCs de persona moral
      .pipe(finalize(() => this.loadingDespacho = false))
      .subscribe({
        next: (data: RfcColegioResponseDto) => {
          if (!this.despachoContador) {
            this.despachoContador = {
              rfcDespacho: '', nombreRazonSocial: '',
              cveIdTipoSociedad: '', desTipoSociedad: '',
              cveIdCargoContador: '', desCargoContador: '',
              telefonoFijo: ''
            };
          }
          this.despachoContador.rfcDespacho = data.rfc;
          this.despachoContador.nombreRazonSocial = data.nombreRazonSocial;
          this.alertService.success('Datos del despacho encontrados.');
          console.log('Datos del despacho obtenidos:', this.despachoContador);
        },
        error: (error: HttpErrorResponse) => {
          console.error('Error al buscar el RFC del despacho:', error);
          if (error.status === 404) {
            this.alertService.error('No se encontró información para el RFC del despacho proporcionado. Por favor, verifica el RFC.');
          } else {
            this.alertService.error('Error al consultar el RFC del despacho. Inténtalo de nuevo más tarde.');
          }
          if (this.despachoContador) {
            this.despachoContador.nombreRazonSocial = ''; // Limpiar si no se encuentra
          }
        }
      });
  }

  /**
   * Limpia los campos de RFC y razón social del despacho.
   */
  limpiarDatosDespacho(): void {
    this.nuevoRfcDespacho = '';
    this.rfcDespachoValido = true;
    if (this.despachoContador) {
      this.despachoContador.nombreRazonSocial = '';
      this.despachoContador.rfcDespacho = '';
    }
    this.alertService.info('Campos de RFC y Razón Social del despacho limpiados.');
  }

  /**
   * Guarda los cambios en los datos del despacho (simulado).
   */
  guardarDatosDespacho(): void {
    this.formSubmitted = true; // Para mostrar validaciones

    // Validaciones básicas antes de guardar
    if (!this.selectedTipoSociedad) {
      this.alertService.error('Por favor, selecciona el tipo de sociedad.', { autoClose: true });
      return;
    }
    if (!this.nuevoRfcDespacho || !this.validarRfc(this.nuevoRfcDespacho)) {
      this.alertService.error('Por favor, ingresa un RFC válido para el despacho.', { autoClose: true });
      this.rfcDespachoValido = false;
      return;
    }
    if (!this.despachoContador?.nombreRazonSocial) {
      this.alertService.error('Por favor, busca y carga la razón social del despacho.', { autoClose: true });
      return;
    }
    if (!this.selectedCargoDesempena) {
      this.alertService.error('Por favor, selecciona el cargo que desempeña.', { autoClose: true });
      return;
    }
    if (!this.telefonoFijoDespacho || this.telefonoFijoDespacho.length < 8) { // Ejemplo de validación de teléfono
      this.alertService.error('Por favor, ingresa un número de teléfono fijo válido (mínimo 8 dígitos).', { autoClose: true });
      return;
    }

    // Actualizar el DTO del despacho con los valores seleccionados/ingresados
    if (this.despachoContador) {
      this.despachoContador.cveIdTipoSociedad = this.selectedTipoSociedad;
      this.despachoContador.desTipoSociedad = this.tiposSociedad.find(t => t.cveIdTipoSociedad === this.selectedTipoSociedad)?.desTipoSociedad || '';
      this.despachoContador.rfcDespacho = this.nuevoRfcDespacho;
      // La razón social ya debería estar cargada por `buscarDatosDespacho`
      this.despachoContador.cveIdCargoContador = this.selectedCargoDesempena;
      this.despachoContador.desCargoContador = this.cargosContador.find(c => c.cveIdCargoContador === this.selectedCargoDesempena)?.desCargoContador || '';
      this.despachoContador.telefonoFijo = this.telefonoFijoDespacho;
    }

    // Aquí iría la llamada al servicio para guardar los datos del despacho
    console.log('Guardando datos del despacho:', this.despachoContador);
    this.alertService.success('Los datos del despacho han sido guardados exitosamente (simulado).');

    // Deshabilita los campos después de guardar si no hay más edición
    this.habilitarCamposDespacho = false;
    this.deseaActualizarDespacho = false; // O mantener true si se permite edición continua
    // Opcionalmente, redirigir: this.router.navigate(['/home']);
  }

  cancelarEdicionDespacho(): void {
    // Si cancela, volvemos al estado inicial (preguntar si desea actualizar)
    this.deseaActualizarDespacho = null;
    this.habilitarCamposDespacho = false;
    this.alertService.info('Edición de datos del despacho cancelada.');
    // Podrías recargar los datos iniciales del despacho si quieres revertir cualquier cambio
    // this.consultarDatosDespacho();
  }








}
