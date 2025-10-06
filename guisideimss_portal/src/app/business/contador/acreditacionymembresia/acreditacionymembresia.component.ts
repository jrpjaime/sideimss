import { Component, Renderer2 } from '@angular/core';
import { BaseComponent } from '../../../shared/base/base.component';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CatalogosService } from '../../../shared/catalogos/services/catalogos.service';
import { SharedService } from '../../../shared/services/shared.service'; 
import { fechaInicioMenorOigualFechaFin } from '../../../global/validators';
import { AcreditacionMembresiaService } from '../services/services/acreditacion-membresia.service';

@Component({
  selector: 'app-acreditacionymembresia',
  standalone: true,
  imports: [ReactiveFormsModule, ReactiveFormsModule ],
  templateUrl: './acreditacionymembresia.component.html',
  styleUrl: './acreditacionymembresia.component.css'
})
export class AcreditacionymembresiaComponent extends BaseComponent {


  formAcreditacionMembresia: FormGroup;
  // Propiedades para almacenar los archivos seleccionados
  selectedFileUno: File | null = null;
  selectedFileDos: File | null = null;

    constructor (
    private fb: FormBuilder,
    private router : Router,
    private renderer: Renderer2,
    private catalogosService: CatalogosService, 
    private acreditacionMembresiaService: AcreditacionMembresiaService,
    sharedService: SharedService
  ) {
            super(sharedService);
 



            this.formAcreditacionMembresia = this.fb.group({ 
            fechaExpedicionAcreditacion: ['', [Validators.required]], // Control para la Fecha 
            fechaExpedicionMembresia: ['', [Validators.required]], // Control para la Fecha 
            archivoUno: ['', [Validators.required]], // Nuevo control para el archivo uno
            archivoDos: ['', [Validators.required]]  // Nuevo control para el archivo dos

          }, { validators: fechaInicioMenorOigualFechaFin() } );

  }






  // Método para manejar la selección de archivos
  onFileSelected(event: any, controlName: string) {
    const file: File = event.target.files[0];
    if (file) {
      // Validar tamaño del archivo (5MB = 5 * 1024 * 1024 bytes)
      if (file.size > 5 * 1024 * 1024) {
        alert('El archivo excede el tamaño máximo permitido de 5MB.');
        this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'maxSize': true });
        event.target.value = null; // Limpiar el input para permitir volver a seleccionar
        if (controlName === 'archivoUno') {
          this.selectedFileUno = null;
        } else if (controlName === 'archivoDos') {
          this.selectedFileDos = null;
        }
        return;
      }
      // Validar tipo de archivo (solo PDF)
      if (file.type !== 'application/pdf') {
        alert('Solo se permiten archivos en formato PDF.');
        this.formAcreditacionMembresia.get(controlName)?.setErrors({ 'invalidType': true });
        event.target.value = null;
        if (controlName === 'archivoUno') {
          this.selectedFileUno = null;
        } else if (controlName === 'archivoDos') {
          this.selectedFileDos = null;
        }
        return;
      }

      // Asignar el archivo a la propiedad correspondiente y marcar el control como válido
      if (controlName === 'archivoUno') {
        this.selectedFileUno = file;
      } else if (controlName === 'archivoDos') {
        this.selectedFileDos = file;
      }
      this.formAcreditacionMembresia.get(controlName)?.setValue(file.name); // Almacenar el nombre del archivo o similar
      this.formAcreditacionMembresia.get(controlName)?.updateValueAndValidity(); // Actualizar la validez
    } else {
      // Si no se seleccionó ningún archivo, invalidar el control
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
   
    if (this.formAcreditacionMembresia.valid && this.selectedFileUno && this.selectedFileDos) {
      const formData = new FormData();
      formData.append('fechaExpedicionAcreditacion', this.formAcreditacionMembresia.get('fechaExpedicionAcreditacion')?.value);
      formData.append('fechaExpedicionMembresia', this.formAcreditacionMembresia.get('fechaExpedicionMembresia')?.value);
      formData.append('archivoUno', this.selectedFileUno, this.selectedFileUno.name);
      formData.append('archivoDos', this.selectedFileDos, this.selectedFileDos.name);

      this.acreditacionMembresiaService.enviarAcreditacionMembresia(formData).subscribe({
        next: (response) => {
          console.log('Respuesta del backend:', response);
          alert('Acreditación y membresía enviada exitosamente!');
          this.onReiniciarFormAcreditacionMembresia();
        },
        error: (error) => {
          console.error('Error al enviar al backend:', error);
          alert('Hubo un error al enviar la información. Por favor, inténtalo de nuevo.');
        }
      });

    } else {
      this.formAcreditacionMembresia.markAllAsTouched();
      alert('Por favor, completa todos los campos requeridos y adjunta los archivos.');
    }
  }

  onReiniciarFormAcreditacionMembresia() {
    console.log('Botón Cancelar presionado'); 
    this.formAcreditacionMembresia.reset();
    this.selectedFileUno = null;
    this.selectedFileDos = null;
    // Limpiar los inputs de tipo file manualmente
    const fileInputUno = document.getElementById('archivoUno') as HTMLInputElement;
    const fileInputDos = document.getElementById('archivoDos') as HTMLInputElement;
    if (fileInputUno) fileInputUno.value = '';
    if (fileInputDos) fileInputDos.value = '';
  }

}
