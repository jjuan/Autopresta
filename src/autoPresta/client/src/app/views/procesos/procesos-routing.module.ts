import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratoComponent} from "./contrato/contrato.component";
import {GeneraLiquidacionComponent} from "./genera-liquidacion/genera-liquidacion.component";
import {ImportacionesComponent} from "./importaciones/importaciones.component";


const routes: Routes = [
  { path: '', redirectTo: 'Contrato', pathMatch: 'full' },
  { path:'Contrato', component: ContratoComponent  },
  { path:'Genera-Liquidacion', component: GeneraLiquidacionComponent  },
  { path:'Importaciones', component: ImportacionesComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProcesosRoutingModule { }
