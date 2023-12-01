import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";

@Component({
  selector: 'app-form-fecha-inhabil',
  templateUrl: './form-fecha-inhabil.component.html',
  styleUrls: ['./form-fecha-inhabil.component.sass']
})
export class FormFechaInhabilComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  public divisaCombo: Combo[];
  constructor(
    public dialogRef: MatDialogRef<FormFechaInhabilComponent>,
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
    this.restService.combo<Combo[]>({id: 'Divisas'}, 'comboController').subscribe(res => this.divisaCombo = res)
    this.action = this.data.action;
    this.dialogTitle = this.data.action + ' ' + this.data.title.toLowerCase();
    this.formulario = this.restService.buildForm({
      id: [this.data.data.id ? this.data.data.id : ''],
      descripcion: [this.data.data.descripcion ? this.data.data.descripcion : '', Validators.required],
      fecha: [this.data.data.fecha ? this.data.data.fecha+'T00:00:00' : '', Validators.required],
      divisa: [this.data.data.divisa ? this.data.data.divisa.id : '', Validators.required],
      // contratoVenta: [this.data.data.contratoVenta ? this.data.data.contratoVenta.id : ''],
    });
  }

  myFilter = (d: Date): boolean => {
    const day = d.getDay();
    return day !== 0 && day !== 6;
  }
}
