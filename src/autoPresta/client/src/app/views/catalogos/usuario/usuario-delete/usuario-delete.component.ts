import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-usuario-delete',
  templateUrl: './usuario-delete.component.html',
  styleUrls: ['./usuario-delete.component.sass']
})
export class UsuarioDeleteComponent {
  constructor( public dialogRef: MatDialogRef<UsuarioDeleteComponent>, @Inject(MAT_DIALOG_DATA) public data: any ) {}

  onNoClick(): void { this.dialogRef.close(); }

  confirmDelete(): void { this.dialogRef.close(true); }
}
