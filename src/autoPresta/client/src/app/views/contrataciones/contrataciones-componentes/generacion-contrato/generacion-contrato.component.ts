import {Component, Inject, OnInit} from '@angular/core';
import {FormGroup, Validators} from "@angular/forms";
import {Agencias, Combo, Folios, Portafolios} from "../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {RestService} from "../../../../core/service/rest.service";
import {GlobalService} from "../../../../core/service/global.service";
import {Subscription} from "rxjs";
import {HttpClient} from "@angular/common/http";

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
  public comboContratos: Combo[];

  constructor(
    public dialogRef: MatDialogRef<GeneracionContratoComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private globalService: GlobalService,
    public restService: RestService, private http: HttpClient
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
    this.action = this.data.action == "Editar"?"Editar":"Generar"
    this.dialogTitle = this.action + ' contrato';
    console.log(this.data.data)
    this.restService.combo<Combo[]>({id: 'CalificacionCliente'}, 'comboAutoPresta').subscribe(r => this.calificacionClientesCombo = r)
    this.restService.combo<Combo[]>({tipo: 'TipoContrato'}, 'combos').subscribe(r => this.comboContratos = r)
    this.formulario = this.restService.buildForm({
      calificacionCliente: [this.data.data.calificacionCliente ? this.data.data.calificacionCliente : '', Validators.required],
      fechaContrato: [this.data.data.fechaContrato ? this.data.data.fechaContrato+ 'T00:00:00' : '', Validators.required],
      contratoPrueba: [this.data.data.contratoPrueba ? this.data.data.contratoPrueba : false, Validators.required],
      contratoMonterrey: [this.data.data.contratoMonterrey ? this.data.data.contratoMonterrey : false, Validators.required],
      numeroContrato: [this.data.data.numeroContrato ? this.data.data.numeroContrato : '', Validators.required],
      referenciaBancariaBBVA: [this.data.data.referenciaBancariaBBVA ? this.data.data.referenciaBancariaBBVA : ''],
      montoTransferencia: [this.data.data.montoTransferencia ? this.data.data.montoTransferencia : '', Validators.required],
      descuentosRetenciones: [this.data.data.descuentosRetenciones ? this.data.data.descuentosRetenciones : 'N/A', Validators.required],
      fechaSolicitud: [this.data.data.fechaSolicitud ? this.data.data.fechaSolicitud : ''],
      montoLiquidar: [this.data.data.montoLiquidar ? this.data.data.montoLiquidar : ''],
      fechaCompromiso: [this.data.data.fechaCompromiso ? this.data.data.fechaCompromiso : ''],
      tipoContrato: [this.data.data.tipoContrato ? this.data.data.tipoContrato : ''],
      detalleDescuentos: ['N/A', Validators.required]
    });
    if (this.data.action != "Editar") {
      this.restService.index<any>('Sucursales', {}, 'cargarFolio').subscribe(result => {
        this.formulario.patchValue({
          fechaContrato: result.fecha + 'T00:00:00',
          numeroContrato: result.numeroContrato,
          tipoContrato: result.tipoContrato
        });
      });
    }
  }

  cambiarFolio(checked: boolean, folioMty) {
    if (checked == true && folioMty == false) {
      this.formulario.patchValue({
        numeroContrato: this.folios.folioPrueba, contratoPrueba: true, contratoMonterrey: false
      })
    } else if (checked == false && folioMty == true) {
      this.formulario.patchValue({numeroContrato: this.folios.folioMty, contratoPrueba: false, contratoMonterrey: true})
    } else {
      this.formulario.patchValue({numeroContrato: this.folios.folio, contratoPrueba: false, contratoMonterrey: false})
    }
  }

  obtenerFolio(clave) {
    this.restService.combo<any>({clave: clave}, 'obtenerFolio', 'Contrato').subscribe(r =>
      this.formulario.patchValue({numeroContrato: r.folio})
    )
  }
}
