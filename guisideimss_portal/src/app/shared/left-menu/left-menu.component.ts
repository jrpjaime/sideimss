import { Component, EventEmitter, Output, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ModalService } from '../services/modal.service';

import { Subscription } from 'rxjs'; // Para gestionar las suscripciones
import { Constants } from '../../global/Constants';
import { SharedService } from '../services/shared.service';

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
        { name: 'Presentación de acreditación y menbresía', icon: 'bi bi-file-text-fill', route: '/contador/acreditacionymembresia' },
        { name: 'Modificación de datos', icon: 'bi bi-arrow-repeat', route: '/contador/modificaciondatos' },
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }
      ]
    },
    {
      name: 'Dictamen electrónico', icon: 'bi bi-people-fill', isExpanded: false,
      roles: [Constants.rolePatron, Constants.roleRepresentante], // Visible para Patron o Representante
      children: [
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }, // OJO: Las rutas están repetidas, probablemente sean diferentes en el futuro.
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }
      ]
    },
    {
      name: 'Consulta al dictamen', icon: 'bi bi-cloud-upload-fill', isExpanded: false,
      roles: [Constants.rolePatron], // Solo visible para el rol Patron
      children: [
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }, // OJO: Las rutas están repetidas, probablemente sean diferentes en el futuro.
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }
      ]
    }
  ];

  // Este será el array de menú que se renderizará, filtrado por roles
  menuItems: MenuItem[] = [];
  private rolesSubscription!: Subscription; // Para gestionar la desuscripción

  constructor(
    private modalService: ModalService,
    private router: Router,
    private sharedService: SharedService // Inyectamos SharedService
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

    if (item.route) {
      event.preventDefault();
      setTimeout(() => {
        this.router.navigate([item.route!]);
      }, 0);
    }
  }
}
