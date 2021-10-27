import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { CatalogosRoutingModule } from './catalogos-routing.module';
import {SharedModule} from "../../shared/shared.module";
import { ClientesComponent } from './clientes/clientes.component';
import { FormClientesComponent } from './clientes/form-clientes/form-clientes.component';
import { AutomovilesComponent } from './automoviles/automoviles.component';
import { FormAutomovilesComponent } from './automoviles/form-automoviles/form-automoviles.component';
import { ServiciosComponent } from './servicios/servicios.component';
import { FormServiciosComponent } from './servicios/form-servicios/form-servicios.component';
import { MarcasComponent } from './marcas/marcas.component';
import { FormMarcasComponent } from './marcas/form-marcas/form-marcas.component';
import { GpsComponent } from './gps/gps.component';
import { FormGpsComponent } from './gps/form-gps/form-gps.component';
import { AgenciasComponent } from './agencias/agencias.component';
import { FormAgenciasComponent } from './agencias/form-agencias/form-agencias.component';
import { FormSucursalesComponent } from './sucursales/form-sucursales/form-sucursales.component';
import { FormRegionesComponent } from './regiones/form-regiones/form-regiones.component';
import { PortafoliosComponent } from './portafolios/portafolios.component';
import { FormPortafoliosComponent } from './portafolios/form-portafolios/form-portafolios.component';
import { ProveedoresComponent } from './proveedores/proveedores.component';
import { FormProveedoresComponent } from './proveedores/form-proveedores/form-proveedores.component';
import { MercadosComponent } from './mercados/mercados.component';
import { FormMercadosComponent } from './mercados/form-mercados/form-mercados.component';
import { DivisasComponent } from './divisas/divisas.component';
import { FormDivisasComponent } from './divisas/form-divisas/form-divisas.component';
import { SucursalesComponent } from './sucursales/sucursales.component';
import { RegionesComponent } from './regiones/regiones.component';
import { ModelosComponent } from './modelos/modelos.component';
import { FormModelosComponent } from './modelos/form-modelos/form-modelos.component';


@NgModule({
  declarations: [ ClientesComponent, FormClientesComponent, AutomovilesComponent, FormAutomovilesComponent, ServiciosComponent, FormServiciosComponent, MarcasComponent, FormMarcasComponent, GpsComponent, FormGpsComponent, AgenciasComponent, FormAgenciasComponent, FormSucursalesComponent, FormRegionesComponent, PortafoliosComponent, FormPortafoliosComponent, ProveedoresComponent, FormProveedoresComponent, MercadosComponent, FormMercadosComponent, DivisasComponent, FormDivisasComponent, SucursalesComponent, RegionesComponent, ModelosComponent, FormModelosComponent],
  imports: [
    CommonModule, SharedModule,
    CatalogosRoutingModule
  ]
})
export class CatalogosModule { }
