export class  SdcMotivoDocumentoFiltroDto  { 
	page: number = 0;
	size: number = 10;
	order: string = '';
	asc: boolean = true;
	idMotivoDocumento?: number;
	idMotivoDevolucion?: number;
	idTipoDocumento?: number;

}
