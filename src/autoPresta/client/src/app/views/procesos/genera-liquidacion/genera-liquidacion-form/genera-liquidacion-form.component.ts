import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {HttpClient} from '@angular/common/http';
import {MatSnackBar} from '@angular/material/snack-bar';
import {_cuentaBancaria, _instruccionesDePago, Combo} from "../../../../core/models/data.interface";
import {GlobalService} from "../../../../core/service/global.service";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-genera-liquidacion-form',
  templateUrl: './genera-liquidacion-form.component.html',
  styleUrls: ['./genera-liquidacion-form.component.sass']
})
export class GeneraLiquidacionFormComponent implements OnInit {
  action: string;
  _pagoProveedoresForm: FormGroup;
  dialogTitle: string;
  // advanceTable: _pagoProveedores;
  public razonSocialCombo: Combo[];
  public divisaCombo: Combo[];
  public beneficiarioCombo: Combo[];
  public cuentaCombo: Combo[];
  public bancosCombo: Combo[];
  public razonSocialId;
  public cuentaCliente: _cuentaBancaria;
  public cuentaProveedor: _instruccionesDePago;
  public subconceptoCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<GeneraLiquidacionFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public advanceTableService: RestService,
    private globalService: GlobalService,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  submit() {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  public confirmAdd(): void {
    this.dialogRef.close(this._pagoProveedoresForm.value);
  }

  ngOnInit(): void {
    this.advanceTableService.combo<Combo[]>({id: 'RazonSocial'}, 'comboController').subscribe(result =>
      this.razonSocialCombo = result);
    this.advanceTableService.combo<Combo[]>({id: 'Subconceptos'}, 'comboController').subscribe(result =>
      this.subconceptoCombo = result);
    this.advanceTableService.combo<Combo[]>( {id: 'Banco'}, 'comboController').subscribe( result => {
      this.bancosCombo = result; });
    this.action = this.data.action;
    if (this.action === 'Editar') {
      //this.cargarCuentas(this.data.data.banco.id);
      // this.cargarDatos(this.data.data.alias.id);
      this.dialogTitle = 'Editar ' + this.data.title;
      this._pagoProveedoresForm = this.advanceTableService.buildForm({
        id: [this.data.data.id, Validators.required],
        banco: [this.data.data.banco.id, Validators.required],
        chequerasCasa: [this.data.data.chequerasCasa.id, Validators.required],
        configuracion: [this.data.data.configuracion, Validators.required],
        habilitado: [this.data.data.habilitado, Validators.required],
        nombre: [this.data.data.nombre, Validators.required],
        orden: [this.data.data.orden, Validators.required],
        subconcepto: [this.data.data.subconcepto.id, Validators.required],
        tipoArchivo: [this.data.data.tipoArchivo, Validators.required],
        cargoAbono: [this.data.data.cargoAbono, Validators.required],
      });
    } else {
      this.dialogTitle = 'Crear ' + this.data.title;
      this._pagoProveedoresForm = this.advanceTableService.buildForm({
        id: [this.data.data.id],
        banco: [this.data.data.banco, Validators.required],
        chequerasCasa: [this.data.data.chequerasCasa, Validators.required],
        configuracion: [this.data.data.configuracion, Validators.required],
        habilitado: [this.data.data.habilitado, Validators.required],
        nombre: [this.data.data.nombre, Validators.required],
        orden: [this.data.data.orden, Validators.required],
        subconcepto: [this.data.data.subconcepto, Validators.required],
        tipoArchivo: [this.data.data.tipoArchivo, Validators.required],
        cargoAbono: [this.data.data.cargoAbono, Validators.required],
      });
    }
  }

  cargarCuentas(value: any) {
    this.advanceTableService.combo<Combo[]>({id: 0, banco: value}, 'comboCuenta').subscribe(result =>
      this.cuentaCombo = result);
  }


  showNotification(colorName, text, placementFrom, placementAlign) {
    this.snackBar.open(text, '',
      { duration: 2000, verticalPosition: placementFrom, horizontalPosition: placementAlign, panelClass: colorName });
  }

}
