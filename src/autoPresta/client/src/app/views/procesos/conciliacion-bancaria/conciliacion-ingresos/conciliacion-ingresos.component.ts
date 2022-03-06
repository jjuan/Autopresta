import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";
import {
  ConciliacionMovimientosComponent
} from "../componentes/conciliacion-movimientos/conciliacion-movimientos.component";

@Component({
  selector: 'app-conciliacion-ingresos',
  templateUrl: './conciliacion-ingresos.component.html',
  styleUrls: ['./conciliacion-ingresos.component.sass']
})
export class ConciliacionIngresosComponent implements OnInit {
  public datos = {
    title: 'Conciliacion de ingresos', modulo: 'Procesos', icono: 'fas fa-desktop', componente: 'Conciliacion de ingresos'
  };
  showTables = false
  cargoAbono=false
  public formulario: FormGroup;

  constructor(private restService: RestService,
              private dateAdapter: DateAdapter<Date>
  ) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
  }

  ngOnInit(): void {
    this.formulario = this.restService.buildForm({
      fechaInicio: [new Date("01/01/22 00:00:00"), Validators.required],
      fechaFin: [new Date("02/28/22 00:00:00"), Validators.required]
    })
  }

  public configMovimientos = {
    cargoAbono: this.cargoAbono,
    titulo: 'Conciliacion de movimentos(Ingresos)',
    subtitulo: 'Movimiento'
  }

  public configContratos = {
    cargoAbono: this.cargoAbono,
    titulo: 'Conciliacion de contratos',
    subtitulo: 'Parcialidad'
  }

  save() {
    this.showTables = true
    // let cm = new ConciliacionMovimientosComponent()
    // cm
  }

}
