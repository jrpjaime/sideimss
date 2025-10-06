import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { EPs } from '../../../../global/endPoint';
import { environment } from '../../../../../environments/environment';
 

@Injectable({
  providedIn: 'root'
})
export class AcreditacionMembresiaService {

  constructor(
    private httpClient: HttpClient,
    private router: Router ) { } 

  enviarAcreditacionMembresia(formData: FormData): Observable<any> {
    return this.httpClient.post<any>(environment.contadoresApiUrl + EPs.contadores.acreditacionmembresia, formData);
  }
  
}
