import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratoComponent} from "./contrato/contrato.component";


const routes: Routes = [
  { path: '', redirectTo: 'Contrato', pathMatch: 'full' },
  { path:'Contrato', component: ContratoComponent  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProcesosRoutingModule { }
