import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';

export const terminosAceptadosGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();
  if (!token) {
    return true; 
  }

  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    
    if (payload.terminosAceptados) {
      // Si ya aceptó, perfecto, puede continuar a cualquier ruta dentro del layout.
      return true;
    } else {
      // =================================================================
      // Si NO ha aceptado, se redirige a la página de aceptación sin menú.
      // =================================================================
      return router.createUrlTree(['/aceptacion-terminos']);
    }
  } catch (e) {
    return router.createUrlTree(['/login']);
  }
};
