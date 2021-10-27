import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Agencias, Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-form-marcas',
  templateUrl: './form-marcas.component.html',
  styleUrls: ['./form-marcas.component.sass']
})
export class FormMarcasComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  advanceTable: Agencias;
  agenciasCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<FormMarcasComponent>,
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
    this.restService.combo<Combo[]>({id: 'Agencias'}, 'comboAutoPresta').subscribe(result =>
      this.agenciasCombo = result);
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      agencia: [this.data.data.agencia ? this.data.data.agencia.id : '', Validators.required],
      nombre: [this.data.data.nombre ? this.data.data.nombre : '', Validators.required],
    });
  }
}
