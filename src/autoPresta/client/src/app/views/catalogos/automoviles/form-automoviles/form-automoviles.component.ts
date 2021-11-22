import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";
import {Combo} from "../../../../core/models/data.interface";

@Component({
  selector: 'app-form-automoviles',
  templateUrl: './form-automoviles.component.html',
  styleUrls: ['./form-automoviles.component.sass']
})
export class FormAutomovilesComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  public marcasCombo: Combo[];
  public modelosCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<FormAutomovilesComponent>,
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
    this.restService.combo<Combo[]>({id: 'Marcas'}, 'comboAutoPresta').subscribe(res => this.marcasCombo = res);
    this.restService.combo<Combo[]>({id: 'Modelos'}, 'comboAutoPresta').subscribe(res => this.modelosCombo = res);

    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id?this.data.data.id:''],
      anio: [this.data.data.anio?this.data.data.anio:''],
      marca: [this.data.data.marca?this.data.data.marca.id:''],
      modelo: [this.data.data.modelo?this.data.data.modelo.id:''],
      // versionAuto: [this.data.data.versionAuto?this.data.data.versionAuto:''],
    });
  }
}
