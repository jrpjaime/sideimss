import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { SolicitudBajaDto } from '../model/SolicitudBajaDto';
import { environment } from '../../../../environments/environment';
import { EPs } from '../../../global/endPoint';

@Injectable({
  providedIn: 'root'
})
export class ContadorPublicoAutorizadoService {

    constructor(
    private httpClient: HttpClient,
    private router: Router ) { }


  /**
     * Obtiene los datos completos del contador (personales, fiscales, contacto)
     * del endpoint /consultaDatosContador.
     * @returns Un Observable con el objeto SolicitudBajaDto.
     */
    getDatosContador(): Observable<SolicitudBajaDto> { 
        const url = `${environment.contadoresApiUrl}${EPs.contadores.consultaDatosContador}`;
        console.log('URL de consulta de datos del contador:', url); // Para depuración
        return this.httpClient.get<SolicitudBajaDto>(url);
    }
}
