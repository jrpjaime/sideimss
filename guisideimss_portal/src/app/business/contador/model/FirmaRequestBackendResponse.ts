export interface FirmaRequestBackendResponse {
  cad_original: string;
  peticionJSON: string;
  fechaParaAcuse: string;
  error: boolean;
  mensaje?: string; // Opcional, para mensajes de éxito/error
}
