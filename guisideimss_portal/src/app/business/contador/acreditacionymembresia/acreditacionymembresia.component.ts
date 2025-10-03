import { Component, Renderer2 } from '@angular/core';
import { BaseComponent } from '../../../shared/base/base.component';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CatalogosService } from '../../../shared/catalogos/services/catalogos.service';
import { SharedService } from '../../../shared/services/shared.service'; 
import { fechaInicioMenorOigualFechaFin } from '../../../global/validators';

@Component({
  selector: 'app-acreditacionymembresia',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './acreditacionymembresia.component.html',
  styleUrl: './acreditacionymembresia.component.css'
})
export class AcreditacionymembresiaComponent extends BaseComponent {


  formAcreditacionMembresia: FormGroup;


    constructor (
    private fb: FormBuilder,
    private router : Router,
    private renderer: Renderer2,
    private catalogosService: CatalogosService, 
    sharedService: SharedService
  ) {
            super(sharedService);
 



            this.formAcreditacionMembresia = this.fb.group({ 
            fechaExpedicionAcreditacion: ['', [Validators.required]], // Control para la Fecha 
            fechaExpedicionMembresia: ['', [Validators.required]], // Control para la Fecha 

          }, { validators: fechaInicioMenorOigualFechaFin() } );

  }






    onSubmit() {
    console.log('Formulario enviado:', this.formAcreditacionMembresia.value);
    // Aquí puedes añadir la lógica para procesar el formulario
    if (this.formAcreditacionMembresia.valid) {
      // Hacer algo con los datos del formulario
    } else {
      // Marcar todos los campos como "touched" para mostrar los mensajes de error
      this.formAcreditacionMembresia.markAllAsTouched();
    }
  }



    onReiniciarTrabajador() {
    console.log('Botón Cancelar presionado');
    // Aquí puedes añadir la lógica para reiniciar el formulario o cancelar la operación
    this.formAcreditacionMembresia.reset(); // Reinicia el formulario
  }

}
