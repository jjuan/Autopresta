import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Agencias, Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-cp-form',
  templateUrl: './cp-form.component.html',
  styleUrls: ['./cp-form.component.sass']
})
export class CpFormComponent implements OnInit {

  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  // advanceTable: Agencias;
  constructor(
    public dialogRef: MatDialogRef<CpFormComponent>,
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
    console.log(this.data.data)
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      codigoPostal: [this.data.data.codigoPostal ? this.data.data.codigoPostal : '', [Validators.required, Validators.minLength(5), Validators.maxLength(5)]],
      asentamiento: [this.data.data.asentamiento ? this.data.data.asentamiento : '', Validators.required],
      ciudad: [this.data.data.ciudad ? this.data.data.ciudad : '', Validators.required],
      municipio: [this.data.data.municipio ? this.data.data.municipio : '', Validators.required],
      estado: [this.data.data.estado ? this.data.data.estado : '', Validators.required],
    });
  }
}
