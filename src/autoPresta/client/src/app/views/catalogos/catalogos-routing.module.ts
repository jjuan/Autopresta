import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {AgenciasComponent} from "./agencias/agencias.component";
import {ClientesComponent} from "./clientes/clientes.component";
import {AutomovilesComponent} from "./automoviles/automoviles.component";
import {ServiciosComponent} from "./servicios/servicios.component";
import {MarcasComponent} from "./marcas/marcas.component";
import {GpsComponent} from "./gps/gps.component";
import {SucursalesComponent} from "./sucursales/sucursales.component";
import {RegionesComponent} from "./regiones/regiones.component";
import {PortafoliosComponent} from "./portafolios/portafolios.component";
import {ProveedoresComponent} from "./proveedores/proveedores.component";
import {MercadosComponent} from "./mercados/mercados.component";
import {DivisasComponent} from "./divisas/divisas.component";
import {ModelosComponent} from "./modelos/modelos.component";
import {CalificacionClienteComponent} from "./calificacion-cliente/calificacion-cliente.component";
import {IdentificacionesOficialesComponent} from "./identificaciones-oficiales/identificaciones-oficiales.component";
import {CuentasBancariasComponent} from "./cuentas-bancarias/cuentas-bancarias.component";
import {UsuarioComponent} from "./usuario/usuario.component";
import {BancosComponent} from "./bancos/bancos.component";
import {FoliosComponent} from "./folios/folios.component";
import {FliosRecupeadosComponent} from "./flios-recupeados/flios-recupeados.component";


const routes: Routes = [
  { path: '', redirectTo: 'Agencias', pathMatch: 'full' },
  { path:'Clientes', component: ClientesComponent  },
  { path:'Automoviles', component: AutomovilesComponent  },
  { path:'Servicios', component: ServiciosComponent },
  { path:'Marcas', component: MarcasComponent },
  { path:'GPS', component: GpsComponent },
  { path:'Agencias', component: AgenciasComponent },
  { path:'Sucursales', component: SucursalesComponent },
  { path:'Regiones', component: RegionesComponent },
  { path:'Portafolios', component: PortafoliosComponent },
  { path:'Proveedores', component: ProveedoresComponent },
  { path:'Mercados', component: MercadosComponent },
  { path:'Divisas', component: DivisasComponent },
  { path:'Modelos', component: ModelosComponent },
  { path:'Calificacion-Clientes', component: CalificacionClienteComponent },
  { path:'Identificaciones-Oficiales', component: IdentificacionesOficialesComponent },
  { path:'Cuentas-Bancarias', component: CuentasBancariasComponent },
  { path:'Usuarios', component: UsuarioComponent },
  { path:'Bancos', component: BancosComponent },
  { path:'Folios', component: FoliosComponent },
  { path:'Folios-Recuperados', component: FliosRecupeadosComponent },

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CatalogosRoutingModule { }
