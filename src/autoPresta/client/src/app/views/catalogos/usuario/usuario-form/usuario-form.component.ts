import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {_razonSocial} from "../../../../core/models/data.interface";
import {RestService} from "../../../../core/service/rest.service";

class _combo {
}

@Component({
  selector: 'app-usuario-form',
  templateUrl: './usuario-form.component.html',
  styleUrls: ['./usuario-form.component.sass']
})
export class UsuarioFormComponent implements OnInit {
  action: string;
  hide = true;
  hide5 = true;
  _usuarioForm: FormGroup;
  dialogTitle: string;
  advanceTable: _razonSocial;
  public razonSocialCombo: _combo[];
  public cargoCombo: _combo[];
  public perfillCombo: _combo[];
  constructor(
    public dialogRef: MatDialogRef<UsuarioFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public advanceTableService: RestService,
    private snackBar: MatSnackBar
  ) {}

  users: _combo[] = [
    {id: 'Avatar', descripcion: 'Avatar'},
    {id: 'Avatar2', descripcion: 'Avatar2'},
    {id: 'Avatar3', descripcion: 'Avatar3'},
    {id: 'Avatar4', descripcion: 'Avatar4'},
    {id: 'Avatar5', descripcion: 'Avatar5'},
    {id: 'Avatar6', descripcion: 'Avatar6'},
    {id: 'Avatar7', descripcion: 'Avatar7'},
    {id: 'Avatar8', descripcion: 'Avatar8'},
    {id: 'Avatar9', descripcion: 'Avatar9'},
    {id: 'Avatar10', descripcion: 'Avatar10'},
    {id: 'Avatar11', descripcion: 'Avatar11'},
    {id: 'Avatar12', descripcion: 'Avatar12'},
    {id: 'Avatar13', descripcion: 'Avatar13'},
    {id: 'Avatar14', descripcion: 'Avatar14'},
  ];
  submit() {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  public confirmAdd(): void {
    this.dialogRef.close(this._usuarioForm.value);
  }

  ngOnInit(): void {
    this.advanceTableService.combo<_combo[]>({id: 'RazonSocial'}, 'comboController').subscribe(result =>
      this.razonSocialCombo = result);
    this.action = this.data.action;
    if (this.action === 'Editar') {
     // this.cargarPuestos(this.data.data.razonSocial.id)
      this.dialogTitle = this.data.title;
      this._usuarioForm = this.advanceTableService.buildForm({
        id: [this.data.data.id, Validators.required],
        username: [this.data.data.username, [Validators.required]],
        password: ['', [Validators.required]],
        password5: ['', [Validators.required]],
        nombre: [this.data.data.nombre, [Validators.required]],
        apellidoPaterno: [this.data.data.apellidoPaterno, [Validators.required]],
        apellidoMaterno: [this.data.data.apellidoMaterno, [Validators.required]],
        mail: [this.data.data.mail, [Validators.required, Validators.email]],
        puesto: [this.data.data.puesto, [Validators.required]],
        avatar: [this.data.data.avatar, [Validators.required]],
        razonSocial: [this.data.data.razonSocial.id, [Validators.required]],
        desde: [this.data.data.desde, [Validators.required]],
        nuevo: [this.data.data.nuevo, [Validators.required]],
        enabled: [this.data.data.enabled, [Validators.required]],
        accountExpired: [this.data.data.accountExpired, [Validators.required]],
        accountLocked: [this.data.data.accountLocked, [Validators.required]],
        passwordExpired: [this.data.data.passwordExpired, [Validators.required]],
        perfil: ['']
      });
    } else {
      this.dialogTitle = 'Crear ' + this.data.title;
      this.advanceTableService.combo<_combo[]>({id: 'Role'}, 'comboSecurity').subscribe(result =>
        this.perfillCombo = result);
      this._usuarioForm = this.advanceTableService.buildForm({
        id: [this.data.data.id],
        username: [this.data.data.username, [Validators.required]],
        password: [this.data.data.password, [Validators.required]],
        password5: [this.data.data.password5, [Validators.required]],
        nombre: [this.data.data.nombre, [Validators.required]],
        apellidoPaterno: [this.data.data.apellidoPaterno, [Validators.required]],
        apellidoMaterno: [this.data.data.apellidoMaterno, [Validators.required]],
        mail: [this.data.data.mail, [Validators.required, Validators.email]],
        puesto: [this.data.data.cargo, [Validators.required]],
        avatar: ['Avatar', [Validators.required]],
        razonSocial: [this.data.data.razonSocial, [Validators.required]],
        desde: [this.data.data.desde, [Validators.required]],
        nuevo: [this.data.data.nuevo, [Validators.required]],
        enabled: [this.data.data.enabled, [Validators.required]],
        accountExpired: [this.data.data.accountExpired, [Validators.required]],
        accountLocked: [this.data.data.accountLocked, [Validators.required]],
        passwordExpired: [this.data.data.passwordExpired, [Validators.required]],
        perfil: ['', Validators.required]
      });
    }
  }

  cargarPuestos(value: any) {
    this.advanceTableService.combo<_combo[]>( {id: value}, 'comboPuestos').subscribe( result => {
      this.cargoCombo = result
      if (result.length == 0){
        this.showNotification( 'snackbar-danger', '¡No se encontraron puestos dentro de esta Razon Social!', 'bottom', 'center' );
      }
    });
  }

  showNotification(colorName, text, placementFrom, placementAlign) {
    this.snackBar.open(text, '', { duration: 2000, verticalPosition: placementFrom, horizontalPosition: placementAlign, panelClass: colorName });
  }
}
