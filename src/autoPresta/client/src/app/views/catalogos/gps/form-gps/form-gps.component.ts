import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {Divisas, Gps} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-form-gps',
  templateUrl: './form-gps.component.html',
  styleUrls: ['./form-gps.component.sass']
})
export class FormGpsComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  advanceTable: Gps;
  constructor(
    public dialogRef: MatDialogRef<FormGpsComponent>,
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
      nombre: [this.data.data.nombre ? this.data.data.nombre : ''],
    });
  }
}
