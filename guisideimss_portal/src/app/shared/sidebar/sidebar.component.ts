import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { SharedService } from '../services/shared.service';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
    selector: 'app-sidebar',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './sidebar.component.html',
    styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  nombreSesion: string = '';
  primerApellidoSesion: string = '';
  segundoApellidoSesion: string = '';




  constructor(private authService: AuthService,
              private sharedService: SharedService) {

               }

  logout(): void {
    this.authService.logout();
  }

  ngOnInit(): void {
    // Suscribirse al nombre del usuario desde SharedService
    this.sharedService.currentNombreSesion.subscribe(nombre => {
      this.nombreSesion = nombre;
    });

    this.sharedService.currentPrimerApellidoSesion.subscribe(primerApellido => {
      this.primerApellidoSesion = primerApellido;
    });

    this.sharedService.currentSegundoApellidoSesion.subscribe(segundoApellido => {
      this.segundoApellidoSesion = segundoApellido;
    });
  }

}
