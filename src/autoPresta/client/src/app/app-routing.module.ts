import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { Page404Component } from './authentication/page404/page404.component';
import { AuthGuard } from './core/guard/auth.guard';
import { AuthLayoutComponent } from './layout/app-layout/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './layout/app-layout/main-layout/main-layout.component';

const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: '', redirectTo: '/Autenticacion/Login', pathMatch: 'full' },
      {
        path: 'Catalogos',
        loadChildren: () => import('./views/catalogos/catalogos.module').then((m) => m.CatalogosModule)
      },
      {
        path: 'Procesos',
        loadChildren: () => import('./views/procesos/procesos.module').then((m) => m.ProcesosModule)
      },
      {
        path: 'Reportes',
        loadChildren: () => import('./views/reportes/reportes.module').then((m) => m.ReportesModule)
      }
    ]
  },
  {
    path: 'Autenticacion',
    component: AuthLayoutComponent,
    loadChildren: () =>
      import('./authentication/authentication.module').then(
        (m) => m.AuthenticationModule
      )
  },
  { path: '**', component: Page404Component }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
