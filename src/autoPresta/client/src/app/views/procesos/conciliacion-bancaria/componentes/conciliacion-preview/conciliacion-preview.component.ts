import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-conciliacion-preview',
  templateUrl: './conciliacion-preview.component.html',
  styleUrls: ['./conciliacion-preview.component.sass']
})
export class ConciliacionPreviewComponent {
  constructor( public dialogRef: MatDialogRef<ConciliacionPreviewComponent>,
               @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  onNoClick(): void { this.dialogRef.close(); }

  confirmDelete(): void { this.dialogRef.close(true); }
}
