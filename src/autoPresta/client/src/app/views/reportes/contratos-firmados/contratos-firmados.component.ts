import { Component, OnInit } from '@angular/core';
import {RestService} from "../../../core/service/rest.service";
import {DatePipe} from "@angular/common";
import {FormGroup, Validators} from "@angular/forms";
import {DateAdapter} from "@angular/material/core";

@Component({
  selector: 'app-contratos-firmados',
  templateUrl: './contratos-firmados.component.html',
  styleUrls: ['./contratos-firmados.component.sass']
})
export class ContratosFirmadosComponent implements OnInit {
  public _datos = {
    _title: 'Contrato', _modulo: 'Procesos', _icono: 'fas fa-desktop', _dominio: 'Contrato', _componente: 'Contrato'
  };
  _contratosFirmados: FormGroup;
  private _domain = 'Contrato'
  fechaInicio: Date;
  fechaFin: Date;

  constructor(public restService: RestService,//private dateAdapter: DateAdapter<Date>
              private datePipe: DatePipe
  ) {
    // this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
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
      'contratosFirmados', 'Reporte',
      {
        fechaInicio: fechaInicio,
        fechaFin: fechaFin
      });
    // tslint:disable-next-line:max-line-length
    return this.restService.printReport(_observable, 'ContratosFirmados.' + this._contratosFirmados.get( 'formato').value.toLowerCase());
  }

}
