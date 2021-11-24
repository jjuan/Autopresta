import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratacionesComponent} from "./contrataciones/contrataciones.component";

const routes: Routes = [
  { path: '', redirectTo: 'Contrataciones', pathMatch: 'full' },
  { path:'Contrataciones', component: ContratacionesComponent  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConsultasRoutingModule { }
