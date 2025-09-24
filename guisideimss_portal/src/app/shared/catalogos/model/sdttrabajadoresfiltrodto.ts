export class  SdtTrabajadoresFiltroDto  {
	page: number = 0;
	size: number = 10;
	order: string = '';
	asc: boolean = true;
	idTrabajador?: string;
	cveNss?: string;
	nomNombre?: string;
	nomApellidoPaterno?: string;
	nomApellidoMaterno?: string;
	idJornada?: number;
	idTipoPension?: number;
	fecInicio?: string;
	fecFin?: string;

	idSolicitudDevolucion?: string;
  numSalarioDiarioInte?: string;
	desJornada?: string;
	desTipoPension?: string;

}
