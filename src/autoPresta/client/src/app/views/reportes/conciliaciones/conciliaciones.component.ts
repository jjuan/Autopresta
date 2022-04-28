import { Component, OnInit } from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../../core/service/rest.service";
import {DatePipe} from "@angular/common";
import {DateAdapter} from "@angular/material/core";

@Component({
  selector: 'app-conciliaciones',
  templateUrl: './conciliaciones.component.html',
  styleUrls: ['./conciliaciones.component.sass']
})
export class ConciliacionesComponent implements OnInit {

  public _datos = {
    _title: 'Reporte de conciliaciones', _modulo: 'Reportes', _icono: 'fas fa-desktop', _dominio: 'Reporte de conciliaciones', _componente: 'Contrato'
  };
  _contratosFirmados: FormGroup;
  private _domain = 'Contrato'
  fechaInicio: Date;
  fechaFin: Date;

  constructor(public restService: RestService,private dateAdapter: DateAdapter<Date>,
              private datePipe: DatePipe
  ) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
  }

  ngOnInit(): void {
    this._contratosFirmados = this.restService.buildForm({
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      formato: ['PDF']
    })
  }

  download() {
    // const fechaInicio = this._contratosFirmados.get('fechaInicio').value;
    const fechaInicio =  this.datePipe.transform(this._contratosFirmados.get('fechaInicio').value, 'yyyy-MM-dd');
    const fechaFin =  this.datePipe.transform(this._contratosFirmados.get('fechaFin').value, 'yyyy-MM-dd');
    // const fechaFin = this._contratosFirmados.get('fechaFin').value;
    const _observable = this.restService.getReport(
      'conciliaciones', 'Reporte',
      {
        fechaInicio: fechaInicio,
        fechaFin: fechaFin
      });
    // tslint:disable-next-line:max-line-length
    return this.restService.printReport(_observable, 'Reporte de Conciliaciones (' +this.datePipe.transform(this._contratosFirmados.get('fechaInicio').value, 'dd-MM-yyyy') + ' - ' + this.datePipe.transform(this._contratosFirmados.get('fechaFin').value, 'dd-MM-yyyy') + ')' );
  }

}
