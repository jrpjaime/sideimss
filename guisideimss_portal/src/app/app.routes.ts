import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/layout/layout.component';
import { HomeComponent } from './business/home/home.component';
import { LoginComponent } from './business/authentication/login/login.component';
import { AuthGuard } from './core/guards/auth.guard';
import { AuthenticatedGuard } from './core/guards/authenticated.guard';


import { TableroTrabajadoresComponent } from './business/tablero-trabajadores/tablero-trabajadores.component';



export const routes: Routes = [
  { path: 'login', component: LoginComponent },

  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard], // Solo necesitamos AuthGuard aquí
    children: [
      {
        path: 'home',
        component: HomeComponent,
      },
      {
        path: 'trabajadores/tablero',
        component: TableroTrabajadoresComponent,
        canActivate: [AuthGuard], // Protegido por SeleccionPatronalGuard
      },
      { path: '', redirectTo: 'home', pathMatch: 'full' }
    ]
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
