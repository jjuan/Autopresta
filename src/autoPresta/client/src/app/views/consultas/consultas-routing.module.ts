import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratacionesComponent} from "./contrataciones/contrataciones.component";
import {ContratosFirmadosComponent} from "./contratos-firmados/contratos-firmados.component";

const routes: Routes = [
  { path: '', redirectTo: 'Contrataciones', pathMatch: 'full' },
  { path:'Contrataciones', component: ContratacionesComponent  },
  { path:'Contratos-Firmados', component: ContratosFirmadosComponent  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConsultasRoutingModule { }
