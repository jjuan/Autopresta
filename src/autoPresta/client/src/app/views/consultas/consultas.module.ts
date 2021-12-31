import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ConsultasRoutingModule } from './consultas-routing.module';
import { ContratacionesComponent } from './contrataciones/contrataciones.component';
import {SharedModule} from "../../shared/shared.module";
import { ContratosFirmadosComponent } from './contratos-firmados/contratos-firmados.component';
import { DetallePagosComponent } from './contratos-firmados/detalle-pagos/detalle-pagos.component';
import { CamboEstadoComponent } from './contratos-firmados/cambo-estado/cambo-estado.component';


@NgModule({
  declarations: [ContratacionesComponent, ContratosFirmadosComponent, DetallePagosComponent, CamboEstadoComponent],
  imports: [
    CommonModule, SharedModule,
    ConsultasRoutingModule
  ]
})
export class ConsultasModule { }
