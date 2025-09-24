import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map } from 'rxjs';
import { ContextoPatronalService } from '../services/contexto-patronal.service';

export const SeleccionPatronalGuard: CanActivateFn = (route, state) => {
  const contextoService = inject(ContextoPatronalService);
  const router = inject(Router);

  return contextoService.registroActual$.pipe(
    map(registro => {
      // --- LOG DE DEPURACIÓN  ---
      console.log('[SeleccionPatronalGuard] Verificando acceso. Valor recibido:', registro);

       
      // Comprueba no solo si el objeto existe, sino si tiene un RP válido dentro.
      if (registro && registro.registroPatronal) {
        console.log('[SeleccionPatronalGuard] Acceso PERMITIDO.');
        return true;
      }
      
      console.error('[SeleccionPatronalGuard] Acceso DENEGADO. Redirigiendo a /home.');
      return router.createUrlTree(['/home']);
    })
  );
};