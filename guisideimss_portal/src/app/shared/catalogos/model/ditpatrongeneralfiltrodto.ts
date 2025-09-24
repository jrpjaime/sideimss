export class  DitPatronGeneralFiltroDto  {
	page: number = 0;
	size: number = 10;
	order: string = '';
	asc: boolean = true;
	idPatronGeneral?: number;
	denominacionRazonSocial?: string;
	rfc?: string;
	registroPatronal?: string;
	cveDelegacion?: string;
	desDelegacion?: string;
	cveSubdelegacion?: string;
	desSubdelegacion?: string;

}


