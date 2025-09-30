
import { Component, EventEmitter, Output } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ModalService } from '../services/modal.service';


// Definimos una interfaz para nuestros elementos de menú para tener un código más limpio
export interface MenuItem {
  name: string;
  icon: string; // Usaremos nombres de clase para los iconos
  route?: string; // Ruta para la navegación
  isExpanded?: boolean; // Para controlar si el submenú está abierto
  children?: MenuItem[]; // Para los subniveles
  action?: 'limpiarContexto';
}

@Component({
  selector: 'app-left-menu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './left-menu.component.html',
  styleUrls: ['./left-menu.component.css']
})
export class LeftMenuComponent {

  @Output() toggleMenuClicked = new EventEmitter<void>();

  // Aquí definimos toda la estructura del menú
  menuItems: MenuItem[] = [
    { name: 'Contador', icon: 'bi bi-building-fill', isExpanded: false, children: [
        { name: 'Presentación de acreditación y menbresía', icon: 'bi bi-file-text-fill', route: '/contador/acreditacionymembresia' },
        { name: 'Modificación de datos', icon: 'bi bi-arrow-repeat', route: '/contador/modificaciondatos' },
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }
    ]},
    { name: 'Dictamen electrónico', icon: 'bi bi-people-fill', isExpanded: false, children: [
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' },
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }
    ]},
    { name: 'Consulta al dictamen', icon: 'bi bi-cloud-upload-fill', isExpanded: false, children: [
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' },
        { name: 'Solicitud de baja', icon: 'bi bi-dot', route: '/contador/solicitudbaja' }
    ]}
  ];

  constructor(
    private modalService: ModalService,
    private router: Router
  ) { }

  // Este método se llama cuando se hace clic en el botón principal de toggle
  onToggleMenu(): void {
    this.toggleMenuClicked.emit();
  }

  // Este método maneja la apertura y cierre de los submenús




  // Este método se mantiene igual
  toggleSubmenu(item: MenuItem): void {
    const isOpening = !item.isExpanded;
    this.menuItems.forEach(i => { if (i.children) { i.isExpanded = false; } });
    if (isOpening) {
      item.isExpanded = true;
    }
  }

  // --- 4. MANEJADOR DE CLICS CENTRAL ---
onItemClick(event: MouseEvent, item: MenuItem): void {
  // Caso 1: Es un elemento con una acción especial (ej. "Cambiar RP")
  if (item.action) {
    event.preventDefault(); // Detenemos cualquier navegación
    if (item.action === 'limpiarContexto') {

        this.router.navigate(['/home']);

    }
    return; // Detenemos la ejecución aquí
  }

  // Caso 2: Es un menú padre que se despliega (tiene hijos)
  if (item.children) {
    event.preventDefault(); // Detenemos cualquier navegación
    this.toggleSubmenu(item); // Solo abrimos/cerramos el submenú
    return; // ¡IMPORTANTE! Detenemos la ejecución aquí
  }

  // Caso 3: Es un enlace de navegación normal (no tiene acción ni hijos)
  if (item.route) {
    event.preventDefault(); // Detenemos la navegación por defecto de [routerLink]

    // Usamos setTimeout para que la navegación ocurra en el siguiente "tick",
    // evitando la colisión con el ciclo de detección de cambios actual.
    setTimeout(() => {
      this.router.navigate([item.route!]);
    }, 0);
  }
}







}
