import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {_bancos, Combo, Folios, Portafolios} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {GlobalService} from "../../../../core/service/global.service";
import {RestService} from "../../../../core/service/rest.service";
import {HttpClient} from "@angular/common/http";
import {number} from "ngx-custom-validators/src/app/number/validator";
import {BancosFormComponent} from "../../../catalogos/bancos/bancos-form/bancos-form.component";

@Component({
  selector: 'app-extension-form',
  templateUrl: './extension-form.component.html',
  styleUrls: ['./extension-form.component.sass']
})
export class ExtensionFormComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  costoMensualInteres
  costoMensualMonitoreo
  costoMensualGPS
  totalAutoPresta
  iva
  costoMensualTotal

  tasaInteres = 5;
  tasaMonitoreo = 1;
  tasaGPS = 0.75;

  constructor(
    public dialogRef: MatDialogRef<ExtensionFormComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private globalService: GlobalService,
    public restService: RestService, private http: HttpClient
  ) {
  }

  submit() {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }


  ngOnInit(): void {
    this.dialogTitle = 'Generar extension de contrato';
    this.formulario = this.restService.buildForm({
      descripcion: [''],
      contrato: [''],
      noContrato: [''],
      parcialidadInicio: [''],
      parcialidadFin: [''],
      montoRequerido: [''],
      costoMensualInteres: [''],
      costoMensualGPS: [''],
      costoMensualTotal: [''],
      costoMensualMonitoreo: [''],
      totalAutoPresta: [''],
      iva: [''],
      totalApagar: [''],
    });
    this.restService.index<any>('Contrato', {id: this.data.id}, 'nuevaExtension').subscribe(r=> {
      this.formulario.patchValue({
        descripcion: r.descripcion,
        contrato: r.contrato,
        noContrato: r.noContrato,
        parcialidadInicio: r.parcialidadInicio,
        parcialidadFin: r.parcialidadFin,
        montoRequerido: r.montoRequerido,
      })
      this.calcular(r.montoRequerido)
    })
  }

  calcular(monto: number) {
    this.costoMensualInteres = (monto * this.tasaInteres) / 100
    this.costoMensualMonitoreo = ((monto * this.tasaMonitoreo) / 100) < 800 ? 800 : ((monto * this.tasaMonitoreo) / 100)
    this.costoMensualGPS = ((monto * this.tasaGPS) / 100) < 600 ? 600 : ((monto * this.tasaGPS) / 100)
    this.totalAutoPresta = this.costoMensualInteres + this.costoMensualMonitoreo + this.costoMensualGPS
    this.iva = this.totalAutoPresta * 0.16
    this.costoMensualTotal = this.totalAutoPresta + this.iva

    this.formulario.patchValue({
      costoMensualInteres: this.costoMensualInteres, costoMensualMonitoreo: this.costoMensualMonitoreo,
      costoMensualGPS: this.costoMensualGPS, totalAutoPresta: this.totalAutoPresta, iva: this.iva,
      costoMensualTotal: this.costoMensualTotal,
    })
  }


  confirmAdd() {
        this.restService.save<string>(this.formulario.value,{}, 'Contrato', 'generarExtension').subscribe(data => {
          this.dialogRef.close()
          // this.showNotification( 'snackbar-success', this._datos._title + 'Agregada!!', 'bottom', 'center' );
          // this.refresh();
        }, error => {
          this.dialogRef.close()
          }
        )
  }
}
