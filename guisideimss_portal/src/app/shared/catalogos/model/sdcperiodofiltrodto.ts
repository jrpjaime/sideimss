export class  SdcPeriodoFiltroDto  {
	page: number = 0;
	size: number = 10;
	order: string = '';
	asc: boolean = true;
	idPeriodo?: number;
	desPeriodo?: string;
	cvePeriodo?: string;
	indPeriodo?: number;
  idTipoSeguro?: number;
  indRiss?: number;

}
