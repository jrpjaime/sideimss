export interface PlantillaDatoDto {
  cveIdPlantillaDatos: number | null;  
  nomDocumento: string;
  desVersion: string; // Corresponde a desVersion de PlantillaDato
  datosJson: string; // Aquí se pasará la cadena JSON
  tipoAcuse: string;
}