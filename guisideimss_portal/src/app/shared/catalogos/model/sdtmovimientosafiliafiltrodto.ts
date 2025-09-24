export class  SdtMovimientosAfiliaFiltroDto  {
	page: number = 0;
	size: number = 10;
	order: string = '';
	asc: boolean = true;
	idMovimientosAfilia?:  string | null;
	refTipoMovimiento?: string;
	fecInicio?: Date;
	numDias?: number;
	fecFin?: Date;
	refProcedente?: string;
	refIndicadorRiss?: string;
	numSalarioDiarioInte?: number;
	folioIncapacidad?: string;
	indArt33?: number;
	idTrabajador?: string;
	fecRegistroAlta?: Date;
	fecRegistroActualizado?: Date;
	fecRegistroBaja?: Date;
	cveRegistroPatronalVigente?: string;
	idMovimiento?: number;
	indRiss?: number;
	idJornada?: number;
  numPorcentajeDescuento?: number;

}
