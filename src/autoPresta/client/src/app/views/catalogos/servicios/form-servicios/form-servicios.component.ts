import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-form-servicios',
  templateUrl: './form-servicios.component.html',
  styleUrls: ['./form-servicios.component.sass']
})
export class FormServiciosComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  gps1Combo: Combo[];
  proveedores1Combo: Combo[];
  gps2Combo: Combo[];
  proveedores2Combo: Combo[];
  gps3Combo: Combo[];
  proveedores3Combo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<FormServiciosComponent>,
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
    this.restService.combo<Combo[]>({id: 'Gps'}, 'comboController').subscribe(res => this.gps1Combo = res)
    this.restService.combo<Combo[]>({id: 'Proveedores'}, 'comboController').subscribe(res => this.proveedores1Combo = res)
    this.restService.combo<Combo[]>({id: 'Gps'}, 'comboController').subscribe(res => this.gps2Combo = res)
    this.restService.combo<Combo[]>({id: 'Proveedores'}, 'comboController').subscribe(res => this.proveedores2Combo = res)
    this.restService.combo<Combo[]>({id: 'Gps'}, 'comboController').subscribe(res => this.gps3Combo = res)
    this.restService.combo<Combo[]>({id: 'Proveedores'}, 'comboController').subscribe(res => this.proveedores3Combo = res)
    this.action = this.data.action;
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      gps1: [this.data.data.gps1 ? this.data.data.gps1.id : ''],
      proveedor1: [this.data.data.proveedor1 ? this.data.data.proveedor1.id : ''],
      gps2: [this.data.data.gps2 ? this.data.data.gps2.id : ''],
      proveedor2: [this.data.data.proveedor2 ? this.data.data.proveedor2.id : ''],
      gps3: [this.data.data.gps3 ? this.data.data.gps3.id : ''],
      proveedor3: [this.data.data.proveedor3 ? this.data.data.proveedor3.id : ''],
    });
  }
}
