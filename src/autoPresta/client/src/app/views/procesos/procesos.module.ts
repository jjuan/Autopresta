import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ProcesosRoutingModule } from './procesos-routing.module';
import { ContratoComponent } from './contrato/contrato.component';
import {SharedModule} from "../../shared/shared.module";
import { ContratoDetalleComponent } from './contrato/contrato-detalle/contrato-detalle.component';
import { GeneracionContratoComponent } from './contrato/generacion-contrato/generacion-contrato.component';
import {GeneraLiquidacionComponent} from "./genera-liquidacion/genera-liquidacion.component";
import {GeneraLiquidacionFormComponent} from "./genera-liquidacion/genera-liquidacion-form/genera-liquidacion-form.component";
import {GeneraLiquidacionDeleteComponent} from "./genera-liquidacion/genera-liquidacion-delete/genera-liquidacion-delete.component";
import {GeneraLiquidacionReglaComponent} from "./genera-liquidacion/genera-liquidacion-regla/genera-liquidacion-regla.component";
import {TablaReglasComponent} from "./genera-liquidacion/tabla-reglas/tabla-reglas.component";
import {ImportacionesComponent} from "./importaciones/importaciones.component";
import {CargaExtractosComponent} from "./importaciones/carga-extractos/carga-extractos.component";
import { ConciliacionIngresosComponent } from './conciliacion-bancaria/conciliacion-ingresos/conciliacion-ingresos.component';
import { ConciliacionEgresosComponent } from './conciliacion-bancaria/conciliacion-egresos/conciliacion-egresos.component';
import {CatalogosModule} from "../catalogos/catalogos.module";
import { ConciliacionMovimientosComponent } from './conciliacion-bancaria/componentes/conciliacion-movimientos/conciliacion-movimientos.component';
import { ConciliacionContratosComponent } from './conciliacion-bancaria/componentes/conciliacion-contratos/conciliacion-contratos.component';


@NgModule({
  declarations: [ContratoComponent, ContratoDetalleComponent, GeneracionContratoComponent, GeneraLiquidacionComponent,
  GeneraLiquidacionFormComponent, GeneraLiquidacionDeleteComponent, GeneraLiquidacionReglaComponent, TablaReglasComponent,
  ImportacionesComponent, CargaExtractosComponent, ConciliacionIngresosComponent, ConciliacionEgresosComponent, ConciliacionMovimientosComponent, ConciliacionContratosComponent],
    imports: [
        CommonModule, SharedModule,
        ProcesosRoutingModule, CatalogosModule
    ]
})
export class ProcesosModule { }
