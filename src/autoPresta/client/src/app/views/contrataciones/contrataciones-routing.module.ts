import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ContratoPersonaFisicaComponent} from "./contrato-persona-fisica/contrato-persona-fisica.component";
import {ContratoPersonaFisicaCoacreditadoComponent} from "./contrato-persona-fisica-coacreditado/contrato-persona-fisica-coacreditado.component";
import {ContratoPersonaMoralComponent} from "./contrato-persona-moral/contrato-persona-moral.component";
import {ContratoComponent} from "./contrato/contrato.component";
import {
  ExtensionContratosComponent
} from "./contrataciones-componentes/extension-contratos/extension-contratos.component";

const routes: Routes = [
  { path: '', redirectTo: 'Contrato-Persona-Fisica', pathMatch: 'full' },
  { path:'Contrato-Persona-Fisica', component: ContratoPersonaFisicaComponent  },
  { path:'Contrato-Persona-Fisica-Coacreditado', component: ContratoPersonaFisicaCoacreditadoComponent  },
  { path:'Contrato-Persona-Moral', component: ContratoPersonaMoralComponent  },
  { path:'Edicion-Contrato/:id', component: ContratoComponent  },
  { path:'Extension-Contrato/:id', component: ExtensionContratosComponent  },
];
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ContratacionesRoutingModule { }
