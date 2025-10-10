import { Component, Renderer2 } from '@angular/core';
import { BaseComponent } from '../../../shared/base/base.component';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CatalogosService } from '../../../shared/catalogos/services/catalogos.service';
import { SharedService } from '../../../shared/services/shared.service';
import { fechaInicioMenorOigualFechaFin } from '../../../global/validators';
import { AcreditacionMembresiaService } from '../services/services/acreditacion-membresia.service';
import { CommonModule } from '@angular/common';
import { AlertService } from '../../../shared/services/alert.service';
import { DocumentoIndividualResponseDto } from '../model/DocumentoIndividualResponseDto ';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ModalService } from '../../../shared/services/modal.service';

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

  loadingFileUno: boolean = false; // Nuevo: Para el spinner del botón Adjuntar
  loadingFileDos: boolean = false; // Nuevo: Para el spinner del botón Adjuntar

  responseDto: DocumentoIndividualResponseDto | null = null; // Para la respuesta final del submit, si aplica


  constructor (
    private fb: FormBuilder,
    private router : Router,
    private renderer: Renderer2,
    private catalogosService: CatalogosService,
    private modalService: ModalService,
    private acreditacionMembresiaService: AcreditacionMembresiaService,
    private alertService: AlertService,
    sharedService: SharedService
  ) {
    super(sharedService);

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

    let fileToUpload: File | null = null;
    let loadingFlag: 'loadingFileUno' | 'loadingFileDos';
    let fileHdfsPath: 'fileUnoHdfsPath' | 'fileDosHdfsPath';
    let fileUploadSuccess: 'fileUnoUploadSuccess' | 'fileDosUploadSuccess';
    let fileError: 'fileUnoError' | 'fileDosError';
    let documentType: string;
    let desRfcValue = 'RFCIMSS00001'; // <-- IMPORTANTE: Debes obtener el RFC real del usuario logueado o del formulario

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
    // Puedes enviar desPath si aplica, o dejarlo nulo para que el backend use el default
    // formData.append('desPath', 'subdirectorio_opcional');
    // Puedes enviar fechaActual si aplica, o dejarlo nulo
    // formData.append('fechaActual', 'DD/MM/AAAA');

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


   onSubmit() {
    console.log('Formulario de acreditación y membresía enviado:', this.formAcreditacionMembresia.value);
    this.alertService.clear();

    // Validar que las fechas sean válidas
    if (this.formAcreditacionMembresia.invalid) {
        this.formAcreditacionMembresia.markAllAsTouched();
        this.alertService.error('Por favor, completa correctamente las fechas requeridas.', { autoClose: true });
        return;
    }

    // Validar que ambos archivos hayan sido subidos exitosamente
    if (!this.fileUnoUploadSuccess || !this.fileDosUploadSuccess) {
      this.alertService.error('Por favor, adjunta y sube ambos archivos antes de verificar.', { autoClose: true });
      // Asegurarse de que los controles de archivo reflejen el estado de "requerido" si no se han subido
      if (!this.fileUnoUploadSuccess) {
        this.formAcreditacionMembresia.get('archivoUno')?.setErrors({ 'required': true });
        this.formAcreditacionMembresia.get('archivoUno')?.markAsTouched();
      }
      if (!this.fileDosUploadSuccess) {
        this.formAcreditacionMembresia.get('archivoDos')?.setErrors({ 'required': true });
        this.formAcreditacionMembresia.get('archivoDos')?.markAsTouched();
      }
      return;
    }

    // Crear un DTO para enviar al backend con los paths HDFS
    const submitDto = {
      fechaExpedicionAcreditacion: this.formAcreditacionMembresia.get('fechaExpedicionAcreditacion')?.value,
      fechaExpedicionMembresia: this.formAcreditacionMembresia.get('fechaExpedicionMembresia')?.value,
      desPathHdfsAcreditacion: this.fileUnoHdfsPath, // Envía el path HDFS en Base64
      desPathHdfsMembresia: this.fileDosHdfsPath     // Envía el path HDFS en Base64
      // Aquí puedes añadir otros campos si tu DTO final de submit los necesita
    };

    // Suponiendo que tienes un servicio para enviar estos datos finales
    // y que el backend tiene un endpoint diferente para la "verificación" final
    this.acreditacionMembresiaService.enviarDatosFinales(submitDto).subscribe({
        next: (response: any) => { // Ajusta el tipo de respuesta si tienes un DTO específico para el submit final
          console.log('Respuesta del backend (Verificación final):', response);
          if (response.codigo === 0) {
            this.alertService.success(response.mensaje || 'Verificación final exitosa.', { autoClose: true });
            // Posiblemente navegar a otra página o mostrar un mensaje de éxito grande
          } else {
            this.alertService.error(response.mensaje || 'Error en la verificación final.', { autoClose: true });
          }
        },
        error: (errorResponse: HttpErrorResponse) => {
            console.error('Error al enviar datos finales al backend:', errorResponse);
            let errorMessage = 'Hubo un error al realizar la verificación final. Por favor, inténtalo de nuevo.';
            if (errorResponse.error instanceof Object && errorResponse.error.mensaje) {
                errorMessage = errorResponse.error.mensaje;
            } else if (errorResponse.message) {
                errorMessage = errorResponse.message;
            }
            this.alertService.error(errorMessage, { autoClose: true });
        }
    });
  }


onReiniciarFormAcreditacionMembresia() {
    console.log('Botón Cancelar presionado');
    this.formAcreditacionMembresia.reset();
    this.selectedFileUno = null;
    this.selectedFileDos = null;
    this.fileUnoUploadSuccess = false;
    this.fileDosUploadSuccess = false;
    this.fileUnoHdfsPath = null;
    this.fileDosHdfsPath = null;
    this.fileUnoError = null;
    this.fileDosError = null;
    this.loadingFileUno = false; // Resetear estado de carga
    this.loadingFileDos = false; // Resetear estado de carga
    this.alertService.clear();
    this.responseDto = null;

    const fileInputUno = document.getElementById('archivoUno') as HTMLInputElement;
    const fileInputDos = document.getElementById('archivoDos') as HTMLInputElement;
    if (fileInputUno) fileInputUno.value = '';
    if (fileInputDos) fileInputDos.value = '';

    Object.keys(this.formAcreditacionMembresia.controls).forEach(key => {
      this.formAcreditacionMembresia.get(key)?.markAsPristine(); // Fixed here
      this.formAcreditacionMembresia.get(key)?.markAsUntouched();
      this.formAcreditacionMembresia.get(key)?.updateValueAndValidity();
    });
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
      this.alertService.error('No hay una ruta HDFS para descargar este archivo.', { autoClose: true });
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
      this.alertService.error('No hay un archivo cargado para eliminar o no se encontró la ruta HDFS.', { autoClose: true });
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
}
