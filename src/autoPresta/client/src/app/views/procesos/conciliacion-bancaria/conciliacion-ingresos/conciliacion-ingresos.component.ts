import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-conciliacion-ingresos',
  templateUrl: './conciliacion-ingresos.component.html',
  styleUrls: ['./conciliacion-ingresos.component.sass']
})
export class ConciliacionIngresosComponent implements OnInit {
  public datos = {
    title: 'Contrato', modulo: 'Procesos', icono: 'fas fa-desktop', componente: 'Contrato'
  };

  public configContratos = {

  }
  public configMovimientos = {

  }
  constructor() {
  }

  ngOnInit(): void {
  }

}
