import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {Divisas, Mercados} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-form-mercados',
  templateUrl: './form-mercados.component.html',
  styleUrls: ['./form-mercados.component.sass']
})
export class FormMercadosComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  advanceTable: Mercados;
  constructor(
    public dialogRef: MatDialogRef<FormMercadosComponent>,
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
      cveMercado: [this.data.data.cveMercado ? this.data.data.cveMercado : ''],
      fecha: [this.data.data.fecha ? this.data.data.fecha : ''],
      descripcion: [this.data.data.descripcion ? this.data.data.descripcion : ''],
    });
  }
}
