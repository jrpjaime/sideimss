import { Component, OnInit } from '@angular/core'; // Importa OnInit
import { CommonModule } from '@angular/common'; // Necesario para ngIf y otros 
import { ContadorPublicoAutorizadoService } from '../services/contador-publico-autorizado.service'; // Importa el servicio
import { SolicitudBajaDto } from '../model/SolicitudBajaDto';

@Component({
  selector: 'app-solicitudbaja',
  standalone: true,
  imports: [CommonModule], // Añade CommonModule para usar *ngIf, etc.
  templateUrl: './solicitudbaja.component.html',
  styleUrl: './solicitudbaja.component.css'
})
export class SolicitudbajaComponent implements OnInit { // Implementa OnInit

  solicitudBajaData: SolicitudBajaDto | null = null;
  loading: boolean = true;
  error: string | null = null;

  constructor(private contadorPublicoAutorizadoService: ContadorPublicoAutorizadoService) { }

  ngOnInit(): void {
    this.cargarDatosContador();
  }

  cargarDatosContador(): void {
    this.loading = true;
    this.error = null;
    this.contadorPublicoAutorizadoService.getDatosContador().subscribe({
      next: (data) => {
        this.solicitudBajaData = data;
        this.loading = false;
        console.log('Datos del contador cargados:', this.solicitudBajaData);
      },
      error: (err) => {
        console.error('Error al cargar los datos del contador:', err);
        this.error = 'No se pudieron cargar los datos del contador. Intente de nuevo más tarde.';
        this.loading = false;
      }
    });
  }
}