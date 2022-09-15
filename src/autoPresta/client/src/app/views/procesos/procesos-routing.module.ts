import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratoComponent} from "../contrataciones/contrato/contrato.component";
import {GeneraLiquidacionComponent} from "./genera-liquidacion/genera-liquidacion.component";
import {ImportacionesComponent} from "./importaciones/importaciones.component";
import {ConciliacionEgresosComponent} from "./conciliacion-bancaria/conciliacion-egresos/conciliacion-egresos.component";
import {
  ConciliacionMovimientosComponent
} from "./conciliacion-bancaria/componentes/conciliacion-movimientos/conciliacion-movimientos.component";
import {
  ConciliacionContratosComponent
} from "./conciliacion-bancaria/componentes/conciliacion-contratos/conciliacion-contratos.component";


const routes: Routes = [
  { path: '', redirectTo: 'Contrato', pathMatch: 'full' },
  { path:'Contrato', component: ContratoComponent  },
  { path:'Genera-Liquidacion', component: GeneraLiquidacionComponent  },
  { path:'Importaciones', component: ImportacionesComponent },
  { path:'Conciliacion-Egresos', component: ConciliacionEgresosComponent },
  { path:'Conciliacion-Ingresos/Movimientos', component: ConciliacionMovimientosComponent },
  { path:'Conciliacion-Ingresos/Mensualidades', component: ConciliacionContratosComponent },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProcesosRoutingModule { }
