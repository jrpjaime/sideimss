import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { EPs } from '../../../global/endPoint';
import { environment } from '../../../../environments/environment';
import { DocumentoIndividualResponseDto } from '../model/DocumentoIndividualResponseDto ';
import { PlantillaDatoDto } from '../model/PlantillaDatoDto';
import { AcuseConfig } from '../model/AcuseConfig ';
import { AcuseParameters } from '../model/AcuseParameters';
import { FirmaRequestFrontendDto } from '../model/FirmaRequestFrontendDto';
import { FirmaRequestBackendResponse } from '../model/FirmaRequestBackendResponse';




@Injectable({
  providedIn: 'root'
})
export class AcreditacionMembresiaService {

  constructor(
    private httpClient: HttpClient,
    private router: Router ) { }

  uploadDocument(formData: FormData): Observable<DocumentoIndividualResponseDto> {
    return this.httpClient.post<DocumentoIndividualResponseDto>(
      environment.documentosApiUrl + EPs.documentos.cargarDocumento, // Usa la URL base de documentos y el endpoint específico
      formData
    );
  }

  downloadDocument(fullHdfsPathBase64: string): Observable<HttpResponse<Blob>> {
    const url = `${environment.documentosApiUrl}${EPs.documentos.descargarDocumento}`;
    return this.httpClient.get(url, {
      params: { fullHdfsPathBase64: fullHdfsPathBase64 },
      observe: 'response', // Para obtener la respuesta completa con encabezados
      responseType: 'blob' // Para que la respuesta sea un objeto Blob
    });
  }


  deleteDocument(fullHdfsPathBase64: string): Observable<void> {
    const url = `${environment.documentosApiUrl}${EPs.documentos.eliminarDocumento}`;
    return this.httpClient.delete<void>(url, {
      params: { fullHdfsPathBase64: fullHdfsPathBase64 }
    });
  }


    acreditacionmembresia(submitDto: any): Observable<any> {

    return this.httpClient.post<any>(environment.contadoresApiUrl + EPs.contadores.acreditacionmembresia, submitDto);
  }


  // método para obtener la configuración del acuse
  getAcuseConfig(tipoAcuse: string): Observable<AcuseParameters> {
    const url = `${environment.acusesApiUrl}${EPs.acuses.getAcuseConfig}`;
    let params = new HttpParams().set('tipoAcuse', tipoAcuse);

    return this.httpClient.get<AcuseParameters>(url, { params: params });
  }


  // método para descargar el preview del acuse
  descargarAcusePreview(plantillaDatoDto: PlantillaDatoDto): Observable<HttpResponse<Blob>> {
    const url = `${environment.acusesApiUrl}${EPs.acuses.descargarAcusePreview}`;
    return this.httpClient.post(url, plantillaDatoDto, {
      observe: 'response',
      responseType: 'blob'
    });
  }


  generarRequestJsonFirma(requestDto: FirmaRequestFrontendDto): Observable<FirmaRequestBackendResponse> {
    const url = `${environment.acusesApiUrl}${EPs.acuses.generaRequestJSONFirmaAcuse}`;
    // El requestBody ahora es directamente el DTO que recibes
    const requestBody = requestDto;
    return this.httpClient.post<FirmaRequestBackendResponse>(url, requestBody);
  }



    getAcuseParaVisualizar(urlDocumento: string): Observable<HttpResponse<Blob>> {
    const url = `${environment.acusesApiUrl}${EPs.acuses.descargarAcuse}`; // Usa el endpoint POST
    const requestBody = { urlDocumento: urlDocumento };
    return this.httpClient.post(url, requestBody, {
      observe: 'response',
      responseType: 'blob' // Esperamos un Blob (el PDF)
    });
  }


}
