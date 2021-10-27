import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Agencias, Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-form-clientes',
  templateUrl: './form-clientes.component.html',
  styleUrls: ['./form-clientes.component.sass']
})
export class FormClientesComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  constructor(
    public dialogRef: MatDialogRef<FormClientesComponent>,
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
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      nombres: [this.data.data.nombres?this.data.data.nombres:''],
      primerApellido: [this.data.data.primerApellido?this.data.data.primerApellido:''],
      segundoApellido: [this.data.data.segundoApellido?this.data.data.segundoApellido:''],
      genero: [this.data.data.genero?this.data.data.genero:''],
      edad: [this.data.data.edad?this.data.data.edad:''],
      rfc: [this.data.data.rfc?this.data.data.rfc:''],
      fechaNacimiento: [this.data.data.fechaNacimiento?this.data.data.fechaNacimiento:''],
      curp: [this.data.data.curp?this.data.data.curp:''],
      claveElector: [this.data.data.claveElector?this.data.data.claveElector:''],
      telefonoFijo: [this.data.data.telefonoFijo?this.data.data.telefonoFijo:''],
      telefonoCelular: [this.data.data.telefonoCelular?this.data.data.telefonoCelular:''],
      telefonoOficina: [this.data.data.telefonoOficina?this.data.data.telefonoOficina:''],
      correoElectronico: [this.data.data.correoElectronico?this.data.data.correoElectronico:''],
      dirTrabajo: [this.data.data.dirTrabajo?this.data.data.dirTrabajo:false],
      dirAdicional: [this.data.data.dirAdicional?this.data.data.dirAdicional:false],
      direccionPrincipal: [this.data.data.direccionPrincipal?this.data.data.direccionPrincipal:''],
      exterior: [this.data.data.exterior?this.data.data.exterior:''],
      interior: [this.data.data.interior?this.data.data.interior:''],
      cp: [this.data.data.cp?this.data.data.cp:''],
      colonia: [this.data.data.colonia?this.data.data.colonia:''],
      municipio: [this.data.data.municipio?this.data.data.municipio:''],
      entidad: [this.data.data.entidad?this.data.data.entidad:''],
    });
  }
}
