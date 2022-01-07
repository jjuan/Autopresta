import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {RestService} from "../../../../core/service/rest.service";
import {_genLiq_ext_det} from "../../../../core/models/data.interface";

@Component({
  selector: 'app-genera-liquidacion-regla',
  templateUrl: './genera-liquidacion-regla.component.html',
  styleUrls: ['./genera-liquidacion-regla.component.sass']
})
export class GeneraLiquidacionReglaComponent implements OnInit {
  action: string;
  _genLiq_ext_detsForm: FormGroup;
  dialogTitle: string;
  advanceTable: _genLiq_ext_det;
  constructor(
    public dialogRef: MatDialogRef<GeneraLiquidacionReglaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public advanceTableService: RestService,
  ) {}

  submit() {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  public confirmAdd(): void {
    this.dialogRef.close(this._genLiq_ext_detsForm.value);
  }

  ngOnInit(): void {

    this.action = this.data.action;
    if (this.action === 'Editar') {
      this.dialogTitle = this.data.data.descripcion;
      this._genLiq_ext_detsForm = this.advanceTableService.buildForm({
        genLiqExtHead: [this.data.head],
        operador: [this.data.data.operador],
        orden: [this.data.orden],
        valor: [this.data.data.valor],
        campoLiq: [this.data.data.campoLiq]
      });
    } else {
      this.dialogTitle = 'Crear ' + this.data.title;
      this._genLiq_ext_detsForm = this.advanceTableService.buildForm({
        genLiqExtHead: [this.data.head],
        operador: [this.data.data.operador],
        orden: [this.data.orden],
        valor: [this.data.data.valor],
        campoLiq: [this.data.data.campoLiq]
      });
    }
  }
}
