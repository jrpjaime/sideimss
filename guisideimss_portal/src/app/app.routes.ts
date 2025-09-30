import { Component } from '@angular/core';
import { Routes } from '@angular/router';
import { LayoutComponent } from './shared/layout/layout.component';
import { HomeComponent } from './business/home/home.component';
import { LoginComponent } from './business/authentication/login/login.component';
import { AuthGuard } from './core/guards/auth.guard';
import { AuthenticatedGuard } from './core/guards/authenticated.guard';
import { AcreditacionymembresiaComponent } from './business/contador/acreditacionymembresia/acreditacionymembresia.component';
import { ModificaciondatosComponent } from './business/contador/modificaciondatos/modificaciondatos.component';
import { SolicitudbajaComponent } from './business/contador/solicitudbaja/solicitudbaja.component';
import { ContadorGuard } from './core/guards/contador.guard';




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
        path: 'contador/acreditacionymembresia',
        component: AcreditacionymembresiaComponent,
        canActivate: [ContadorGuard],
      },
      {
        path: 'contador/modificaciondatos',
        component: ModificaciondatosComponent,
        canActivate: [ContadorGuard],
      },
      {
        path: 'contador/solicitudbaja',
        component: SolicitudbajaComponent,
        canActivate: [ContadorGuard],
      },
      { path: '', redirectTo: 'home', pathMatch: 'full' }
    ]
  },
  {
    path: '**',
    redirectTo: 'home'
  }
];
