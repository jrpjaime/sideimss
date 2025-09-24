 
import { Component, EventEmitter, Output } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { ContextoPatronalService } from '../../core/services/contexto-patronal.service';
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
    { name: 'Patrones', icon: 'bi bi-building-fill', isExpanded: false, children: [
        { name: 'Términos y Condiciones', icon: 'bi bi-file-text-fill', route: '/terminos-y-condiciones' },
        { name: 'Cambiar Registro Patronal', icon: 'bi bi-arrow-repeat', action: 'limpiarContexto' },
        { name: 'Registro Patrón', icon: 'bi bi-dot', route: '/patrones/registro' },
        { name: 'Prima de Riesgo de Trabajo', icon: 'bi bi-dot', route: '/patrones/prima-rt' }
    ]},
    { name: 'Trabajadores', icon: 'bi bi-people-fill', isExpanded: false, children: [
        { name: 'Trabajadores', icon: 'bi bi-grid-1x2-fill', route: '/trabajadores/tablero' },
        { name: 'Datos Afiliatorios', icon: 'bi bi-dot', route: '/trabajadores/datos-afiliatorios' }
    ]},
    { name: 'Carga de datos por archivo', icon: 'bi bi-cloud-upload-fill', isExpanded: false, children: [
        { name: 'Trabajadores (aseg.txt)', icon: 'bi bi-dot', route: '/carga/trabajadores' },
        { name: 'Datos Afiliatorios (afil.txt)', icon: 'bi bi-dot', route: '/carga/afiliatorios' }
    ]},
    { name: 'Movimientos', icon: 'bi bi-file-ruled-fill', isExpanded: false, children: [
        { name: 'Movimientos Afiliatorios', icon: 'bi bi-dot', route: '/movimientos/afiliatorios' },
        { name: 'Movimientos de Créditos', icon: 'bi bi-dot', route: '/movimientos/creditos' },
        { name: 'Movimientos Incapacidades', icon: 'bi bi-dot', route: '/movimientos/incapacidades' },
        { name: 'Movimientos Ausentismo', icon: 'bi bi-dot', route: '/movimientos/ausentismo' },
        { name: 'Carga de Crédito (cred.txt)', icon: 'bi bi-dot', route: '/movimientos/carga-credito' },
        { name: 'Carga de Incapacidades (inca.txt)', icon: 'bi bi-dot', route: '/movimientos/carga-incapacidades' }
    ]},
    { name: 'Aportaciones', icon: 'bi bi-piggy-bank-fill', isExpanded: false, children: [
        { name: 'Voluntarias mov9', icon: 'bi bi-dot', route: '/aportaciones/voluntarias' },
        { name: 'Complementaria mov10', icon: 'bi bi-dot', route: '/aportaciones/complementaria' }
    ]},
    // Para los elementos sin submenú, podemos usar iconos más específicos
    { name: 'Registro de Obra', icon: 'bi bi-tools', route: '/registro-obra' },
    { name: 'Determinación de Prima de RT', icon: 'bi bi-postcard-fill', route: '/determinacion-prima-rt' },
    { name: 'Reportes', icon: 'bi bi-clipboard2-data-fill', route: '/reportes' },
    { name: 'Confronta', icon: 'bi bi-check2-circle-fill', route: '/confronta' }
  ];

  constructor(
    private contextoService: ContextoPatronalService,
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
      if (this.contextoService.valorActual) {
        this.solicitarConfirmacionParaCambiar();
      } else {
        this.router.navigate(['/home']);
      }
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



  private solicitarConfirmacionParaCambiar(): void {
    // Así llamas al nuevo diálogo configurable
    this.modalService.showDialog(
      'confirm', // Tipo: Dos botones (Confirmar/Cancelar)
      'info',    // Estilo: 'info' para el look dorado
      'Información', // Título
      '¿Está seguro de que quiere cambiar de Registro Patronal?', // Mensaje
      (confirmado: boolean) => {
        if (confirmado) {
          this.contextoService.limpiarRegistro();
          this.router.navigate(['/home']);
        }
      },
      'Continuar', // Texto del botón de confirmación
      'Cancelar'   // Texto del botón de rechazo
    );
  }


   private mostrarError(): void {
    this.modalService.showDialog(
      'alert', // Tipo: Un solo botón
      'error', // Estilo: 'error' para el look rojo
      'Error', // Título
      'Existen 000 de trabajadores con error.<br>A continuación se muestran los motivos de rechazo por trabajador.', // Mensaje con salto de línea
      (confirmado: boolean) => {
        // Lógica a ejecutar después de que el usuario presione 'Aceptar'
        console.log('Diálogo de error cerrado');
      },
      'Aceptar' // Texto del único botón
      // No se necesita el texto de rechazo aquí
    );
  }

}