import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratosFirmadosComponent} from "./contratos-firmados/contratos-firmados.component";
import {PagosRealizadosComponent} from "./pagos-realizados/pagos-realizados.component";


const routes: Routes = [
  { path: '', redirectTo: 'Contratos-Firmados', pathMatch: 'full' },
  { path:'Contratos-Firmados', component: ContratosFirmadosComponent  },
  { path:'Pagos-Realizados', component: PagosRealizadosComponent  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ReportesRoutingModule { }
