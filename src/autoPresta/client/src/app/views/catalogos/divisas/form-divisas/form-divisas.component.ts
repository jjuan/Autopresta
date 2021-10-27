import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../../../core/service/rest.service";
import {Divisas} from "../../../../core/models/data.interface";

@Component({
  selector: 'app-form-divisas',
  templateUrl: './form-divisas.component.html',
  styleUrls: ['./form-divisas.component.sass']
})
export class FormDivisasComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  advanceTable: Divisas;
  constructor(
    public dialogRef: MatDialogRef<FormDivisasComponent>,
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
    });
  }
}
