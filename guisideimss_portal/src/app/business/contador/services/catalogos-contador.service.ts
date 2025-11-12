import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { EPs } from '../../../global/endPoint';
import { TipoDatoContadorDto } from '../model/TipoDatoContadorDto';
import { RfcColegioRequestDto } from '../model/RfcColegioRequestDto';
import { RfcColegioResponseDto } from '../model/RfcColegioResponseDto';
import { TipoSociedadFormaParteDto } from '../model/TipoSociedadFormaParteDto';
import { CargoContadorDto } from '../model/CargoContadorDto';

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



  /**
   * Obtiene la lista de tipos de sociedad a la que puede pertenecer un contador.
   * @returns Un Observable que emite una lista de TipoSociedadFormaParteDto.
   */
  getTiposSociedadFormaParte(): Observable<TipoSociedadFormaParteDto[]> {
    const url = `${environment.catalogosApiUrl}${EPs.catalogo.tiposSociedadFormaParte}`;
    return this.httpClient.get<TipoSociedadFormaParteDto[]>(url);
  }

  /**
   * Obtiene la lista de cargos de contador.
   * @returns Un Observable que emite una lista de CargoContadorDto.
   */
  getCargosContador(): Observable<CargoContadorDto[]> {
    const url = `${environment.catalogosApiUrl}${EPs.catalogo.cargosContador}`;
    return this.httpClient.get<CargoContadorDto[]>(url);
  }
  
  
}
