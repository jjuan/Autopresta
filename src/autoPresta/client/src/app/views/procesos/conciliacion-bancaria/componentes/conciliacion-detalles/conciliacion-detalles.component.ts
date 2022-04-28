import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../../core/service/rest.service";
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../../../../core/service/global.service";
import {MatSnackBar} from "@angular/material/snack-bar";

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

  constructor(public dialogRef: MatDialogRef<ConciliacionDetallesComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private advanceTableService: RestService, private http: HttpClient,
              private snackBar: MatSnackBar, private globalService: GlobalService) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.dialogTitle = "Resumen de la conciliacion"
    console.log(this.data.info.detalles)
  }

  confirmDelete(): void {
    this.dialogRef.close(true);
  }

  showNotification(colorName, text, placementFrom, placementAlign) {
    this.snackBar.open(text, '', {
      duration: 2000,
      verticalPosition: placementFrom,
      horizontalPosition: placementAlign,
      panelClass: colorName
    });
  }

  eliminar() {
    this.advanceTableService.delete<any>(this.data.info.id, {id: this.data.info.id}, 'Conciliaciones', 'eliminarConciliacion').subscribe(r=>{
      this.showNotification('snackbar-danger', 'Conciliacion Eliminada!!', 'bottom', 'center');
    })
  }

  desconciliarMovimiento(p: any, texto: string) {
    const formData = new FormData();
    formData.append('id', p.id);
    this.http.post(this.globalService.BASE_API_URL + 'Conciliaciones/eliminarConciliacionDetalle', formData, { headers: {
        'Authorization': 'Bearer=' + this.globalService.getAuthToken()
      }
    }).subscribe(r => {
      this.showNotification('snackbar-danger', texto, 'bottom', 'center');
      this.dialogRef.close()
    });
  }
}
