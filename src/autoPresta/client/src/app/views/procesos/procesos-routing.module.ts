import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratoComponent} from "./contrato/contrato.component";
import {GeneraLiquidacionComponent} from "./genera-liquidacion/genera-liquidacion.component";
import {ImportacionesComponent} from "./importaciones/importaciones.component";
import {ConciliacionEgresosComponent} from "./conciliacion-bancaria/conciliacion-egresos/conciliacion-egresos.component";
import {ConciliacionIngresosComponent} from "./conciliacion-bancaria/conciliacion-ingresos/conciliacion-ingresos.component";


const routes: Routes = [
  { path: '', redirectTo: 'Contrato', pathMatch: 'full' },
  { path:'Contrato', component: ContratoComponent  },
  { path:'Genera-Liquidacion', component: GeneraLiquidacionComponent  },
  { path:'Importaciones', component: ImportacionesComponent },
  { path:'Conciliacion-Egresos', component: ConciliacionEgresosComponent },
  { path:'Conciliacion-Ingresos', component: ConciliacionIngresosComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProcesosRoutingModule { }
