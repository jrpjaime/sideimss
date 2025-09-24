import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { TerminosYCondicionesService } from './services/terminos-y-condiciones.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { SharedService } from '../../shared/services/shared.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-terminos-y-condiciones',
  imports: [CommonModule, FormsModule],
  templateUrl: './terminos-y-condiciones.component.html',
  styleUrl: './terminos-y-condiciones.component.css'
})
export class TerminosYCondicionesComponent {
 haLeidoCompleto = false;
 terminosYaAceptados = false;

  constructor(
    private terminosYCondicionesService: TerminosYCondicionesService,
    private authService: AuthService,
    private router: Router,
    private sharedService: SharedService,
     private httpClient: HttpClient
  ) {}

  onScroll(event: Event): void {
    const element = event.target as HTMLElement;
    // Si el usuario ha llegado al final del scroll, se activa el checkbox
    if (element.scrollHeight - element.scrollTop <= element.clientHeight + 1) {
      this.haLeidoCompleto = true;
    }
  }


    ngOnInit(): void {
    // 1. Verificar el estado actual desde el token al cargar el componente
    this.verificarEstadoAceptacion();
  }


 


  aceptarTerminos(): void {
    if (!this.haLeidoCompleto) return; 
    const rfcUsuarioAutenticado = this.sharedService.currentRfcSesionValue;

    if (!rfcUsuarioAutenticado) {
      console.error("No se pudo obtener el RFC del usuario para aceptar los términos.");
      return;
    }

    // Ahora 'rfcUsuarioAutenticado' es un string, y la llamada es válida.
    this.terminosYCondicionesService.aceptarTerminos(rfcUsuarioAutenticado).subscribe({
      next: () => {
        this.terminosYaAceptados = true;
        this.authService.refreshToken().subscribe(() => {
          console.error("aceptarTerminos refreshToken.");
          this.router.navigate(['/home']);
        });
      },
      error: (err) => console.error("Error al aceptar los términos", err)
    });
  }


    private verificarEstadoAceptacion(): void {
    const token = this.authService.getToken();
    if (!token) {
      this.terminosYaAceptados = false;
      return;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      // Leemos el claim que añadimos en el JWT para saber si aceptó
      this.terminosYaAceptados = payload.terminosAceptados; 
    } catch (error) {
      console.error('Error al decodificar el token JWT para verificar aceptación:', error);
      this.terminosYaAceptados = false;
    }
  }
 

}
