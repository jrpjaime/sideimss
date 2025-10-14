import { Component, Renderer2 } from '@angular/core';
import { BaseComponent } from '../../../shared/base/base.component';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CatalogosService } from '../../../shared/catalogos/services/catalogos.service';
import { SharedService } from '../../../shared/services/shared.service';
import { fechaInicioMenorOigualFechaFin } from '../../../global/validators';
import { AcreditacionMembresiaService } from '../services/acreditacion-membresia.service';
import { CommonModule, DatePipe } from '@angular/common';
import { AlertService } from '../../../shared/services/alert.service';
import { DocumentoIndividualResponseDto } from '../model/DocumentoIndividualResponseDto ';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ModalService } from '../../../shared/services/modal.service';
import { catchError, forkJoin, of } from 'rxjs';
import { AcreditacionMembresiaDataService } from '../services/acreditacion-membresia-data.service';
import { NAV } from '../../../global/navigation';


@Component({
  selector: 'app-acreditacionymembresia',
  standalone: true,
  imports: [ReactiveFormsModule, ReactiveFormsModule, CommonModule ],
  templateUrl: './acreditacionymembresia.component.html',
  styleUrl: './acreditacionymembresia.component.css'
})
export class AcreditacionymembresiaComponent extends BaseComponent {

  formAcreditacionMembresia: FormGroup;
  selectedFileUno: File | null = null;
  selectedFileDos: File | null = null;

  fileUnoUploadSuccess: boolean = false;
  fileDosUploadSuccess: boolean = false;
  fileUnoHdfsPath: string | null = null; // Guardará el path HDFS en Base64
  fileDosHdfsPath: string | null = null; // Guardará el path HDFS en Base64
  fileUnoError: string | null = null;
  fileDosError: string | null = null;

  loadingFileUno: boolean = false; //  Para el spinner del botón Adjuntar
  loadingFileDos: boolean = false; //  Para el spinner del botón Adjuntar

  responseDto: DocumentoIndividualResponseDto | null = null; // Para la respuesta final del submit, si aplica


  constructor (
    private fb: FormBuilder,
    private router : Router,
    private renderer: Renderer2,
    private catalogosService: CatalogosService,
    private modalService: ModalService,
    private acreditacionMembresiaService: AcreditacionMembresiaService,
    private alertService: AlertService,
    private acreditacionMembresiaDataService: AcreditacionMembresiaDataService,
    private datePipe: DatePipe,
    sharedService: SharedService
  ) {
    super(sharedService);
    this.recargaParametros();
    this.formAcreditacionMembresia = this.fb.group({
      fechaExpedicionAcreditacion: ['', [Validators.required]],
      fechaExpedicionMembresia: ['', [Validators.required]],
      // Los campos de archivo ya no son 'required' en la inicialización si se suben individualmente
      // En su lugar, se validará su estado de carga exitosa
      archivoUno: [''], // Mantenerlo para almacenar el nombre o un placeholder, pero sin Validators.required inicial
      archivoDos: ['']
    }, { validators: fechaInicioMenorOigualFechaFin() });
  }




    onFileSelected(event: any, controlName: string) {
    this.alertService.clear(); // Limpiar alerts previos

    const file: File = event.target.files[0];

    // Limpiar el estado de éxito/error del archivo previo al seleccionar uno nuevo
    if (controlName === 'archivoUno') {
        this.fileUnoUploadSuccess = false;
        this.fileUnoHdfsPath = null;
        this.fileUnoError = null;
        this.selectedFileUno = null; // Limpiar si hay un archivo previo
    } else if (controlName === 'archivoDos') {
        this.fileDosUploadSuccess = false;
        this.fileDosHdfsPath = null;
        this.fileDosError = null;
        this.selectedFileDos = null; // Limpiar si hay un archivo previo
    }

    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        this.alertService.error('El archivo excede el tamaño máximo permitido de 5MB.', { autoClose: true });
        this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'maxSize': true });
        event.target.value = null; // Limpiar el input file
        // No asignar el archivo si hay error
        return;
      }
      if (file.type !== 'application/pdf') {
        this.alertService.error('Solo se permiten archivos en formato PDF.', { autoClose: true });
        this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'invalidType': true });
        event.target.value = null; // Limpiar el input file
        // No asignar el archivo si hay error
        return;
      }

      if (controlName === 'archivoUno') {
        this.selectedFileUno = file;
      } else if (controlName === 'archivoDos') {
        this.selectedFileDos = file;
      }
      // Actualizar el valor del FormControl, pero sin marcarlo como válido/inválido por la subida
      this.formAcreditacionMembresia.get(controlName)?.setValue(file.name);
      this.formAcreditacionMembresia.get(controlName)?.markAsDirty();
      this.formAcreditacionMembresia.get(controlName)?.markAsTouched();
      this.formAcreditacionMembresia.get(controlName)?.updateValueAndValidity(); // Recalcular validez
    } else {
      // Si no se selecciona ningún archivo
      this.formAcreditacionMembresia.get(controlName)?.setValue('');
      this.formAcreditacionMembresia.get(controlName)?.markAsDirty();
      this.formAcreditacionMembresia.get(controlName)?.markAsTouched();
      this.formAcreditacionMembresia.get(controlName)?.updateValueAndValidity(); // Recalcular validez
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
    let loadingFlag: 'loadingFileUno' | 'loadingFileDos';
    let fileHdfsPath: 'fileUnoHdfsPath' | 'fileDosHdfsPath';
    let fileUploadSuccess: 'fileUnoUploadSuccess' | 'fileDosUploadSuccess';
    let fileError: 'fileUnoError' | 'fileDosError';
    let documentType: string;
    //let desRfcValue = this.rfcSesion; // <-- IMPORTANTE: Debes obtener el RFC real del usuario logueado o del formulario

    if (controlName === 'archivoUno') {
      fileToUpload = this.selectedFileUno;
      loadingFlag = 'loadingFileUno';
      fileHdfsPath = 'fileUnoHdfsPath';
      fileUploadSuccess = 'fileUnoUploadSuccess';
      fileError = 'fileUnoError';
      documentType = 'Acreditación';
    } else if (controlName === 'archivoDos') {
      fileToUpload = this.selectedFileDos;
      loadingFlag = 'loadingFileDos';
      fileHdfsPath = 'fileDosHdfsPath';
      fileUploadSuccess = 'fileDosUploadSuccess';
      fileError = 'fileDosError';
      documentType = 'Membresía';
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
      this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'required': true });
      return;
    }

    this[loadingFlag] = true; // Activar spinner

    const formData = new FormData();
    formData.append('archivo', fileToUpload, fileToUpload.name);
    formData.append('desRfc', desRfcValue); // Usar el RFC real
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
          // Opcional: Marcar el control de formulario como válido si la subida fue un éxito
          this.formAcreditacionMembresia.get(controlName)?.setErrors(null);
          this.formAcreditacionMembresia.get(controlName)?.updateValueAndValidity();
        } else {
          this[fileUploadSuccess] = false;
          this[fileHdfsPath] = null;
          this[fileError] = response.mensaje || `Error desconocido al cargar el archivo de ${documentType}.`;
          this.alertService.error(this[fileError] as string, { autoClose: true });
          this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'uploadFailed': true });
          this.formAcreditacionMembresia.get(controlName)?.updateValueAndValidity();
        }
      },
      error: (errorResponse: HttpErrorResponse) => {
        this[loadingFlag] = false; // Desactivar spinner
        console.error(`Error al cargar el archivo de ${documentType}:`, errorResponse);
        let errorMessage = `Error al cargar el archivo de ${documentType}. Inténtalo de nuevo.`;

        if (errorResponse.error instanceof Object && errorResponse.error.mensaje) {
          errorMessage = errorResponse.error.mensaje; // Si el backend devuelve un DTO de error estructurado
        } else if (errorResponse.message) {
          errorMessage = errorResponse.message;
        }

        this[fileUploadSuccess] = false;
        this[fileHdfsPath] = null;
        this[fileError] = errorMessage;
        this.alertService.error(errorMessage, { autoClose: true });
        this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'uploadFailed': true });
        this.formAcreditacionMembresia.get(controlName)?.updateValueAndValidity();
      }
    });
  }





  onReiniciarFormAcreditacionMembresia() {
    console.log('Botón Cancelar presionado');
    this.alertService.clear(); // Limpiar alerts previos

    // Verificar si hay archivos cargados
    const hasUploadedFiles = this.fileUnoHdfsPath !== null || this.fileDosHdfsPath !== null;

    const message = hasUploadedFiles
      ? 'Se detectaron archivos cargados. ¿Estás seguro de que deseas cancelar la solicitud de Acreditación y Membresía? Los archivos se eliminarán permanentemente.'
      : '¿Estás seguro de que deseas cancelar la solicitud de Acreditación y Membresía?';

    this.modalService.showDialog(
      'confirm',
      'warning',
      'Confirmar Cancelación',
      message,
      (confirmed: boolean) => {
        if (confirmed) {
          this.alertService.info('Cancelando proceso...', { autoClose: true });

          const deleteObservables = [];

          if (this.fileUnoHdfsPath) {
            deleteObservables.push(
              this.acreditacionMembresiaService.deleteDocument(this.fileUnoHdfsPath).pipe(
                catchError(error => {
                  console.error('Error al eliminar archivo de Acreditación:', error);
                  this.alertService.error('Error al eliminar el archivo de Acreditación. Continuar con la cancelación.', { autoClose: true });
                  return of(null); // Retorna un observable que emite null para que forkJoin no se detenga
                })
              )
            );
          }

          if (this.fileDosHdfsPath) {
            deleteObservables.push(
              this.acreditacionMembresiaService.deleteDocument(this.fileDosHdfsPath).pipe(
                catchError(error => {
                  console.error('Error al eliminar archivo de Membresía:', error);
                  this.alertService.error('Error al eliminar el archivo de Membresía. Continuar con la cancelación.', { autoClose: true });
                  return of(null); // Retorna un observable que emite null para que forkJoin no se detenga
                })
              )
            );
          }

          if (deleteObservables.length > 0) {
            forkJoin(deleteObservables).subscribe({
              next: () => {
                this.performResetAndRedirect();
                this.alertService.success('Archivos eliminados y cancelación confirmada.', { autoClose: true });
              },
              error: (err) => {
                // Este error solo se capturaría si forkJoin falla completamente sin catchError en los pipes individuales
                console.error('Error general al eliminar archivos:', err);
                this.alertService.error('Ocurrió un error al intentar eliminar algunos archivos. Se procederá con la cancelación.', { autoClose: true });
                this.performResetAndRedirect();
              }
            });
          } else {
            // No hay archivos para eliminar, simplemente resetear y redirigir
            this.performResetAndRedirect();
            this.alertService.success('Cancelación confirmada.', { autoClose: true });
          }

        } else {
          // El usuario canceló la operación en la modal
          this.alertService.info('La cancelación ha sido anulada.', { autoClose: true });
        }
      },
      'Si', // Texto para el botón de confirmar
      'No'  // Texto para el botón de cancelar
    );
  }

  /**
   * Método auxiliar para resetear el formulario y redirigir,
   * para evitar duplicación de código.
   */
  private performResetAndRedirect() {
    this.formAcreditacionMembresia.reset();
    this.selectedFileUno = null;
    this.selectedFileDos = null;
    this.fileUnoUploadSuccess = false;
    this.fileDosUploadSuccess = false;
    this.fileUnoHdfsPath = null;
    this.fileDosHdfsPath = null;
    this.fileUnoError = null;
    this.fileDosError = null;
    this.loadingFileUno = false;
    this.loadingFileDos = false;
    this.responseDto = null;

    // Limpiar inputs de tipo file en el DOM
    const fileInputUno = document.getElementById('archivoUno') as HTMLInputElement;
    const fileInputDos = document.getElementById('archivoDos') as HTMLInputElement;
    if (fileInputUno) fileInputUno.value = '';
    if (fileInputDos) fileInputDos.value = '';

    // Restablecer el estado de los controles del formulario
    Object.keys(this.formAcreditacionMembresia.controls).forEach(key => {
      this.formAcreditacionMembresia.get(key)?.markAsPristine();
      this.formAcreditacionMembresia.get(key)?.markAsUntouched();
      this.formAcreditacionMembresia.get(key)?.updateValueAndValidity();
    });

    // Redirigir a la página inicial
    this.router.navigate(['/home']);
  }


downloadFile(hdfsPath: string | null, fileName: string) {
    if (hdfsPath) {
      this.alertService.info(`Iniciando descarga de "${fileName}"...`, { autoClose: true });

      this.acreditacionMembresiaService.downloadDocument(hdfsPath).subscribe({
        next: (response: HttpResponse<Blob>) => {
          if (response.body) {
            // 1. Obtener el nombre del archivo del Content-Disposition si está disponible
            const contentDisposition = response.headers.get('Content-Disposition');
            let actualFileName = fileName; // Usar el nombre que se pasó por defecto
            if (contentDisposition) {
              const matches = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/.exec(contentDisposition);
              if (matches != null && matches[1]) {
                actualFileName = matches[1].replace(/['"]/g, '');
              }
            }

            // 2. Crear un objeto URL para el Blob
            const url = window.URL.createObjectURL(response.body);

            // 3. Crear un enlace (<a>) en el DOM
            const a = document.createElement('a');
            a.href = url;
            a.download = actualFileName; // Establecer el nombre de archivo para la descarga
            document.body.appendChild(a); // Es necesario que el enlace esté en el DOM para poder hacer click programáticamente

            // 4. Hacer clic programáticamente en el enlace para iniciar la descarga
            a.click();

            // 5. Limpiar: remover el enlace y revocar la URL del objeto Blob
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
              // Si el error es un Blob (ej: un JSON de error devuelto como blob),
              // intentar leerlo como texto. Esto es común en Spring Boot.
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
              return; // Salir para que el alert se muestre después de leer el Blob
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

    if (controlName === 'archivoUno' && this.fileUnoHdfsPath) {
      hdfsPathToDelete = this.fileUnoHdfsPath;
      fileName = this.selectedFileUno?.name || 'Archivo de Acreditación';
      documentType = 'Acreditación';
    } else if (controlName === 'archivoDos' && this.fileDosHdfsPath) {
      hdfsPathToDelete = this.fileDosHdfsPath;
      fileName = this.selectedFileDos?.name || 'Archivo de Membresía';
      documentType = 'Membresía';
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
              // Lógica para resetear el estado del archivo en el frontend
              if (controlName === 'archivoUno') {
                this.fileUnoUploadSuccess = false;
                this.fileUnoHdfsPath = null;
                this.selectedFileUno = null;
                const fileInputUno = document.getElementById('archivoUno') as HTMLInputElement;
                if (fileInputUno) fileInputUno.value = '';
                this.formAcreditacionMembresia.get('archivoUno')?.setValue('');
                this.formAcreditacionMembresia.get('archivoUno')?.markAsUntouched();
                this.formAcreditacionMembresia.get('archivoUno')?.setErrors({ 'required': true }); // Marcarlo como requerido nuevamente
                this.fileUnoError = null;
              } else if (controlName === 'archivoDos') {
                this.fileDosUploadSuccess = false;
                this.fileDosHdfsPath = null;
                this.selectedFileDos = null;
                const fileInputDos = document.getElementById('archivoDos') as HTMLInputElement;
                if (fileInputDos) fileInputDos.value = '';
                this.formAcreditacionMembresia.get('archivoDos')?.setValue('');
                this.formAcreditacionMembresia.get('archivoDos')?.markAsUntouched();
                this.formAcreditacionMembresia.get('archivoDos')?.setErrors({ 'required': true }); // Marcarlo como requerido nuevamente
                this.fileDosError = null;
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
          // El usuario canceló la eliminación
          this.alertService.info('La eliminación del archivo ha sido cancelada.', { autoClose: true });
        }
      },
      'Eliminar', // Texto para el botón de confirmar
      'Cancelar'  // Texto para el botón de cancelar
    );
  }




  continuarConAcuse(): void {
    // Verificar la validez del formulario y si los archivos han sido cargados exitosamente
    if (this.formAcreditacionMembresia.valid) {

      // Inicializar docMem y docAcr como null por defecto
      let docMem: string | null = null;
      let docAcr: string | null = null;

      // Validar si this.fileUnoHdfsPath fue cargado
      if (this.fileUnoHdfsPath) {
        docAcr = "Si";
      }

      // Validar si this.fileDosHdfsPath fue cargado
      if (this.fileDosHdfsPath) {
        docMem = "Si";
      }

      // Si ambos archivos fueron cargados exitosamente, proceed
      if (this.fileUnoUploadSuccess && this.fileDosUploadSuccess) {
        // Obtener los datos actuales del formulario
        const datosDelFormulario = this.formAcreditacionMembresia.value;

        let fechaDocMem: string | null = null;
        let fechaDocAcr: string | null = null;

        // Formatear fechaExpedicionMembresia si se proporcionó
        if (datosDelFormulario.fechaExpedicionMembresia) {
          fechaDocMem = this.datePipe.transform(datosDelFormulario.fechaExpedicionMembresia, 'dd/MM/yyyy');
        }

        // Formatear fechaExpedicionAcreditacion si se proporcionó
        if (datosDelFormulario.fechaExpedicionAcreditacion) {
          fechaDocAcr = this.datePipe.transform(datosDelFormulario.fechaExpedicionAcreditacion, 'dd/MM/yyyy');
        }


        const nombreCompletoSesion = this.nombreCompletoSync; // Usa el getter síncrono del BaseComponent
        const rfcSesion = this.rfcSesion;
        const curpSesion = this.curpSesion;




       const cadenaOriginal = `${nombreCompletoSesion}|${this.rfcSesion}|${this.curpSesion}`;

        console.warn('nombreCompletoSesion: '+ nombreCompletoSesion);
        console.warn('rfcSesion: '+ curpSesion);
        console.warn('curpSesion'+ curpSesion );
        console.warn('cadenaOriginal: '+ cadenaOriginal);

        const datosCompletosParaAcuse = {
          ...datosDelFormulario, // Extiende todos los campos del formulario
          desPathHdfsAcreditacion: this.fileUnoHdfsPath, // Añade el path del archivo uno
          desPathHdfsMembresia: this.fileDosHdfsPath,      // Añade el path del archivo dos
          docMem: docMem, // Agrega el valor de docMem
          docAcr: docAcr,  // Agrega el valor de docAcr
          fechaDocMem: fechaDocMem, // Agrega la fecha de membresía formateada
          fechaDocAcr: fechaDocAcr,  // Agrega la fecha de acreditación formateada
          nombreCompleto: nombreCompletoSesion, // Añade el nombre completo
          RFC: rfcSesion,  // Añade el RFC
          CURP: curpSesion,// Añade el CURP
          cadenaOriginal: cadenaOriginal
        };

        // Guardar los datos completos en el servicio
        this.acreditacionMembresiaDataService.setDatosFormularioPrevio(datosCompletosParaAcuse);

        // Navegar al componente de acuse
        this.router.navigate([NAV.contadoracreditacionymembresiaacuse]);
      } else {
        // Si el formulario es válido pero faltan archivos o no se cargaron correctamente
        console.warn('Faltan archivos por cargar o la carga no fue exitosa.');
        this.alertService.error('Por favor, asegúrate de que ambos archivos estén cargados correctamente antes de continuar.', { autoClose: true });
      }
    } else {
      // Si el formulario no es válido
      console.warn('El formulario no es válido.');
      // Opcional: Marcar todos los campos como "touched" para que se muestren los mensajes de error de validación
      this.formAcreditacionMembresia.markAllAsTouched();
      this.alertService.error('Por favor, completa todos los campos requeridos del formulario antes de continuar.', { autoClose: true });
    }
  }



}
