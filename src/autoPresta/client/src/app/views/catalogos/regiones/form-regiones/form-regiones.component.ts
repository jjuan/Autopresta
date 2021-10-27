import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {Divisas, Regiones} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-form-regiones',
  templateUrl: './form-regiones.component.html',
  styleUrls: ['./form-regiones.component.sass']
})
export class FormRegionesComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  advanceTable: Regiones;
  constructor(
    public dialogRef: MatDialogRef<FormRegionesComponent>,
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
      clave: [this.data.data.clave ? this.data.data.clave : ''],
      descripcion: [this.data.data.descripcion ? this.data.data.descripcion : ''],
      variacion: [this.data.data.variacion ? this.data.data.variacion : 0],
    });
  }
}
