import {Component, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-conciliacion-ingresos',
  templateUrl: './conciliacion-ingresos.component.html',
  styleUrls: ['./conciliacion-ingresos.component.sass']
})
export class ConciliacionIngresosComponent implements OnInit {
  public datos = {
    title: 'Contrato', modulo: 'Procesos', icono: 'fas fa-desktop', componente: 'Contrato'
  };
  showTables = false
  cargoAbono=false
  public formulario: FormGroup;

  constructor(private restService: RestService) {
  }

  ngOnInit(): void {
    this.formulario = this.restService.buildForm({
      fechaInicio: ['', Validators.required],
      fechaFin: ['', Validators.required]
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
  }

}
