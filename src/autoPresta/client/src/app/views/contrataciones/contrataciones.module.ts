import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ContratacionesRoutingModule } from './contrataciones-routing.module';
import { ContratoPersonaFisicaComponent } from './contrato-persona-fisica/contrato-persona-fisica.component';
import { ContratoPersonaFisicaCoacreditadoComponent } from './contrato-persona-fisica-coacreditado/contrato-persona-fisica-coacreditado.component';
import { ContratoPersonaMoralComponent } from './contrato-persona-moral/contrato-persona-moral.component';
import {SharedModule} from "../../shared/shared.module";
import { ExtensionContratosComponent } from './contrataciones-componentes/extension-contratos/extension-contratos.component';
import { ExtensionFormComponent } from './contrataciones-componentes/extension-form/extension-form.component';


@NgModule({
  declarations: [ContratoPersonaFisicaComponent, ContratoPersonaFisicaCoacreditadoComponent, ContratoPersonaMoralComponent, ExtensionContratosComponent, ExtensionFormComponent],
  imports: [
    CommonModule, SharedModule,
    ContratacionesRoutingModule
  ]
})
export class ContratacionesModule { }
