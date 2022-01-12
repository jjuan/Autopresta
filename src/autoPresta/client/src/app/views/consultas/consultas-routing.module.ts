import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratacionesComponent} from "./contrataciones/contrataciones.component";
import {ContratosFirmadosComponent} from "./contratos-firmados/contratos-firmados.component";
import {FirmaComponent} from "./firma/firma.component";

const routes: Routes = [
  { path: '', redirectTo: 'Contrataciones', pathMatch: 'full' },
  { path:'Contrataciones', component: ContratacionesComponent  },
  { path:'Contratos-Firmados', component: ContratosFirmadosComponent  },
  { path:'Firma', component: FirmaComponent  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConsultasRoutingModule { }
