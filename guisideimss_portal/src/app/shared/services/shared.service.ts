import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Constants } from '../../global/Constants'; 

@Injectable({
  providedIn: 'root'
})
export class SharedService {

  private tokenKey = Constants.tokenKey;

  //Variables para el RFC con el que se esta trabajando
  private rfcSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentRfc = this.rfcSource.asObservable();

  private nombreSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentNombre = this.nombreSource.asObservable();

  private primerApellidoSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentPrimerApellido= this.primerApellidoSource.asObservable();

  private segundoApellidoSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentSegundoApellido= this.segundoApellidoSource.asObservable();


  private roleSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentRole = this.roleSource.asObservable();

  private registroPatronalSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentRegistroPatronal = this.registroPatronalSource.asObservable();
 






  //Variables para el usuario autenticado
  private rfcSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentRfcSesion = this.rfcSesionSource.asObservable();

  private nombreSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentNombreSesion = this.nombreSesionSource.asObservable();

  private primerApellidoSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentPrimerApellidoSesion= this.primerApellidoSesionSource.asObservable();

  private segundoApellidoSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentSegundoApellidoSesion= this.segundoApellidoSesionSource.asObservable();


  private roleSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentRoleSesion = this.roleSesionSource.asObservable();


  private delegacionSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentDelegacionSesion = this.delegacionSesionSource.asObservable();


  private subdelegacionSesionSource = new BehaviorSubject<string>(''); // Valor por defecto
  currentSubdelegacionSesion = this.subdelegacionSesionSource.asObservable();




  constructor() {}

  changeRfc(rfc: string) {
    this.rfcSource.next(rfc); // Cambia el valor del RFC
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

  changeRole(role: string) {
    this.roleSource.next(role); // Cambia el valor del Role
  }


  changeRegistroPatronal(registroPatronal: string) {
    this.registroPatronalSource.next(registroPatronal); // Cambia el valor del registro patronal
  }

 





// Variables para el usuario autenticado
  changeRfcSesion(rfcSesion: string) {
    this.rfcSesionSource.next(rfcSesion); // Cambia el valor del RFC
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

  changeRoleSesion(roleSesion: string) {
    this.roleSesionSource.next(roleSesion); // Cambia el valor del Role
  }


  changeDelegacionSesion(delegacionSesion: string) {
    this.delegacionSesionSource.next(delegacionSesion); // Cambia el valor del Delegacion
  }


  changeSubdelegacionSesion(subdelegacionSesion: string) {
    this.subdelegacionSesionSource.next(subdelegacionSesion); // Cambia el valor del Subdelegacion
  }


    get currentRfcSesionValue(): string {
    return this.rfcSesionSource.getValue();
  }


  initializeUserData(): void {

    console.log("INICIO SharedService initializeUserData: " );
    const token = sessionStorage.getItem(this.tokenKey+"")+"";
    const payload = JSON.parse(atob(token.split('.')[1] ));
    const role = payload.role;
    const rfc = payload.rfc;
    const nombre = payload.nombre;
    const primerApellido = payload.primerApellido;
    const segundoApellido = payload.segundoApellido;
    const desDelegacion = payload.desDelegacion;
    const desSubdelegacion = payload.desSubdelegacion;


    console.log("rfc: " + rfc);
    console.log("nombre: " + nombre);
    console.log("primerApellido: " + primerApellido);
    console.log("segundoApellido: " + segundoApellido);
    console.log("role: " + role);
    console.log("desDelegacion: " + desDelegacion);
    console.log("desSubdelegacion: " + desSubdelegacion);

    this.changeRfcSesion(rfc);
    this.changeNombreSesion(nombre);
    this.changePrimerApellidoSesion(primerApellido);
    this.changeSegundoApellidoSesion(segundoApellido);
    this.changeDelegacionSesion(desDelegacion);
    this.changeSubdelegacionSesion(desSubdelegacion);
    this.changeRoleSesion(role);

    if (role === Constants.rolePatron) {

      this.changeRfc(rfc);
      this.changeNombre(nombre);
      this.changePrimerApellido(primerApellido);
      this.changeSegundoApellido(segundoApellido);
      this.changeRole(role);

      console.log("::rfc: " + rfc);

    }
    console.log("TERMINA SharedService initializeUserData: " );
  }


  }
