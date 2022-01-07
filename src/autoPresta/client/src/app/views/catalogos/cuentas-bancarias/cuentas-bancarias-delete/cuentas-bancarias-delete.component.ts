import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";

@Component({
  selector: 'app-cuentas-bancarias-delete',
  templateUrl: './cuentas-bancarias-delete.component.html',
  styleUrls: ['./cuentas-bancarias-delete.component.sass']
})
export class CuentasBancariasDeleteComponent {
  constructor( public dialogRef: MatDialogRef<CuentasBancariasDeleteComponent>, @Inject(MAT_DIALOG_DATA) public data: any ) {}

  onNoClick(): void { this.dialogRef.close(); }

  confirmDelete(): void { this.dialogRef.close(true); }
  }
