export class  SdcMotivoDocumentoDto  { 
	idMotivoDocumento?: number;
	idMotivoDevolucion?: number;
	idTipoDocumento?: number;
	
	cveTipoDocumento?: string;
	desTipoDocumento?: string;

	cveMotivoDevolucion?: string;
	desMotivoDevolucion?: string;



	codigo?: number;
	mensaje?: string;

	idSolicitudDevolucion?: string;
	
	nombreArchivo?: string;
    tipoArchivo?: string;
    tamanioArchivo?: string;
 
	refTipoDocumento?: string;
	
	idDocumentoD1?: string;
	idMotivoDocumentoD1?: string;
	refNombreDocumentoD1?: string;
	fecRegistroAltaD1?: string;

	idDocumentoD2?: string;
	idMotivoDocumentoD2?: string;
	refNombreDocumentoD2?: string;
	fecRegistroAltaD2?: string;


	idDocumento?: string;

}
