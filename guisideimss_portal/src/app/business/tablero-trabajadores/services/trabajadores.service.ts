import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { EPs } from '../../../global/endPoint';
import { environment } from '../../../../environments/environment';
import { SwtAseguradoDto } from '../model/swt-asegurado.dto';


@Injectable({
  providedIn: 'root'
})
export class TrabajadoresService {

  constructor(
    private httpClient: HttpClient,
    private router: Router ) { }

    public listPaginatedTrabajadores(swtAseguradoDto:SwtAseguradoDto): Observable<any> {
      return this.httpClient.post<any>(environment.autodeterminacionesApiUrl + EPs.autodeterminaciones.listPaginatedTrabajadores, swtAseguradoDto);
    }

  

}
