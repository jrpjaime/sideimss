export class  SdcMovimientoFiltroDto  { 
	page: number = 0;
	size: number = 10;
	order: string = '';
	asc: boolean = true;
	idMovimiento?: number;
	cveMovimiento?: string;
	desMovimiento?: string;

}
