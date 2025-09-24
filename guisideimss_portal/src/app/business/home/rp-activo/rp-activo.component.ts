import { Component } from '@angular/core';
import { ContextoPatronalService, RegistroPatronal } from '../../../core/services/contexto-patronal.service';
import { Observable } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-rp-activo',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './rp-activo.component.html',
  styleUrls: ['./rp-activo.component.css']
})
export class RpActivoComponent {
  
  registroActual$: Observable<RegistroPatronal | null>;

  constructor(private contextoService: ContextoPatronalService) {
    this.registroActual$ = this.contextoService.registroActual$;
  }

  cambiarRegistroPatronal(): void {
    this.contextoService.limpiarRegistro();
  }
}