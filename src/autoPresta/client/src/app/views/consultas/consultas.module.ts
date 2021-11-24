import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ConsultasRoutingModule } from './consultas-routing.module';
import { ContratacionesComponent } from './contrataciones/contrataciones.component';
import {SharedModule} from "../../shared/shared.module";


@NgModule({
  declarations: [ContratacionesComponent],
  imports: [
    CommonModule, SharedModule,
    ConsultasRoutingModule
  ]
})
export class ConsultasModule { }
