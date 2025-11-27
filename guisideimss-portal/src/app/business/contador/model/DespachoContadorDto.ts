export interface DespachoContadorDto {
  rfcDespacho: string;
  nombreRazonSocial: string;
  cveIdTipoSociedad: string;
  desTipoSociedad: string;
  cveIdCargoContador: string;
  desCargoContador: string;
  telefonoFijo: string;
  tieneTrabajadores?: string;
  numeroTrabajadores?: string; 
}