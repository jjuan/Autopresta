import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Agencias, Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-calificacion-cliente-form',
  templateUrl: './calificacion-cliente-form.component.html',
  styleUrls: ['./calificacion-cliente-form.component.sass']
})
export class CalificacionClienteFormComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  advanceTable: Agencias;
  agenciasCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<CalificacionClienteFormComponent>,
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
      descripcion: [this.data.data.descripcion ? this.data.data.descripcion : ''],
      nombre: [this.data.data.nombre ? this.data.data.nombre : '', Validators.required],
    });
  }
}
