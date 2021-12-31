import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {Combo, Gps} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-cambo-estado',
  templateUrl: './cambo-estado.component.html',
  styleUrls: ['./cambo-estado.component.sass']
})
export class CamboEstadoComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  estatusContratoCombo: Combo[] = [
    {id: 'Activo', descripcion: 'Activo'},
    {id: 'Inpago (vendido)', descripcion: 'Inpago (vendido)'},
    {id: 'Inpago(no vendido)', descripcion: 'Inpago(no vendido)'},
    {id: 'Liquidado anticipado', descripcion: 'Liquidado anticipado'},
    {id: 'Liquidado a tiempo', descripcion: 'Liquidado a tiempo'},
    {id: 'Fraude', descripcion: 'Fraude'},
    {id: 'extensión o aumento de Crédito', descripcion: 'extensión o aumento de Crédito'}
  ];
  constructor(
    public dialogRef: MatDialogRef<CamboEstadoComponent>,
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
      estatusContrato: [this.data.data.estatusContrato ? this.data.data.estatusContrato : ''],
    });
  }
}
