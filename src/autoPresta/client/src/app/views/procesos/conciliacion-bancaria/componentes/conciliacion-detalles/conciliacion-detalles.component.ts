import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../../core/service/rest.service";

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

  constructor(public dialogRef: MatDialogRef<ConciliacionDetallesComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private advanceTableService: RestService) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.dialogTitle = "Resumen de la conciliacion"
    this.parcialidades = this.data.info.detalles?this.data.info.detalles[0].operacion:this.data.datos.parcialidades
    console.log(this.parcialidades)
    this.movimientos = this.data.info.detalles?this.data.info.detalles[0].movimiento:this.data.datos.movimientos
  }

  confirmDelete(): void {
    this.dialogRef.close(true);
  }

  eliminarOperacion(p) {

  }

  eliminar() {
    this.advanceTableService.delete<any>(this.data.info.id, {id: this.data.info.id}, 'Conciliaciones', 'eliminarConciliacion').subscribe(r=>{
      console.log(r)

    })
  }
}
