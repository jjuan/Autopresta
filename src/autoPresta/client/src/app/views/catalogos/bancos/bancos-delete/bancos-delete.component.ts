import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-bancos-delete',
  templateUrl: './bancos-delete.component.html',
  styleUrls: ['./bancos-delete.component.sass']
})
export class BancosDeleteComponent {
  constructor( public dialogRef: MatDialogRef<BancosDeleteComponent>, @Inject(MAT_DIALOG_DATA) public data: any ) {}

  onNoClick(): void { this.dialogRef.close(); }

  confirmDelete(): void { this.dialogRef.close(true); }
}

