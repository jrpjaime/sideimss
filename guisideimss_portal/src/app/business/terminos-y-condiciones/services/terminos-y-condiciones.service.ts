import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { EPs } from '../../../global/endPoint';

@Injectable({
  providedIn: 'root'
})
export class TerminosYCondicionesService {

    constructor(
    private httpClient: HttpClient,
    private router: Router
  ) { }



 

  public aceptarTerminos(rfc: string): Observable<any> {
    const body = { rfc: rfc }; // Creamos un objeto para el cuerpo de la petición
    // MODIFICADO: Enviamos el cuerpo en la petición POST
    return this.httpClient.post<any>(environment.seguridadApiUrl + EPs.oauth.aceptarTerminos, body);
  }
}
