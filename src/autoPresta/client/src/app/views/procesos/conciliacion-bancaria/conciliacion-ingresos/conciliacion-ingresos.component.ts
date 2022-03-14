import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";
import {
  ConciliacionMovimientosComponent
} from "../componentes/conciliacion-movimientos/conciliacion-movimientos.component";
import {ConciliacionContratosComponent} from "../componentes/conciliacion-contratos/conciliacion-contratos.component";
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../../../core/service/global.service";
import {MatDialog} from "@angular/material/dialog";
import {DatePipe} from "@angular/common";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-conciliacion-ingresos',
  templateUrl: './conciliacion-ingresos.component.html',
  styleUrls: ['./conciliacion-ingresos.component.sass']
})
export class ConciliacionIngresosComponent implements OnInit {
  public datos = {
    title: 'Conciliacion de ingresos',
    modulo: 'Procesos',
    icono: 'fas fa-desktop',
    componente: 'Conciliacion de ingresos'
  };
  showTables = false
  cargoAbono = false
  public formulario: FormGroup;
  private cm: ConciliacionMovimientosComponent;
  private cc: ConciliacionContratosComponent;

  constructor(private restService: RestService,
              private dateAdapter: DateAdapter<Date>, public httpClient: HttpClient, private globalService: GlobalService, public dialog: MatDialog, private datePipe: DatePipe,
              public advanceTableService: RestService, private snackBar: MatSnackBar, private fBuilder: FormBuilder
  ) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
  }

  ngOnInit(): void {
    this.formulario = this.restService.buildForm({
      fechaInicio: [new Date("02/01/22 00:00:00"), Validators.required],
      fechaFin: [new Date("02/07/22 00:00:00"), Validators.required]
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
    this.showTables = false
    // this.cm = new ConciliacionMovimientosComponent(this.httpClient, this.globalService, this.dialog, this.datePipe, this.advanceTableService, this.snackBar, this.fBuilder)
    // this.cm.loadData()
    // this.cc = new ConciliacionContratosComponent(this.httpClient, this.globalService, this.dialog, this.datePipe, this.advanceTableService, this.snackBar, this.fBuilder)
    // this.cc.loadData()
    this.showTables = true
  }

}
