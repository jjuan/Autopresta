import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-contrato-detalle',
  templateUrl: './contrato-detalle.component.html',
  styleUrls: ['./contrato-detalle.component.sass']
})
export class ContratoDetalleComponent {
  constructor(public dialogRef: MatDialogRef<ContratoDetalleComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
              private restService: RestService) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  confirmDelete(): void {

    const _observable = this.restService.getReport(
      'contratoAutoPresta', 'Reporte',
      {
        id: this.data.data.id
      });

    return this.restService.printReport(_observable, 'Contrato AutoPresta');
  }

}
