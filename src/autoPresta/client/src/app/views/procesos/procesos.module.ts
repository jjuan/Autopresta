import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProcesosRoutingModule } from './procesos-routing.module';
import { ContratoComponent } from './contrato/contrato.component';
import {SharedModule} from "../../shared/shared.module";
import { ContratoDetalleComponent } from './contrato/contrato-detalle/contrato-detalle.component';


@NgModule({
  declarations: [ContratoComponent, ContratoDetalleComponent],
  imports: [
    CommonModule, SharedModule,
    ProcesosRoutingModule
  ]
})
export class ProcesosModule { }
