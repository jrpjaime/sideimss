import { Component, EventEmitter, Output, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { catchError, of, Subscription, switchMap } from 'rxjs'; // Para gestionar las suscripciones
import { Constants } from '../../global/Constants';
import { SharedService } from '../services/shared.service';
import { ContadorPublicoAutorizadoService } from '../../business/contador/services/contador-publico-autorizado.service';
import { AlertService } from '../services/alert.service';
import { LoaderService } from '../services/loader.service';
import { SolicitudBajaDataService } from '../../business/contador/services/solicitud-baja-data.service';

// Definimos una interfaz para nuestros elementos de menú para tener un código más limpio
export interface MenuItem {
  name: string;
  icon: string; // Usaremos nombres de clase para los iconos
  route?: string; // Ruta para la navegación
  isExpanded?: boolean; // Para controlar si el submenú está abierto
  children?: MenuItem[]; // Para los subniveles
  action?: 'limpiarContexto';
  roles?: string[]; // Para especificar qué roles pueden ver este elemento
}

@Component({
  selector: 'app-left-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.css']
})
export class LeftMenuComponent implements OnInit, OnDestroy { // Implementamos OnInit y OnDestroy

  @Output() toggleMenuClicked = new EventEmitter<void>();

  // Definimos la estructura completa del menú con sus roles asociados
  private fullMenuItems: MenuItem[] = [
    {
      name: 'Contador', icon: 'bi bi-building-fill', isExpanded: false,
      roles: [Constants.roleContador], // Solo visible para el rol Contador
      children: [
        { name: 'Presentación de acreditación y membresía', icon: 'bi bi-file-text-fill', route: '/contador/acreditacionymembresia' },
        { name: 'Modificación de datos', icon: 'bi bi-arrow-repeat', route: '/contador/modificaciondatos' },
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }
      ]
    }
    /*,
    {
      name: 'Dictamen electrónico', icon: 'bi bi-people-fill', isExpanded: false,
      roles: [Constants.rolePatron, Constants.roleRepresentante], // Visible para Patron o Representante
      children: [
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' },
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }
      ]
    } */
  ];

  // Este será el array de menú que se renderizará, filtrado por roles
  menuItems: MenuItem[] = [];
  private rolesSubscription!: Subscription; // Para gestionar la desuscripción

  constructor(
    private router: Router,
    private sharedService: SharedService, // Inyectamos SharedService
    private contadorService: ContadorPublicoAutorizadoService,
    private alertService: AlertService,
    private loaderService: LoaderService,
    private solicitudBajaDataService: SolicitudBajaDataService
  ) { }

  ngOnInit(): void {
    // Nos suscribimos a los cambios de roles del usuario
    this.rolesSubscription = this.sharedService.currentRoleSesion.subscribe(userRoles => {
      console.log('LeftMenuComponent - Roles del usuario recibidos:', userRoles);
      this.filterMenuItems(userRoles);
    });
  }

  ngOnDestroy(): void {
    // Es importante desuscribirse para evitar fugas de memoria
    if (this.rolesSubscription) {
      this.rolesSubscription.unsubscribe();
    }
  }

  // Método para filtrar los elementos del menú basados en los roles del usuario
  private filterMenuItems(userRoles: string[]): void {
    if (!userRoles || userRoles.length === 0) {
      this.menuItems = []; // Si no hay roles, no mostrar nada
      return;
    }

    this.menuItems = this.fullMenuItems.filter(item => {
      // Si el item del menú no tiene roles definidos, es visible por defecto
      if (!item.roles) {
        return true;
      }
      // Verificar si alguno de los roles del usuario coincide con los roles requeridos para el item del menú
      return item.roles.some(requiredRole => userRoles.includes(requiredRole));
    });
    console.log('LeftMenuComponent - Menú filtrado:', this.menuItems);
  }

  onToggleMenu(): void {
    this.toggleMenuClicked.emit();
  }

  toggleSubmenu(item: MenuItem): void {
    const isOpening = !item.isExpanded;
    // Cierra todos los submenús antes de abrir el seleccionado
    this.menuItems.forEach(i => { if (i.children) { i.isExpanded = false; } });
    if (isOpening) {
      item.isExpanded = true;
    }
  }

  onItemClick(event: MouseEvent, item: MenuItem): void {
    if (item.action) {
      event.preventDefault();
      if (item.action === 'limpiarContexto') {
        this.router.navigate(['/home']);
      }
      return;
    }

    if (item.children) {
      event.preventDefault();
      this.toggleSubmenu(item);
      return;
    }


          // SOLICITUD DE BAJA
      if (item.route === '/contador/solicitudbaja') {
        this.procesarSolicitudBaja(item.route);
        return; // Salimos para no ejecutar el navigate de abajo inmediatamente
      }



    if (item.route) {
      event.preventDefault();
      console.log("item.route: "+ item.route);
      // Detectar clic en Modificación de Datos ---
      if (item.route === '/contador/modificaciondatos') {
        // Disparamos la señal para limpiar el formulario
        this.sharedService.triggerResetModificacionDatos();
      }
      setTimeout(() => {
        this.router.navigate([item.route!]);
      }, 0);
    }
  }




  /**
   * Método auxiliar para manejar la validación de solicitud de baja
   */
  private procesarSolicitudBaja(route: string): void {

    this.solicitudBajaDataService.clearSolicitudBajaData();
    this.solicitudBajaDataService.clearDatosParaRegresar();
    this.sharedService.triggerResetSolicitudBaja();

    this.loaderService.show();
    this.alertService.clear();

    // Paso 1: Obtener datos del contador para sacar el registroIMSS o CPA

    this.contadorService.getDatosContador().pipe(
      switchMap(datos => {
        // Supongamos que necesitamos pasar el registro como parametro, parseamos lo que venga
        const numRegistro = datos.datosPersonalesDto.registroIMSS ? Number(datos.datosPersonalesDto.registroIMSS) : 0;

        // Paso 2: Validar si tiene dictamen
        return this.contadorService.validarDictamenEnProceso(numRegistro);
      }),
      catchError(err => {
        console.error("Error validando dictamen", err);
        // Retornamos un observable con false para no bloquear si falla el servicio (o manejar error)
        return of({ tieneDictamen: false });
      })
    ).subscribe({
      next: (response) => {
        this.loaderService.hide();

        if (response.tieneDictamen) {
          // TIENE DICTAMEN: MANTENER AL USUARIO AQUI Y MOSTRAR ERROR
         //this.alertService.error('No es posible iniciar su trámite, tiene un dictamen en proceso. Favor de concluir con la presentación respectiva.', { autoClose: true, keepAfterRouteChange: false } );
        } else {
          // NO TIENE DICTAMEN: DEJAR PASAR
          this.router.navigate([route]);
        }
      },
      error: (err) => {
        this.loaderService.hide();
        // En caso de error técnico mostrar error genérico.

        this.alertService.error('Ocurrió un error al validar el estado del contador.', { autoClose: true });
      }
    });
  }

}
