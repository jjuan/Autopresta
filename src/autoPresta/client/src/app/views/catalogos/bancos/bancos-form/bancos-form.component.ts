import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";
import {_bancos} from "../../../../core/models/data.interface";

@Component({
  selector: 'app-bancos-form',
  templateUrl: './bancos-form.component.html',
  styleUrls: ['./bancos-form.component.sass']
})
export class BancosFormComponent implements OnInit {
  action: string;
  _bancosForm: FormGroup;
  dialogTitle: string;
  advanceTable: _bancos;
  constructor(
    public dialogRef: MatDialogRef<BancosFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public advanceTableService: RestService,
  ) {}

  submit() {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  public confirmAdd(): void {
    this.dialogRef.close(this._bancosForm.value);
  }

  ngOnInit(): void {

    this.action = this.data.action;
    if (this.action === 'Editar') {
      this.dialogTitle = this.data.data.descripcionCorta;
      this._bancosForm = this.advanceTableService.buildForm({
        id: [this.data.data.id, Validators.required],
        descripcionCorta: [this.data.data.descripcionCorta, Validators.required],
        descripcionLarga: [this.data.data.descripcionLarga, Validators.required],
        direccion_clc: [this.data.data.direccion_clc],
        direccion_con: [this.data.data.direccion_con],
        direccion_dis: [this.data.data.direccion_dis],
        pais: [this.data.data.pais, Validators.required],
      });
    } else {
      this.dialogTitle = 'Crear ' + this.data.title;
      this._bancosForm = this.advanceTableService.buildForm({
        id: [this.data.data.id],
        descripcionCorta: [this.data.data.descripcionCorta, Validators.required],
        descripcionLarga: [this.data.data.descripcionLarga, Validators.required],
        direccion_clc: [this.data.data.direccion_clc],
        direccion_con: [this.data.data.direccion_con],
        direccion_dis: [this.data.data.direccion_dis],
        pais: [this.data.data.pais, Validators.required],
      });
    }
  }
}
