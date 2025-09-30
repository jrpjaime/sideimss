import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { SharedService } from '../services/shared.service';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';
import { BaseComponent } from '../base/base.component';

@Component({
    selector: 'app-sidebar',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './sidebar.component.html',
    styleUrl: './sidebar.component.css'
})
export class SidebarComponent extends BaseComponent implements OnInit {
  // Propiedades para mostrar en la pantalla
  nombreCompleto: string = '';

  constructor(
    private authService: AuthService,
    sharedService: SharedService // Mantenemos SharedService para acceder a los datos del usuario
  ) {
    super(sharedService); // Llama al constructor de BaseComponent
  }

 

  logout(): void {
    this.authService.logout();
  }

  override ngOnInit(): void {

    this.recargaParametros(); // Carga los parámetros del usuario del BaseComponent

    this.nombreCompleto = `${this.nombreSesion} ${this.primerApellidoSesion} ${this.segundoApellidoSesion}`;
  }


 

}





 