import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../../core/service/rest.service";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-pagos-realizados',
  templateUrl: './pagos-realizados.component.html',
  styleUrls: ['./pagos-realizados.component.sass']
})
export class PagosRealizadosComponent implements OnInit {
  public _datos = {
    _title: 'Contrato', _modulo: 'Procesos', _icono: 'fas fa-desktop', _dominio: 'Contrato', _componente: 'Contrato'
  };
  _pagosRealizados: FormGroup;
  private _domain = 'Contrato'
  fechaInicio: Date;
  fechaFin: Date;

  constructor(public restService: RestService,
              private datePipe: DatePipe) {
  }

  ngOnInit(): void {
    this._pagosRealizados = this.restService.buildForm({
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required],
      formato: ['PDF']
    })
  }

  download() {
    const fechaInicio = this.datePipe.transform(this._pagosRealizados.get('fechaInicio').value, 'yyyy-MM-dd');
    const fechaFin = this.datePipe.transform(this._pagosRealizados.get('fechaFin').value, 'yyyy-MM-dd');
    const _observable = this.restService.getReport(
      'pagosRealizados', 'Reporte',
      {
        fechaInicio: fechaInicio,
        fechaFin: fechaFin
      });
    // tslint:disable-next-line:max-line-length
    return this.restService.printReport(_observable, 'PagosRealizados.' + this._pagosRealizados.get('formato').value.toLowerCase());
  }
}
