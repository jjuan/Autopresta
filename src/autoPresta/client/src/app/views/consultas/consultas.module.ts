import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ConsultasRoutingModule } from './consultas-routing.module';
import { ContratacionesComponent } from './contrataciones/contrataciones.component';
import {SharedModule} from "../../shared/shared.module";
import { ContratosFirmadosComponent } from './contratos-firmados/contratos-firmados.component';
import { DetallePagosComponent } from './contratos-firmados/detalle-pagos/detalle-pagos.component';
import { CamboEstadoComponent } from './contratos-firmados/cambo-estado/cambo-estado.component';
import { FirmaComponent } from './firma/firma.component';
import {PdfViewerModule} from "ng2-pdf-viewer";
import { CambioEstatusComponent } from './contratos-firmados/cambio-estatus/cambio-estatus.component';


@NgModule({
  declarations: [ContratacionesComponent, ContratosFirmadosComponent, DetallePagosComponent, CamboEstadoComponent, FirmaComponent, CambioEstatusComponent],
  imports: [
    CommonModule, SharedModule,
    ConsultasRoutingModule, PdfViewerModule,
  ]
})
export class ConsultasModule { }
