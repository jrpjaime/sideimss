import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { SolicitudBajaDto } from '../model/SolicitudBajaDto';
import { environment } from '../../../../environments/environment';
import { EPs } from '../../../global/endPoint';
import { PlantillaDatoDto } from '../model/PlantillaDatoDto';
import { AcuseParameters } from '../model/AcuseParameters';
import { FirmaRequestFrontendDto } from '../model/FirmaRequestFrontendDto';
import { FirmaRequestBackendResponse } from '../model/FirmaRequestBackendResponse';

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



    /**
     * Envía la solicitud de baja final con la firma electrónica.
     * @param submitDto Datos de la solicitud de baja incluyendo la firma.
     * @returns Un Observable con la respuesta del backend.
     */
    solicitudBaja(submitDto: PlantillaDatoDto): Observable<any> {
      // Apunta al nuevo endpoint de solicitudBaja en el controlador
      return this.httpClient.post<any>(environment.contadoresApiUrl + EPs.contadores.solicitudBaja, submitDto);
    }
    /**
     * Obtiene la configuración de la plantilla de acuse para un tipo específico.
     * @param tipoAcuse El tipo de acuse (ej. 'SOLICITUD_BAJA').
     * @returns Un Observable con los parámetros de configuración del acuse.
     */
    getAcuseConfig(tipoAcuse: string): Observable<AcuseParameters> {
      const url = `${environment.acusesApiUrl}${EPs.acuses.getAcuseConfig}`;
      let params = new HttpParams().set('tipoAcuse', tipoAcuse);
      return this.httpClient.get<AcuseParameters>(url, { params: params });
    }

    /**
     * Descarga la previsualización del acuse en formato PDF.
     * @param plantillaDatoDto Objeto con los datos para rellenar la plantilla.
     * @returns Un Observable con la respuesta HTTP que contiene el Blob del PDF.
     */
    descargarAcusePreview(plantillaDatoDto: PlantillaDatoDto): Observable<HttpResponse<Blob>> {
      const url = `${environment.acusesApiUrl}${EPs.acuses.descargarAcusePreview}`;
      return this.httpClient.post(url, plantillaDatoDto, {
        observe: 'response',
        responseType: 'blob'
      });
    }

    /**
     * Genera el JSON de petición para el widget de firma electrónica.
     * @param requestDto Datos necesarios para la generación de la cadena original.
     * @returns Un Observable con la respuesta del backend que incluye la cadena original y el JSON de petición.
     */
    generarRequestJsonFirma(requestDto: FirmaRequestFrontendDto): Observable<FirmaRequestBackendResponse> {
      const url = `${environment.acusesApiUrl}${EPs.acuses.generaRequestJSONFirmaAcuse}`;
      return this.httpClient.post<FirmaRequestBackendResponse>(url, requestDto);
    }

    /**
     * Obtiene el acuse final en formato PDF para visualización después de la firma.
     * @param urlDocumento La URL del documento a descargar (obtenida de la respuesta final del backend).
     * @returns Un Observable con la respuesta HTTP que contiene el Blob del PDF.
     */
    getAcuseParaVisualizar(urlDocumento: string): Observable<HttpResponse<Blob>> {
      const url = `${environment.acusesApiUrl}${EPs.acuses.descargarAcuse}`;
      const requestBody = { urlDocumento: urlDocumento };
      return this.httpClient.post(url, requestBody, {
        observe: 'response',
        responseType: 'blob'
      });
    }

    /**
     * Obtiene un nuevo folio de solicitud.
     * @returns Un Observable con el folio generado como string.
     */
    getNuevoFolioSolicitud(): Observable<string> {
      const url = `${environment.catalogosApiUrl}${EPs.catalogo.getNuevoFolioSolicitud}`;
      return this.httpClient.get(url, { responseType: 'text' });
    }
}
