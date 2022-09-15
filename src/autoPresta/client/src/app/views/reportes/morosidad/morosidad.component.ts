import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";
import {DatePipe} from "@angular/common";
import {Combo} from "../../../core/models/data.interface";

@Component({
  selector: 'app-morosidad',
  templateUrl: './morosidad.component.html',
  styleUrls: ['./morosidad.component.sass']
})
export class MorosidadComponent implements OnInit {
  public _datos = {
    _title: 'Reporte de Morosidad',
    _modulo: 'Reportes',
    _icono: 'fas fa-desktop',
    _dominio: 'Reporte de Morosidad',
    _componente: 'Reporte de Morosidad'
  };
  formulario: FormGroup;
  fechaInicio: Date;
  fechaFin: Date;

  tipoReporteCombo: Combo[] = [
    {id: 'Morosidad Mayor 200', descripcion: 'Morosidad Mayor 200'},
    {id: 'Morosidad Mayor 100', descripcion: 'Morosidad Mayor 100'},
    {id: 'Morosidad Mayor', descripcion: 'Morosidad Mayor'},
    {id: 'Morosidad Mayor 10', descripcion: 'Morosidad Mayor 10+'},
    {id: 'Morosidad Menor 100', descripcion: 'Morosidad Menor 100'},
    {id: 'Morosidad Menor', descripcion: 'Morosidad Menor'},
    {id: 'Fraude', descripcion: 'Fraude'}
  ];

  constructor(public restService: RestService, private dateAdapter: DateAdapter<Date>, private datePipe: DatePipe) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
  }

  ngOnInit(): void {
    this.formulario = this.restService.buildForm({
      fecha: ['', Validators.required],
      tipoReporte: ['', Validators.required],
      diaInicio: [''],
      diaFin: [''],
      formato: ['XLSX']
    })
  }

  download() {
    const fecha = this.datePipe.transform(this.formulario.get('fecha').value, 'yyyy-MM-dd');
    const _observable = this.restService.getReport(
      'reporteMorosidad', 'Reporte',
      {
        tipoReporte: this.formulario.get('tipoReporte').value,
        diaInicio: this.formulario.get('diaInicio').value,
        diaFin: this.formulario.get('diaFin').value,
        fecha: fecha
      });
    return this.restService.printReport(_observable, this.formulario.get('tipoReporte').value + "." + this.formulario.get('formato').value.toLowerCase());
  }
}
