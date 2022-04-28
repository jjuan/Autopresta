import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Agencias, Combo} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-form-folios',
  templateUrl: './form-folios.component.html',
  styleUrls: ['./form-folios.component.sass']
})
export class FormFoliosComponent implements OnInit {

  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  constructor(
    public dialogRef: MatDialogRef<FormFoliosComponent>,
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
      cveTipo: [this.data.data.cveTipo ? this.data.data.cveTipo : ''],
      folio: [this.data.data.folio ? this.data.data.folio : '', Validators.required],
    });
  }
}
