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
import { HttpErrorResponse } from '@angular/common/http'; 

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
  fileUnoHdfsPath: string | null = null;
  fileDosHdfsPath: string | null = null;
  fileUnoError: string | null = null;
  fileDosError: string | null = null;

  // Propiedad para almacenar la respuesta completa
  responseDto: DocumentoIndividualResponseDto | null = null;

  constructor (
    private fb: FormBuilder,
    private router : Router,
    private renderer: Renderer2,
    private catalogosService: CatalogosService, 
    private acreditacionMembresiaService: AcreditacionMembresiaService,
    private alertService: AlertService,
    sharedService: SharedService
  ) {
    super(sharedService);

    this.formAcreditacionMembresia = this.fb.group({
      fechaExpedicionAcreditacion: ['', [Validators.required]],
      fechaExpedicionMembresia: ['', [Validators.required]],
      archivoUno: ['', [Validators.required]],
      archivoDos: ['', [Validators.required]]
    }, { validators: fechaInicioMenorOigualFechaFin() });
  }

  onFileSelected(event: any, controlName: string) {
    if (controlName === 'archivoUno') {
      this.fileUnoUploadSuccess = false;
      this.fileUnoHdfsPath = null;
      this.fileUnoError = null;
    } else if (controlName === 'archivoDos') {
      this.fileDosUploadSuccess = false;
      this.fileDosHdfsPath = null;
      this.fileDosError = null;
    }
    this.alertService.clear();

    const file: File = event.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) {
        this.alertService.error('El archivo excede el tamaño máximo permitido de 5MB.', { autoClose: true });
        this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'maxSize': true });
        event.target.value = null;
        if (controlName === 'archivoUno') {
          this.selectedFileUno = null;
        } else if (controlName === 'archivoDos') {
          this.selectedFileDos = null;
        }
        return;
      }
      if (file.type !== 'application/pdf') {
        this.alertService.error('Solo se permiten archivos en formato PDF.', { autoClose: true });
        this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'invalidType': true });
        event.target.value = null;
        if (controlName === 'archivoUno') {
          this.selectedFileUno = null;
        } else if (controlName === 'archivoDos') {
          this.selectedFileDos = null;
        }
        return;
      }

      if (controlName === 'archivoUno') {
        this.selectedFileUno = file;
      } else if (controlName === 'archivoDos') {
        this.selectedFileDos = file;
      }
      this.formAcreditacionMembresia.get(controlName)?.setValue(file.name);
      this.formAcreditacionMembresia.get(controlName)?.updateValueAndValidity();
    } else {
      this.formAcreditacionMembresia.get(controlName)?.setValue('');
      this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'required': true });
      if (controlName === 'archivoUno') {
        this.selectedFileUno = null;
      } else if (controlName === 'archivoDos') {
        this.selectedFileDos = null;
      }
    }
  }

onSubmit() {
    console.log('Formulario enviado:', this.formAcreditacionMembresia.value);
    this.alertService.clear();
    this.fileUnoError = null;
    this.fileDosError = null;
    this.fileUnoUploadSuccess = false;
    this.fileDosUploadSuccess = false;
    this.fileUnoHdfsPath = null;
    this.fileDosHdfsPath = null;
    this.responseDto = null; // Limpiar DTO de respuesta anterior

    if (this.formAcreditacionMembresia.valid && this.selectedFileUno && this.selectedFileDos) {
      const formData = new FormData();
      formData.append('fechaExpedicionAcreditacion', this.formAcreditacionMembresia.get('fechaExpedicionAcreditacion')?.value);
      formData.append('fechaExpedicionMembresia', this.formAcreditacionMembresia.get('fechaExpedicionMembresia')?.value);
      formData.append('archivoUno', this.selectedFileUno, this.selectedFileUno.name);
      formData.append('archivoDos', this.selectedFileDos, this.selectedFileDos.name);

      this.acreditacionMembresiaService.enviarAcreditacionMembresia(formData).subscribe({
        next: (response: DocumentoIndividualResponseDto) => {
          console.log('Respuesta del backend:', response);
          this.responseDto = response; // Almacenar la respuesta completa

          // Siempre intentar asignar los paths HDFS si vienen en la respuesta,
          // independientemente del código de éxito o error.
          this.fileUnoHdfsPath = response.desPathHdfsAcreditacion || null;
          this.fileUnoUploadSuccess = !!this.fileUnoHdfsPath;

          this.fileDosHdfsPath = response.desPathHdfsMembresia || null;
          this.fileDosUploadSuccess = !!this.fileDosHdfsPath;

          if (response.codigo === 0) { // Suponiendo que 0 es éxito
              this.alertService.success(response.mensaje || 'Acreditación y membresía enviadas exitosamente!', { autoClose: true });
              this.fileUnoError = null; // Asegurarse de limpiar errores si ahora es éxito
              this.fileDosError = null;
          } else {
              let errorMessage = response.mensaje || 'Error desconocido al enviar la información.';
              this.alertService.error(errorMessage, { autoClose: true });

              // Lógica para errores específicos si el backend los detalla
              if (response.mensaje?.includes('Acreditación (Código:')) {
                  this.fileUnoError = 'Fallo en la carga del archivo de acreditación.';
              } else if (!this.fileUnoUploadSuccess) { // Si no hubo path HDFS y hay error general
                  this.fileUnoError = 'Fallo al cargar archivo de acreditación.';
              }

              if (response.mensaje?.includes('Membresía (Código:')) {
                  this.fileDosError = 'Fallo en la carga del archivo de membresía.';
              } else if (!this.fileDosUploadSuccess) { // Si no hubo path HDFS y hay error general
                  this.fileDosError = 'Fallo al cargar archivo de membresía.';
              }
          }
        },
        error: (errorResponse: HttpErrorResponse) => { // Especificar el tipo HttpErrorResponse
          console.error('Error al enviar al backend:', errorResponse);
          let errorMessage = 'Hubo un error al enviar la información. Por favor, inténtalo de nuevo.';

          if (errorResponse.error instanceof Object) { // Verificar si errorResponse.error es un objeto
            const errorDto: DocumentoIndividualResponseDto = errorResponse.error;
            // Asegúrate de que el objeto de error tenga la estructura de DocumentoIndividualResponseDto
            if (errorDto.mensaje) {
                this.responseDto = errorDto; // Almacenar el DTO de error

                // Siempre intentar asignar los paths HDFS si vienen en la respuesta de error
                this.fileUnoHdfsPath = errorDto.desPathHdfsAcreditacion || null;
                this.fileUnoUploadSuccess = !!this.fileUnoHdfsPath;

                this.fileDosHdfsPath = errorDto.desPathHdfsMembresia || null;
                this.fileDosUploadSuccess = !!this.fileDosHdfsPath;

                errorMessage = errorDto.mensaje;
                 // Lógica para errores específicos basada en el mensaje o código del errorDto
                if (errorDto.mensaje?.includes('Error al procesar los archivos de entrada')) {
                    this.fileUnoError = 'Error al procesar el archivo de acreditación.';
                    this.fileDosError = 'Error al procesar el archivo de membresía.';
                } else if (!this.fileUnoUploadSuccess) { // Si no se subió exitosamente
                    this.fileUnoError = 'Error al cargar el archivo de acreditación.';
                }
                if (errorDto.mensaje?.includes('No se pudo obtener el token de seguridad')) {
                    errorMessage = 'Error de autenticación: No se pudo obtener el token de seguridad.';
                } else if (!this.fileDosUploadSuccess) { // Si no se subió exitosamente
                    this.fileDosError = 'Error al cargar el archivo de membresía.';
                }
            }
          } else if (errorResponse.message) {
            errorMessage = errorResponse.message;
          }
          this.alertService.error(errorMessage, { autoClose: true });
        }
      });

    } else {
      this.formAcreditacionMembresia.markAllAsTouched();
      this.alertService.error('Por favor, completa todos los campos requeridos y adjunta los archivos.', { autoClose: true });
    }
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
    this.alertService.clear();
    this.responseDto = null; // Limpiar el DTO de respuesta

    const fileInputUno = document.getElementById('archivoUno') as HTMLInputElement;
    const fileInputDos = document.getElementById('archivoDos') as HTMLInputElement;
    if (fileInputUno) fileInputUno.value = '';
    if (fileInputDos) fileInputDos.value = '';

    Object.keys(this.formAcreditacionMembresia.controls).forEach(key => {
      this.formAcreditacionMembresia.get(key)?.markAsPristine();
      this.formAcreditacionMembresia.get(key)?.markAsUntouched();
      this.formAcreditacionMembresia.get(key)?.updateValueAndValidity();
    });
  }

  downloadFile(hdfsPath: string | null, fileName: string) {
    if (hdfsPath) {
      this.alertService.info(`Simulando descarga de "${fileName}" desde: ${hdfsPath}`, { autoClose: true });
      // Aquí integrarías la lógica real para descargar el archivo.
      // Ejemplo: this.acreditacionMembresiaService.downloadDocument(hdfsPath).subscribe(...)
    } else {
      this.alertService.error('No hay una ruta HDFS para descargar este archivo.', { autoClose: true });
    }
  }

  deleteFile(controlName: string) {
    this.alertService.info(`Simulando eliminación del archivo para: ${controlName}`, { autoClose: true });
    if (controlName === 'archivoUno') {
      this.fileUnoUploadSuccess = false;
      this.fileUnoHdfsPath = null;
      this.selectedFileUno = null;
      const fileInputUno = document.getElementById('archivoUno') as HTMLInputElement;
      if (fileInputUno) fileInputUno.value = '';
      this.formAcreditacionMembresia.get('archivoUno')?.setValue('');
      this.formAcreditacionMembresia.get('archivoUno')?.markAsUntouched();
      this.formAcreditacionMembresia.get('archivoUno')?.setErrors({ 'required': true });
      this.fileUnoError = null;
    } else if (controlName === 'archivoDos') {
      this.fileDosUploadSuccess = false;
      this.fileDosHdfsPath = null;
      this.selectedFileDos = null;
      const fileInputDos = document.getElementById('archivoDos') as HTMLInputElement;
      if (fileInputDos) fileInputDos.value = '';
      this.formAcreditacionMembresia.get('archivoDos')?.setValue('');
      this.formAcreditacionMembresia.get('archivoDos')?.markAsUntouched();
      this.formAcreditacionMembresia.get('archivoDos')?.setErrors({ 'required': true });
      this.fileDosError = null;
    }
    this.alertService.clear();
  }
}
