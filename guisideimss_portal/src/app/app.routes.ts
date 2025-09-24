import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/layout/layout.component';
import { HomeComponent } from './business/home/home.component';  
import { LoginComponent } from './business/authentication/login/login.component';
import { AuthGuard } from './core/guards/auth.guard';
import { AuthenticatedGuard } from './core/guards/authenticated.guard';
import { SeleccionPatronalGuard } from './core/guards/seleccion-patronal.guard';
 
import { TerminosYCondicionesComponent } from './business/terminos-y-condiciones/terminos-y-condiciones.component';
import { terminosAceptadosGuard } from './core/guards/terminos-aceptados.guard';
import { TableroTrabajadoresComponent } from './business/tablero-trabajadores/tablero-trabajadores.component';

 

export const routes: Routes = [
 

      { path: 'login', component: LoginComponent }, 
     
      { 
        path: 'aceptacion-terminos',  
        component: TerminosYCondicionesComponent, 
        canActivate: [AuthGuard] // Solo necesita el AuthGuard
      }, 

     {
      path: '',
      component:LayoutComponent,
      canActivate: [AuthGuard, terminosAceptadosGuard],
      children: [
          {
              path: 'home',
              component:HomeComponent,
              //canActivate:[AuthGuard]
          },
          { path: 'terminos-y-condiciones', component: TerminosYCondicionesComponent, canActivate: [AuthGuard] }, 
          
          {
            path: 'trabajadores/tablero', // La URL que definiste en el menú
            component: TableroTrabajadoresComponent, // El componente a cargar
            canActivate: [SeleccionPatronalGuard] // Protegido por el mismo guard que otras páginas de negocio
          },
 
 
   
 



          {path: '', redirectTo: 'home', pathMatch: 'full'}
      ]
  },
/*  {
      path: 'login',
      component:LoginComponent,
      canActivate:[AuthenticatedGuard]
  },*/
  {
      path: '**',
      redirectTo: 'home'
  }
];
