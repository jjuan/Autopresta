import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-detalle-pagos',
  templateUrl: './detalle-pagos.component.html',
  styleUrls: ['./detalle-pagos.component.sass']
})
export class DetallePagosComponent implements OnInit {
  dialogTitle: any;

  constructor(public dialogRef: MatDialogRef<DetallePagosComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
    // this.dialogRef.disableClose = true;
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.dialogTitle = this.data.title;
    console.log(this.data.datos)
  }

  confirmDelete(): void {
    this.dialogRef.close(true);
  }
}
