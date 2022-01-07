import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-genera-liquidacion-delete',
  templateUrl: './genera-liquidacion-delete.component.html',
  styleUrls: ['./genera-liquidacion-delete.component.sass']
})
export class GeneraLiquidacionDeleteComponent {
  constructor( public dialogRef: MatDialogRef<GeneraLiquidacionDeleteComponent>, @Inject(MAT_DIALOG_DATA) public data: any ) {}

  onNoClick(): void { this.dialogRef.close(); }

  confirmDelete(): void { this.dialogRef.close(true); }
}
