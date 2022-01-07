import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {_cuentaBancaria, Combo} from "../../../../core/models/data.interface";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-cuentas-bancarias-form',
  templateUrl: './cuentas-bancarias-form.component.html',
  styleUrls: ['./cuentas-bancarias-form.component.sass']
})
export class CuentasBancariasFormComponent implements OnInit {
  action: string;
  _cuentaBancariaForm: FormGroup;
  dialogTitle: string;
  advanceTable: _cuentaBancaria;
  public razonSocialCombo: Combo[];
  public divisaCombo: Combo[];
  public bancoCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<CuentasBancariasFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public advanceTableService: RestService,
  ) {}

  submit() {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  public confirmAdd(): void {
    this.dialogRef.close(this._cuentaBancariaForm.value);
  }

  ngOnInit(): void {
    this.advanceTableService.combo<Combo[]>({id: 'Portafolios'}, 'comboPortafolios').subscribe(result =>
      this.razonSocialCombo = result);
    this.advanceTableService.combo<Combo[]>({id: 'Divisas'}, 'comboController').subscribe(result =>
      this.divisaCombo = result);
    this.advanceTableService.combo<Combo[]>({id: 'Banco'}, 'comboController').subscribe(result =>
      this.bancoCombo = result);
    this.action = this.data.action;
    if (this.action === 'Editar') {
      this.dialogTitle = this.data.data.alias;
      this._cuentaBancariaForm = this.advanceTableService.buildForm({
        id: [this.data.data.id, Validators.required],
        razonSocial: [this.data.data.razonSocial.id, Validators.required],
        banco: [this.data.data.banco.id, Validators.required],
        alias: [this.data.data.alias, Validators.required],
        cuenta: [this.data.data.cuenta, [ Validators.required, Validators.minLength(6), Validators.maxLength(10)]],
        clabe: [this.data.data.clabe, [ Validators.required, Validators.minLength(18), Validators.maxLength(18)]],
        fechaDeApertura: [this.data.data.fechaDeApertura, Validators.required],
        estatus: [this.data.data.estatus, Validators.required],
        fechaDeCancelacion: [this.data.data.fechaDeCancelacion],
        moneda: [this.data.data.moneda.id, Validators.required],
      });
    } else {
      this.dialogTitle = 'Crear ' + this.data.title;
      this._cuentaBancariaForm = this.advanceTableService.buildForm({
        id: [this.data.data.id],
        razonSocial: [this.data.data.razonSocial, Validators.required],
        banco: [this.data.data.banco, Validators.required],
        alias: [this.data.data.alias, Validators.required],
        cuenta: [this.data.data.cuenta, [ Validators.required, Validators.minLength(6), Validators.maxLength(10)]],
        clabe: [this.data.data.clabe, [ Validators.required, Validators.minLength(18), Validators.maxLength(18)]],
        fechaDeApertura: [this.data.data.fechaDeApertura, Validators.required],
        estatus: [this.data.data.estatus, Validators.required],
        fechaDeCancelacion: [this.data.data.fechaDeCancelacion],
        moneda: [this.data.data.moneda, Validators.required],
      });
    }
  }
}
