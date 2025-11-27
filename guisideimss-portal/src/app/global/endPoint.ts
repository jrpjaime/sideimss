export const EPs = {

  // ********** JWT ******************
  oauth: {
    login: "/v1/login",
    refresh: "/v1/refresh",
    aceptarTerminos: "/v1/aceptar-terminos",
  },




  // ********** CATALOGOS /mssideimss-catalogos**********
  catalogo: {
    info: "/v1/info",
    list: "/v1/list",
    getNuevoFolioSolicitud: "/v1/getNuevoFolioSolicitud",
    tiposDatosContador: '/v1/tiposDatosContador',
    datoRfc: '/v1/datoRfc',
    tiposSociedadFormaParte: '/v1/tiposSociedadFormaParte',
    cargosContador: '/v1/cargosContador',
    consultarDatosDespacho: '/v1/consultarDatosDespacho',

  },

  // ********** CONTADORES /mssideimss-contadores**********
  contadores: {
    info: "/v1/info",
    list: "/v1/list",
    acreditacionmembresia: "/v1/acreditacionMembresia",
    consultaDatosContador: "/v1/consultaDatosContador",
    solicitudBaja: '/v1/solicitudBaja',
    colegioContador: '/v1/colegioContador',
    guardarModificacionDatos: '/v1/guardarModificacionDatos',


  },

  // ********** DOCUMENTOS /mssideimss-documentos**********
  documentos: {
    info: "/v1/info",
    list: "/v1/list",
    descargarDocumento: "/v1/descargarDocumento",
    eliminarDocumento: "/v1/eliminarDocumento",

    cargarDocumento: "/v1/cargarDocumento",
  },

  // ********** ACUSES /mssideimss-acuses**********
  acuses: {
    info: "/v1/info",
    list: "/v1/list",
    getAcuseConfig: "/v1/getAcuseConfig",
    descargarAcuse: '/v1/descargarAcuse',
    descargarAcusePreview: "/v1/descargarAcusePreview",
    generaRequestJSONFirmaAcuse:"/v1/generaRequestJSONFirmaAcuse",
  }



}
