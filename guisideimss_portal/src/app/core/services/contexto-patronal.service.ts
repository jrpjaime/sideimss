import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface RegistroPatronal {
  registroPatronal?: string;
  nombre?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ContextoPatronalService {

  private readonly CONTEXTO_STORAGE_KEY = 'contexto_registro_patronal';

  private registroSeleccionadoSource = new BehaviorSubject<RegistroPatronal | null>(null);
  public registroActual$: Observable<RegistroPatronal | null> = this.registroSeleccionadoSource.asObservable();

  constructor() {
    // ---- LOG DE DEPURACIÓN 1 ----
    console.log('[ContextoService] El servicio ha sido creado. Intentando cargar desde sessionStorage...');
    this.cargarDesdeSessionStorage();
  }

  seleccionarRegistro(registro: RegistroPatronal | null): void {
    console.log('[ContextoService] seleccionarRegistro llamado con:', registro);
    this.registroSeleccionadoSource.next(registro);
    this.guardarEnSessionStorage(registro);
  }

  limpiarRegistro(): void {
    // ---- LOG DE DEPURACIÓN 2 (¡MUY IMPORTANTE!) ----
    console.error('[ContextoService] ¡ALERTA! El método limpiarRegistro() ha sido llamado.');
    // La pila de llamadas en la consola te dirá QUIÉN lo llamó.
    this.seleccionarRegistro(null);
  }

  get valorActual(): RegistroPatronal | null {
    return this.registroSeleccionadoSource.value;
  }

  private guardarEnSessionStorage(registro: RegistroPatronal | null): void {
    if (registro) {
      sessionStorage.setItem(this.CONTEXTO_STORAGE_KEY, JSON.stringify(registro));
    } else {
      sessionStorage.removeItem(this.CONTEXTO_STORAGE_KEY);
    }
  }

  private cargarDesdeSessionStorage(): void {
    const registroGuardadoString = sessionStorage.getItem(this.CONTEXTO_STORAGE_KEY);
    
    // ---- LOG DE DEPURACIÓN 3 ----
    console.log(`[ContextoService] Se encontró esto en sessionStorage: ${registroGuardadoString}`);

    if (registroGuardadoString) {
      try {
        const registroGuardado: RegistroPatronal = JSON.parse(registroGuardadoString);
        this.registroSeleccionadoSource.next(registroGuardado);
        
        // ---- LOG DE DEPURACIÓN 4 ----
        console.log('[ContextoService] ÉXITO: Contexto restaurado y emitido.', registroGuardado);

      } catch (error) {
        console.error('[ContextoService] FRACASO: Error al parsear JSON de sessionStorage.', error);
        sessionStorage.removeItem(this.CONTEXTO_STORAGE_KEY);
      }
    } else {
        console.log('[ContextoService] No se encontró contexto para restaurar.');
    }
  }
}