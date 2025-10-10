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
    listRegistrosPatronales: "/v1/listRegistrosPatronales",
    listPaginatedRegistrosPatronales: "/v1/listPaginatedRegistrosPatronales",

    listMotivoDevolucion: "/v1/listMotivoDevolucion",
    listMotivoDevolucionFiltro: "/v1/listMotivoDevolucionFiltro",
    listEstatusSolicitud: "/v1/listEstatusSolicitud",
    listTipoSeguro: "/v1/listTipoSeguro",
    listTipoCuota: "/v1/listTipoCuota",
    listTipoDocumento: "/v1/listTipoDocumento",
    listEntidadRecaudadora: "/v1/listEntidadRecaudadora",
    listMotivoDevolucionRole: "/v1/listMotivoDevolucionRole",
    listPeriodoFiltro: "/v1/listPeriodoFiltro",
    listCiclo: "/v1/listCiclo",
    listTipoPension: "/v1/listTipoPension",
    listJornada: "/v1/listJornada",
    listMovimiento: "/v1/listMovimiento",

  },

    // ********** CONTADORES /mssideimss-contadores**********
    contadores: {
      info: "/v1/info",
      list: "/v1/list",
      acreditacionmembresia: "/v1/acreditacionMembresia",

    },

    documentos: {
      info: "/v1/info",
      list: "/v1/list",
      descargarDocumento: "/v1/descargarDocumento",
      eliminarDocumento: "/v1/eliminarDocumento",

      cargarDocumento: "/v1/cargarDocumento",
    }



}
