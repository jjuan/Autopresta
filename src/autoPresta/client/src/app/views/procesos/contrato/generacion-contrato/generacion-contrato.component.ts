import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Agencias, Combo, Folios, Portafolios} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";
import {GlobalService} from "../../../../core/service/global.service";
import {Subscription} from "rxjs";

@Component({
  selector: 'app-generacion-contrato',
  templateUrl: './generacion-contrato.component.html',
  styleUrls: ['./generacion-contrato.component.sass']
})
export class GeneracionContratoComponent implements OnInit {
  action: string;
  formulario: FormGroup;
  dialogTitle: string;
  calificacionClientesCombo: Combo[];
  folios: Folios;

  constructor(
    public dialogRef: MatDialogRef<GeneracionContratoComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private globalService: GlobalService,
    public restService: RestService,
  ) {
  }

  submit() {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  public confirmAdd(): void {
    this.dialogRef.close(this.formulario.value);
  }

  ngOnInit(): void {
    this.dialogTitle = 'Generar contrato';

    this.restService.combo<Combo[]>({id: 'CalificacionCliente'}, 'comboAutoPresta').subscribe(r => this.calificacionClientesCombo = r)
    this.formulario = this.restService.buildForm({
      calificacionCliente: [this.data.data.calificacionCliente ? this.data.data.calificacionCliente : '', Validators.required],
      fechaContrato: [this.data.data.fechaContrato ? this.data.data.fechaContrato : '', Validators.required],
      contratoPrueba: [this.data.data.contratoPrueba ? this.data.data.contratoPrueba : false, Validators.required],
      contratoMonterrey: [this.data.data.contratoMonterrey ? this.data.data.contratoMonterrey : false, Validators.required],
      numeroContrato: [this.folios ? this.folios.folio : '', Validators.required],
      referenciaBancariaBBVA: [this.data.data.referenciaBancariaBBVA ? this.data.data.referenciaBancariaBBVA : ''],
      montoTransferencia: [this.data.data.montoTransferencia ? this.data.data.montoTransferencia : '', Validators.required],
      descuentosRetenciones: [this.data.data.descuentosRetenciones ? this.data.data.descuentosRetenciones : 'N/A', Validators.required],
      fechaSolicitud: [this.data.data.fechaSolicitud ? this.data.data.fechaSolicitud : ''],
      montoLiquidar: [this.data.data.montoLiquidar ? this.data.data.montoLiquidar : ''],
      fechaCompromiso: [this.data.data.fechaCompromiso ? this.data.data.fechaCompromiso : ''],
      detalleDescuentos: ['N/A', Validators.required]
    });
    this.restService.edit<Portafolios>(1, 'Portafolios').subscribe(result => {
      this.formulario.patchValue({fechaContrato: result.fecha + 'T00:00:00'});
    });

    this.restService.index<Folios>('Contrato', {}, 'folios').subscribe(r => {
      this.folios = r
      this.formulario.patchValue({numeroContrato: this.folios.folio})
    })
  }

  cambiarFolio(checked: boolean, folioMty) {
    if (checked == true && folioMty == false) {
      this.formulario.patchValue({
        numeroContrato: this.folios.folioPrueba, contratoPrueba: true, contratoMonterrey: false
      })
    } else if (checked == false && folioMty == true) {
      this.formulario.patchValue({numeroContrato: this.folios.folioMty , contratoPrueba: false, contratoMonterrey: true})
    } else {
      this.formulario.patchValue({numeroContrato: this.folios.folio , contratoPrueba: false, contratoMonterrey: false})
    }
  }

}
