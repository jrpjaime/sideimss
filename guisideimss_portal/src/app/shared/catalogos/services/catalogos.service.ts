import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { EPs } from '../../../global/endPoint';

import { DitPatronGeneralFiltroDto } from '../model/ditpatrongeneralfiltrodto';  
import { SdcPeriodoFiltroDto } from '../model/sdcperiodofiltrodto';





@Injectable({
  providedIn: 'root'
})
export class CatalogosService {

  constructor(
    private httpClient: HttpClient,
    private router: Router
  ) { }


  public list(): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.list, null);
  }

  public listRegistrosPatronales(ditPatronGeneralFiltroDto:DitPatronGeneralFiltroDto): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listRegistrosPatronales, ditPatronGeneralFiltroDto);
  }

  public listPaginatedRegistrosPatronales(ditPatronGeneralFiltroDto:DitPatronGeneralFiltroDto): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listPaginatedRegistrosPatronales, ditPatronGeneralFiltroDto);
  }

  public listPaginatedSolicitudDevolucion(ditPatronGeneralFiltroDto:DitPatronGeneralFiltroDto): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listPaginatedRegistrosPatronales, ditPatronGeneralFiltroDto);
  }

  public listMotivoDevolucion( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listMotivoDevolucion, null);
  }
 

 

  public listEstatusSolicitud( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listEstatusSolicitud, null);
  }

  public listTipoSeguro( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listTipoSeguro, null);
  }

  public listTipoCuota( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listTipoCuota, null);
  }


  public listTipoDocumento( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listTipoDocumento, null);
  }

  public listEntidadRecaudadora( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listEntidadRecaudadora, null);
  }




  public listPeriodoFiltro(sdcPeriodoFiltroDto: SdcPeriodoFiltroDto ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listPeriodoFiltro, sdcPeriodoFiltroDto);
  }


  public listCiclo( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listCiclo, null);
  }


  public listTipoPension( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listTipoPension, null);
  }


  public listJornada( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listJornada, null);
  }



  public listMovimiento( ): Observable<any> {
    return this.httpClient.post<any>(environment.catalogosApiUrl + EPs.catalogo.listMovimiento, null);
  }




}



