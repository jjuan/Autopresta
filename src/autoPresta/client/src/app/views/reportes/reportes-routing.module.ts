import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratosFirmadosComponent} from "./contratos-firmados/contratos-firmados.component";
import {PagosRealizadosComponent} from "./pagos-realizados/pagos-realizados.component";
import {ConciliacionesComponent} from "./conciliaciones/conciliaciones.component";


const routes: Routes = [
  { path: '', redirectTo: 'Contratos-Firmados', pathMatch: 'full' },
  { path:'Contratos-Firmados', component: ContratosFirmadosComponent  },
  { path:'Pagos-Realizados', component: PagosRealizadosComponent  },
  { path:'Reporte-Conciliaciones', component: ConciliacionesComponent  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportesRoutingModule { }
