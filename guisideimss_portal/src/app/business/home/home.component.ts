import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; // Necesario para ngIf/ngFor 
import { SharedService } from '../../shared/services/shared.service';
import { BaseComponent } from '../../shared/base/base.component';
 

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule], // Solo necesitamos CommonModule para mostrar datos
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent extends BaseComponent implements OnInit {

  // Propiedades para mostrar en la pantalla
  nombreCompleto: string = '';
  rfcUsuario: string = '';
  curpUsuario: string = '';

  constructor(
    sharedService: SharedService // Mantenemos SharedService para acceder a los datos del usuario
  ) {
    super(sharedService); // Llama al constructor de BaseComponent
  }

   override ngOnInit(): void {
    console.log('ngOnInit HomeComponent');
    this.recargaParametros(); // Carga los parámetros del usuario del BaseComponent

    // Asigna los datos del usuario a las propiedades del componente
    this.nombreCompleto = `${this.nombreSesion} ${this.primerApellidoSesion} ${this.segundoApellidoSesion}`;
    this.rfcUsuario = this.rfcSesion;
    this.curpUsuario = this.curpSesion;
  }

}