import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratacionesComponent} from "./contrataciones/contrataciones.component";
import {ContratosFirmadosComponent} from "./contratos-firmados/contratos-firmados.component";
import {FirmaComponent} from "./firma/firma.component";
import {ContratoComponent} from "../contrataciones/contrato/contrato.component";

const routes: Routes = [
  { path: '', redirectTo: 'Contrataciones', pathMatch: 'full' },
  { path:'Contrataciones', component: ContratacionesComponent  },
  { path:'Contratos-Firmados', component: ContratosFirmadosComponent  },
  { path:'Firma', component: FirmaComponent  },
  { path:'Extension-Contrato/:id', component: ContratoComponent  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ConsultasRoutingModule { }
