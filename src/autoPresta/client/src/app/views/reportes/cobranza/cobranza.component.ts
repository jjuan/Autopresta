import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-cobranza',
  templateUrl: './cobranza.component.html',
  styleUrls: ['./cobranza.component.sass']
})
export class CobranzaComponent implements OnInit {
  public _datos = {
    _title: 'Reporte de Coranza',
    _modulo: 'Reportes',
    _icono: 'fas fa-desktop',
    _dominio: 'Reporte de Coranza',
    _componente: 'Reporte de Coranza'
  };
  formulario: FormGroup;
  fechaInicio: Date;
  fechaFin: Date;

  constructor(public restService: RestService, private dateAdapter: DateAdapter<Date>, private datePipe: DatePipe) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
  }

  ngOnInit(): void {
    this.formulario = this.restService.buildForm({
      fechaFin: ['', Validators.required],
      formato: ['PDF']
    })
  }

  download() {
    const fechaFin = this.datePipe.transform(this.formulario.get('fechaFin').value, 'yyyy-MM-dd');
    const _observable = this.restService.getReport(
      'reporteCobranza', 'Reporte',
      {
        fechaFin: fechaFin
      });
    return this.restService.printReport(_observable, 'Reporte de Cobranza.' + this.formulario.get('formato').value.toLowerCase());
  }
}
