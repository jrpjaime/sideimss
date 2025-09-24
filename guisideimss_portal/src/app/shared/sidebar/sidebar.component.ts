import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { SharedService } from '../services/shared.service';
import { ContextoPatronalService, RegistroPatronal } from '../../core/services/contexto-patronal.service';
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

  registroActual$: Observable<RegistroPatronal | null>;


  constructor(private authService: AuthService, 
              private contextoService: ContextoPatronalService,
              private sharedService: SharedService) {
              this.registroActual$ = this.contextoService.registroActual$;
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
