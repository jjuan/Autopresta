import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {MatSnackBar} from '@angular/material/snack-bar';
import {RestService} from "../../../../core/service/rest.service";

@Component({
  selector: 'app-tabla-reglas',
  templateUrl: './tabla-reglas.component.html',
  styleUrls: ['./tabla-reglas.component.sass']
})
export class TablaReglasComponent {
  constructor( public dialogRef: MatDialogRef<TablaReglasComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
               public advanceTableService: RestService, public snack: MatSnackBar) {
  }

  onNoClick(): void { this.dialogRef.close(); }

  deleteItem(row: any) {
    this.advanceTableService.initService('GenLiq_Ext_Det');
        this.advanceTableService.delete<string>(row.id)
          .subscribe(data => {
            this.showNotification( 'snackbar-danger', '¡¡ Regla Eliminada!!', 'bottom', 'center' );
            this.dialogRef.close();
          }, error => {
            this.showNotification( 'snackbar-danger', '¡Error al eliminar! Este registro esta siendo utilizado', 'bottom', 'center' );
            Object.entries(error._embedded.errors).forEach(([key, value]) => { });
          });
      }
  showNotification(colorName, text, placementFrom, placementAlign) {
    // tslint:disable-next-line:max-line-length
    this.snack.open(text, '', { duration: 2000, verticalPosition: placementFrom, horizontalPosition: placementAlign, panelClass: colorName });
  }
}
