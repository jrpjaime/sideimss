export class  SdcMotivoDevolucionFiltroDto  { 
	page: number = 0;
	size: number = 10;
	order: string = '';
	asc: boolean = true;
	idMotivoDevolucion?: number;
	desMotivoDevolucion?: string;
	role?: string;

}
