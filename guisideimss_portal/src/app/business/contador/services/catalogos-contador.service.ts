import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { EPs } from '../../../global/endPoint';

@Injectable({
  providedIn: 'root'
})
export class CatalogosContadorService {

    constructor(private httpClient: HttpClient) { }

  /**
   * Obtiene la lista de tipos de datos de contador desde el backend.
   * @returns Un Observable que emite una lista de strings (ej: 'Personales', 'Del Despacho', 'Del Colegio').
   */
  getTiposDatosContador(): Observable<string[]> {
    const url = `${environment.catalogosApiUrl}${EPs.catalogo.tiposDatosContador}`;
    return this.httpClient.get<string[]>(url);
  }
}
