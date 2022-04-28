import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ReportesRoutingModule } from './reportes-routing.module';
import { ContratosFirmadosComponent } from './contratos-firmados/contratos-firmados.component';
import {SharedModule} from "../../shared/shared.module";
import {PagosRealizadosComponent} from "./pagos-realizados/pagos-realizados.component";
import { ConciliacionesComponent } from './conciliaciones/conciliaciones.component';


@NgModule({
  declarations: [ContratosFirmadosComponent, PagosRealizadosComponent, ConciliacionesComponent],
  imports: [
    CommonModule, SharedModule,
    ReportesRoutingModule
  ]
})
export class ReportesModule { }
