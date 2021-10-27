import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-form-proveedores',
  templateUrl: './form-proveedores.component.html',
  styleUrls: ['./form-proveedores.component.sass']
})
export class FormProveedoresComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  public monedaCombo: Combo[];
  public monedaFacturaCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<FormProveedoresComponent>,
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
    this.restService.combo<Combo[]>({id: 'Divisas'}, 'comboAutoPresta').subscribe(res => this.monedaCombo = res)
    this.restService.combo<Combo[]>({id: 'Divisas'}, 'comboAutoPresta').subscribe(res => this.monedaFacturaCombo = res)
    this.action = this.data.action;
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      nombre: [this.data.data.nombre ? this.data.data.nombre : '', Validators.required],
      rfc: [this.data.data.rfc ? this.data.data.rfc : ''],
      moneda: [this.data.data.moneda ? this.data.data.moneda.id : ''],
      monedaFactura: [this.data.data.monedaFactura ? this.data.data.monedaFactura.id : ''],
      nombreDeContacto: [this.data.data.nombreDeContacto ? this.data.data.nombreDeContacto : ''],
      correoElectronico: [this.data.data.correoElectronico ? this.data.data.correoElectronico : ''],
      direccion: [this.data.data.direccion ? this.data.data.direccion : ''],
      telefono: [this.data.data.telefono ? this.data.data.telefono : ''],
      estatus: [this.data.data.estatus ? this.data.data.estatus : ''],
      tipo: [this.data.data.tipo ? this.data.data.tipo : ''],
    });
  }
}
