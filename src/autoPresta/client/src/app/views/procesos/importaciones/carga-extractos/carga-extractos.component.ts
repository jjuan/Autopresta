import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {RestService} from "../../../../core/service/rest.service";
import {Combo} from "../../../../core/models/data.interface";

@Component({
  selector: 'app-carga-extractos',
  templateUrl: './carga-extractos.component.html',
  styleUrls: ['./carga-extractos.component.sass']
})
export class CargaExtractosComponent implements OnInit {
  action: string;
  _viajeForm: FormGroup;
  dialogTitle: string;
  public bancosCombo: Combo[];
  public cuentaCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<CargaExtractosComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, public advanceTableService: RestService, private snackBar: MatSnackBar) {}

  submit() {}

  onNoClick(): void { this.dialogRef.close(); }

  public confirmAdd(): void {
    console.log(this._viajeForm.value);
    this.dialogRef.close(this._viajeForm.value);
  }

  ngOnInit(): void {
    this.advanceTableService.combo<Combo[]>( {id: 'Banco'}, 'comboController').subscribe( result => {
      this.bancosCombo = result; });
    if (this.action === 'Editar') {
      this.dialogTitle = this.data.title;
      this._viajeForm = this.advanceTableService.buildForm({
        banco: [null, Validators.required],
        cuenta: [null, Validators.required],
        file: [null],
      });
    } else {
      this.dialogTitle = 'Crear ' + this.data.title;
      this._viajeForm = this.advanceTableService.buildForm({
        banco: [null, Validators.required],
        cuenta: [null, Validators.required],
        file: [null],
      });
    }
  }

  onFileChange(event) {
    const reader = new FileReader();
    const [file] = event.target.files;
    this._viajeForm.patchValue({file: file});
  }
  cargarCuentas(value: any) {
    this.advanceTableService.combo<Combo[]>({id: 0, banco: value}, 'comboCuenta').subscribe(result =>
      this.cuentaCombo = result);
  }
}
