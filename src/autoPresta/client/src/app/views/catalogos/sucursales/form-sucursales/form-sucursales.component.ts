import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";

@Component({
  selector: 'app-form-sucursales',
  templateUrl: './form-sucursales.component.html',
  styleUrls: ['./form-sucursales.component.sass']
})
export class FormSucursalesComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  public regionCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<FormSucursalesComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public restService: RestService
    ,private dateAdapter: DateAdapter<Date>
  ) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
    }

  submit() {}

  onNoClick(): void {
    this.dialogRef.close();
  }
  public confirmAdd(): void {
    this.dialogRef.close(this.formulario.value);
  }

  ngOnInit(): void {
    this.restService.combo<Combo[]>({id: 'Regiones'}, 'comboAutoPresta').subscribe(res => this.regionCombo = res)
    this.action = this.data.action;
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : '' ],
      descripcion: [this.data.data.descripcion ? this.data.data.descripcion : '', Validators.required],
      region: [this.data.data.region ? this.data.data.region.id : '', Validators.required],
      ciudad: [this.data.data.ciudad ? this.data.data.ciudad : '', Validators.required],
      direccion: [this.data.data.direccion ? this.data.data.direccion : '', Validators.required],
      colonia: [this.data.data.colonia ? this.data.data.colonia : '', Validators.required],
      codigoPostal: [this.data.data.codigoPostal ? this.data.data.codigoPostal : '', Validators.required],
      telefono: [this.data.data.telefono ? this.data.data.telefono : '', Validators.required],
      fecha: [this.data.data.fecha ? this.data.data.fecha+'T00:00:00' : '', Validators.required],
    });
  }

  myFilter = (d: Date): boolean => {
    const day = d.getDay();
    return day !== 0 && day !== 6;
  }
}
