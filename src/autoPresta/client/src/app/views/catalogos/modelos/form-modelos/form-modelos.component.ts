import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {Combo, Gps} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-form-modelos',
  templateUrl: './form-modelos.component.html',
  styleUrls: ['./form-modelos.component.sass']
})
export class FormModelosComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  advanceTable: Gps;
  marcaCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<FormModelosComponent>,
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
    this.restService.combo<Combo[]>({id: 'Marcas'}, 'comboAutoPresta').subscribe(res => this.marcaCombo = res)
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      marca: [this.data.data.marca ? this.data.data.marca.id : ''],
      nombre: [this.data.data.nombre ? this.data.data.nombre : ''],
      slug: [this.data.data.slug ? this.data.data.slug : ''],
    });
  }
}
