import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';
import { Constants } from '../../global/Constants';

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  private tokenKey = Constants.tokenKey;

  //Variables para el RFC con el que se esta trabajando
  private rfcSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentRfc = this.rfcSource.asObservable();

  private curpSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentCurp = this.curpSource.asObservable();

  private nombreSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentNombre = this.nombreSource.asObservable();

  private primerApellidoSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentPrimerApellido= this.primerApellidoSource.asObservable();

  private segundoApellidoSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentSegundoApellido= this.segundoApellidoSource.asObservable();


  private roleSource = new BehaviorSubject<string[]>([]); // Valor por defecto: array vacío
  currentRole = this.roleSource.asObservable(); // Observador de string[]

  private registroPatronalSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentRegistroPatronal = this.registroPatronalSource.asObservable();

  private numeroRegistroImssSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentNumeroRegistroImssSesion = this.numeroRegistroImssSesionSource.asObservable();





  //Variables para el usuario autenticado
  private rfcSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentRfcSesion = this.rfcSesionSource.asObservable();

  private curpSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentCurpSesion = this.curpSesionSource.asObservable();

  private nombreSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentNombreSesion = this.nombreSesionSource.asObservable();

  private primerApellidoSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentPrimerApellidoSesion= this.primerApellidoSesionSource.asObservable();

  private segundoApellidoSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentSegundoApellidoSesion= this.segundoApellidoSesionSource.asObservable();


  private roleSesionSource = new BehaviorSubject<string[]>([]); // Valor por defecto: array vacío
  currentRoleSesion = this.roleSesionSource.asObservable(); // Observador de string[]


  private delegacionSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentDelegacionSesion = this.delegacionSesionSource.asObservable();


  private subdelegacionSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentSubdelegacionSesion = this.subdelegacionSesionSource.asObservable();




  constructor() {}

  changeRfc(rfc: string) {
    this.rfcSource.next(rfc); // Cambia el valor del RFC
  }

  changeCurp(curp: string) {
    this.curpSource.next(curp); // Cambia el valor del RFC
  }

  changeNombre(nombre: string) {
    this.nombreSource.next(nombre); // Cambia el valor del Nombre
  }

  changePrimerApellido(primerApellido: string) {
    this.primerApellidoSource.next(primerApellido); // Cambia el valor del PrimerApellido
  }

  changeSegundoApellido(segundoApellido: string) {
    this.segundoApellidoSource.next(segundoApellido); // Cambia el valor del SegundoApellido
  }

 changeRole(roles: string[]) {
   this.roleSource.next(roles); // Cambia el valor del Role
 }

  changeRegistroPatronal(registroPatronal: string) {
    this.registroPatronalSource.next(registroPatronal); // Cambia el valor del registro patronal
  }







// Variables para el usuario autenticado
  changeRfcSesion(rfcSesion: string) {
    this.rfcSesionSource.next(rfcSesion); // Cambia el valor del RFC
  }

  changeCurpSesion(curpSesion: string) {
    this.curpSesionSource.next(curpSesion); // Cambia el valor del RFC
  }
  changeNombreSesion(nombreSesion: string) {
    this.nombreSesionSource.next(nombreSesion); // Cambia el valor del Nombre
  }

  changePrimerApellidoSesion(primerApellidoSesion: string) {
    this.primerApellidoSesionSource.next(primerApellidoSesion); // Cambia el valor del PrimerApellido
  }

  changeSegundoApellidoSesion(segundoApellidoSesion: string) {
    this.segundoApellidoSesionSource.next(segundoApellidoSesion); // Cambia el valor del SegundoApellido
  }

  changeRoleSesion(rolesSesion: string[]) {
   this.roleSesionSource.next(rolesSesion); // Cambia el valor del Role
  }


  changeDelegacionSesion(delegacionSesion: string) {
    this.delegacionSesionSource.next(delegacionSesion); // Cambia el valor del Delegacion
  }


  changeSubdelegacionSesion(subdelegacionSesion: string) {
    this.subdelegacionSesionSource.next(subdelegacionSesion); // Cambia el valor del Subdelegacion
  }


  changeNumeroRegistroImssSesion(numeroRegistroImssSesion: string) {
    this.numeroRegistroImssSesionSource.next(numeroRegistroImssSesion);
  }

 
  get currentNumeroRegistroImssSesionValue(): string {
    return this.numeroRegistroImssSesionSource.getValue();
  }


    get currentRfcSesionValue(): string {
    return this.rfcSesionSource.getValue();
  }


  initializeUserData(): void {

    console.log("INICIO SharedService initializeUserData: " );
    const token = sessionStorage.getItem(this.tokenKey+"")+"";
    const payload = JSON.parse(atob(token.split('.')[1] ));

    const roles: string[] = payload.roles || []; // Asumiendo que el claim se llama 'roles'

     
    const rfc = payload.rfc;
    const curp = payload.curp;
    const nombre = payload.nombre;
    const primerApellido = payload.primerApellido;
    const segundoApellido = payload.segundoApellido;
    const desDelegacion = payload.desDelegacion;
    const desSubdelegacion = payload.desSubdelegacion;
    const numeroRegistroImss = payload.numeroRegistroImss;


    console.log("rfc: " + rfc);
    console.log("curp: " + curp);
    console.log("nombre: " + nombre);
    console.log("primerApellido: " + primerApellido);
    console.log("segundoApellido: " + segundoApellido);
    console.log("roles: " + roles.join(', '));
    console.log("desDelegacion: " + desDelegacion);
    console.log("desSubdelegacion: " + desSubdelegacion);
    console.log("numeroRegistroImss: " + numeroRegistroImss);

    this.changeRfcSesion(rfc);
    this.changeCurpSesion(curp);
    this.changeNombreSesion(nombre);
    this.changePrimerApellidoSesion(primerApellido);
    this.changeSegundoApellidoSesion(segundoApellido);
    this.changeDelegacionSesion(desDelegacion);
    this.changeSubdelegacionSesion(desSubdelegacion);
    this.changeRoleSesion(roles);
    this.changeNumeroRegistroImssSesion(numeroRegistroImss);

    if (roles.includes(Constants.rolePatron)) {

      this.changeRfc(rfc);
      this.changeCurp(curp);
      this.changeNombre(nombre);
      this.changePrimerApellido(primerApellido);
      this.changeSegundoApellido(segundoApellido);
      this.changeRole(roles);

      console.log("::rfc: " + rfc);
      console.log("::curp: " + curp);
    }
    console.log("TERMINA SharedService initializeUserData: " );
  }



    // 1. Crear el Subject para el evento de reinicio
  private resetModificacionDatosSource = new Subject<void>();

  // 2. Exponerlo como Observable para que el componente se suscriba
  resetModificacionDatos$ = this.resetModificacionDatosSource.asObservable();

  // 3. Método para disparar el evento
  triggerResetModificacionDatos() {
    this.resetModificacionDatosSource.next();
  }

  }
