import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {Area} from "d3-shape";
import {FormGroup, Validators} from "@angular/forms";
import {RestService} from "../../../../core/service/rest.service";
import {Agencias, Combo} from "../../../../core/models/data.interface";

@Component({
  selector: 'app-form-agencias',
  templateUrl: './form-agencias.component.html',
  styleUrls: ['./form-agencias.component.sass']
})
export class FormAgenciasComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  advanceTable: Agencias;

  public sucursalCombo: Combo[];
  public regionCombo: Combo[];
  public portafolioCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<FormAgenciasComponent>,
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
    this.restService.combo<Combo[]>({id: 'Sucursales'}, 'comboAutoPresta').subscribe(res => this.sucursalCombo = res)
    this.restService.combo<Combo[]>({id: 'Regiones'}, 'comboAutoPresta').subscribe(res => this.regionCombo = res)
    this.restService.combo<Combo[]>({id: 'Portafolios'}, 'comboPortafolios').subscribe(res => this.portafolioCombo = res)
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      nombre: [this.data.data.nombre ? this.data.data.nombre : '', Validators.required],
      sucursal: [this.data.data.sucursal ? this.data.data.sucursal.id : '', Validators.required],
      region: [this.data.data.region ? this.data.data.region.id : '', Validators.required],
      portafolio: [this.data.data.portafolio ? this.data.data.portafolio.cvePortafolio : '', Validators.required],
      clave: [this.data.data.clave ? this.data.data.clave : '', Validators.required],
    });
  }
}
