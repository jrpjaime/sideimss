import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { EPs } from '../../../global/endPoint';
import { TipoDatoContadorDto } from '../model/TipoDatoContadorDto';
import { RfcColegioRequestDto } from '../model/RfcColegioRequestDto';
import { RfcColegioResponseDto } from '../model/RfcColegioResponseDto';

@Injectable({
  providedIn: 'root'
})
export class CatalogosContadorService {

    constructor(private httpClient: HttpClient) { }

  /**
   * Obtiene la lista de tipos de datos de contador desde el backend.
   * @returns Un Observable que emite una lista de strings (ej: 'Personales', 'Del Despacho', 'Del Colegio').
   */
 getTiposDatosContador(): Observable<TipoDatoContadorDto[]> {
    const url = `${environment.catalogosApiUrl}${EPs.catalogo.tiposDatosContador}`;
    return this.httpClient.get<TipoDatoContadorDto[]>(url);
  }




  /**
   * Consulta los datos de un RFC (Nombre/Razón Social) en el servicio de catálogos.
   * @param rfcRequest DTO con el RFC a consultar.
   * @returns Un Observable que emite un RfcColegioResponseDto con el RFC y Nombre/Razón Social.
   */
  getDatoRfcColegio(rfcRequest: RfcColegioRequestDto): Observable<RfcColegioResponseDto> {
    const url = `${environment.catalogosApiUrl}${EPs.catalogo.datoRfc}`; // Usar el endpoint 'datoRfc'
    return this.httpClient.post<RfcColegioResponseDto>(url, rfcRequest);
  }  
}
