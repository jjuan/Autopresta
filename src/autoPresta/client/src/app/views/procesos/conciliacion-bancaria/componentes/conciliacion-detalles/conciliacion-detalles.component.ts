import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-conciliacion-detalles',
  templateUrl: './conciliacion-detalles.component.html',
  styleUrls: ['./conciliacion-detalles.component.sass']
})
export class ConciliacionDetallesComponent implements OnInit {
  public dialogTitle: any;
  public parcialidades = [];
  etiqueta;
  saldo;
  movimientos: any;

  constructor(public dialogRef: MatDialogRef<ConciliacionDetallesComponent>, @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.dialogTitle = "Resumen de la conciliacion"
    this.parcialidades = this.data.datos.parcialidades
    this.movimientos = this.data.datos.movimientos
  }

  confirmDelete(): void {
    this.dialogRef.close(true);
  }

  eliminarOperacion(p) {

  }
}
