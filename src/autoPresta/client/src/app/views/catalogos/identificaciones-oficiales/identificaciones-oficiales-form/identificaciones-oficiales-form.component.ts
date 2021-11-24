import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-identificaciones-oficiales-form',
  templateUrl: './identificaciones-oficiales-form.component.html',
  styleUrls: ['./identificaciones-oficiales-form.component.sass']
})
export class IdentificacionesOficialesFormComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;

  constructor(
    public dialogRef: MatDialogRef<IdentificacionesOficialesFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, public restService: RestService,
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
      longitud: [this.data.data.longitud ? this.data.data.longitud : '', Validators.required],
      nombre: [this.data.data.nombre ? this.data.data.nombre : '', Validators.required],
    });
  }
}
