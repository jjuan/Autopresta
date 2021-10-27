import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormGroup, Validators} from "@angular/forms";
import {Combo} from "../../../../core/models/data.interface";
import {RestService} from "../../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";

@Component({
  selector: 'app-form-portafolios',
  templateUrl: './form-portafolios.component.html',
  styleUrls: ['./form-portafolios.component.sass']
})
export class FormPortafoliosComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  public usuarioCombo: Combo[];
  public mercadoCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<FormPortafoliosComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public restService: RestService,private dateAdapter: DateAdapter<Date>
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
    this.restService.combo<Combo[]>({id: 'Usuario'}, 'comboSecurity').subscribe(res => this.usuarioCombo = res)
    this.restService.combo<Combo[]>({}, 'comboMercados').subscribe(res => this.mercadoCombo = res)
    this.action = this.data.action;
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      cvePortafolio: [this.data.data.cvePortafolio ? this.data.data.cvePortafolio : '', Validators.required],
      descripcion: [this.data.data.descripcion ? this.data.data.descripcion : ''],
      fecha: [this.data.data.fecha ? this.data.data.fecha : ''],
      mercados: [this.data.data.mercados ? this.data.data.mercados.cveMercado : ''],
      // contratoVenta: [this.data.data.contratoVenta ? this.data.data.contratoVenta.id : ''],
    });
  }

  myFilter = (d: Date): boolean => {
    const day = d.getDay();
    return day !== 0 && day !== 6;
  }
}
