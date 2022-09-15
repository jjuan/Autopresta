import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-cambio-estatus',
  templateUrl: './cambio-estatus.component.html',
  styleUrls: ['./cambio-estatus.component.sass']
})
export class CambioEstatusComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  estatusContratoCombo: Combo[] = [
    {id: 'Activo', descripcion: 'Activo'},
    {id: 'Morosidad', descripcion: 'Morosidad'},
    {id: 'Planea Liquidar', descripcion: 'Planea Liquidar'},
    {id: 'Planea Extender', descripcion: 'Planea Extender'},
    {id: 'Por Definir', descripcion: 'Por Definir'},
  ];
  constructor(
    public dialogRef: MatDialogRef<CambioEstatusComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public restService: RestService,
  ) {}

  submit() {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  public confirmAdd(): void {
    this.dialogRef.close(this.formulario.value);
  }

  ngOnInit(): void {
    this.action = this.data.action;
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      estatusCliente: [this.data.data.estatusCliente ? this.data.data.estatusCliente : ''],
    });
  }
}
