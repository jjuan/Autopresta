import { Component, OnInit } from '@angular/core';
import {AbstractControl, FormGroup, Validators} from "@angular/forms";
import {_comboCp, Combo, Contrato, direccion, IdentificacionesOficiales} from "../../../core/models/data.interface";
import {GlobalService} from "../../../core/service/global.service";
import {DateAdapter} from "@angular/material/core";
import {MatDialog} from "@angular/material/dialog";
import {ActivatedRoute} from "@angular/router";
import {GeneracionContratoComponent} from "../../procesos/contrato/generacion-contrato/generacion-contrato.component";
import {RestService} from "../../../core/service/rest.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-contrato-persona-fisica-coacreditado',
  templateUrl: './contrato-persona-fisica-coacreditado.component.html',
  styleUrls: ['./contrato-persona-fisica-coacreditado.component.sass']
})
export class ContratoPersonaFisicaCoacreditadoComponent implements OnInit {
  public _datos = {
    _title: 'Contrato', _modulo: 'Procesos', _icono: 'fas fa-desktop', _dominio: 'Contrato', _componente: 'Contrato'
  };

  montoMaximoAutorizado = 0
  public direcciones = [];
  public formulario: FormGroup;
  public gps1Combo: Combo[];
  public gps2Combo: Combo[];
  public gps3Combo: Combo[];
  public tipoContratoCombo: Combo[];
  public regimenFiscalCombo: Combo[];
  public modelosCombo: Combo[];
  public marcasCombo: Combo[];
  public direccion: FormGroup;

  costoMensualInteres = 0
  costoMensualMonitoreo = 0
  costoMensualGPS = 0
  totalAutoPresta = 0
  iva = 0
  costoMensualTotal = 0

  tasaInteres = 5;
  tasaMonitoreo = 1;
  tasaGPS = 0.75;

  public longitud = 1
  public longitudCoacreditado = 1

  public respuesta: Contrato;
  public estadosCombo: Combo[];
  public keyword = 'descripcion';
  public coloniasCombo: Combo[];
  public coloniasMoralCombo: Combo[];
  public coacreditado = false
  public aniosCombo: Combo[] = [
    {id: '2010', descripcion: '2010'},
    {id: '2011', descripcion: '2011'},
    {id: '2012', descripcion: '2012'},
    {id: '2013', descripcion: '2013'},
    {id: '2014', descripcion: '2014'},
    {id: '2015', descripcion: '2015'},
    {id: '2016', descripcion: '2016'},
    {id: '2017', descripcion: '2017'},
    {id: '2018', descripcion: '2018'},
    {id: '2019', descripcion: '2019'},
    {id: '2020', descripcion: '2020'},
    {id: '2021', descripcion: '2021'},
    {id: '2022', descripcion: '2022'}

  ];
  public documentoOficialCombo: IdentificacionesOficiales[];
  public documentoOficialCoacreditadoCombo: IdentificacionesOficiales[];

  constructor(
    private globalService: GlobalService, private genericRestService: RestService, private activatedroute: ActivatedRoute,
    private snack: MatSnackBar, public dialog: MatDialog, private dateAdapter: DateAdapter<Date>
  ) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
  }

  ngOnInit() {
    this.form()
    this.direcciones = []
    this.costoMensualInteres = 0;
    this.costoMensualMonitoreo = 0;
    this.costoMensualGPS = 0;
    this.totalAutoPresta = 0;
    this.iva = 0;
    this.costoMensualTotal = 0;
    this.genericRestService.combo<Combo[]>({id: 'Marcas'}, 'comboAutoPresta').subscribe(res => this.marcasCombo = res);
    this.genericRestService.combo<Combo[]>({id: 'Gps'}, 'comboAutoPresta').subscribe(result => this.gps1Combo = result);
    this.genericRestService.combo<Combo[]>({id: 'Gps'}, 'comboAutoPresta').subscribe(result => this.gps2Combo = result);
    this.genericRestService.combo<Combo[]>({id: 'Gps'}, 'comboAutoPresta').subscribe(result => this.gps3Combo = result);
    this.genericRestService.combo<Combo[]>({id: 'TipoContrato'}, 'comboAutoPresta').subscribe(result => this.tipoContratoCombo = result);
    this.genericRestService.combo<Combo[]>({id: 'C_RegimenFiscal'}, 'comboFactura').subscribe(result => this.regimenFiscalCombo = result);
    this.genericRestService.combo<Combo[]>({id: 'Estados'}, 'comboAutoPresta').subscribe(result => {
      this.estadosCombo = result
    });
    this.genericRestService.combo<IdentificacionesOficiales[]>({}, 'comboDocumentos').subscribe(result => {
      this.documentoOficialCombo = result
    });
    this.genericRestService.combo<IdentificacionesOficiales[]>({}, 'comboDocumentos').subscribe(result => {
      this.documentoOficialCoacreditadoCombo = result
    });
    this.genericRestService.create<Contrato>(this._datos._dominio).subscribe(data => this.form(data));
    this.direccionFormulario()
  }

  direccionFormulario() {
    this.direccion = this.genericRestService.buildForm({
      dirTrabajo: [false, Validators.required],
      dirAdicional: [false, Validators.required],
      direccionPrincipal: ['', Validators.required],
      exterior: ['', Validators.required],
      interior: [''],
      cp: ['', Validators.required],
      colonia: ['', Validators.required],
      municipio: ['', Validators.required],
      entidad: ['', Validators.required]
    });
  }


  form(data?) {
    this.formulario = this.genericRestService.buildForm({
      regimenFiscal: ['PF'],
      fechaF: [''],
      nombres: [data ? data.nombres : ''],
      primerApellido: [data ? data.primerApellido : ''],
      segundoApellido: [data ? data.segundoApellido : ''],
      genero: [data ? data.genero : ''],
      edad: [data ? data.edad : ''],
      rfc: [data ? data.rfc : ''],
      fechaNacimiento: [data ? data.fechaNacimiento : ''],
      curp: [data ? data.curp : '', [Validators.maxLength(18), Validators.minLength(18)]],
      documentoOficial: [data ? data.documentoOficial : ''],
      claveElector: [data ? data.claveElector : '', [Validators.required, (control: AbstractControl) => Validators.minLength(this.longitud)(control), (control: AbstractControl) => Validators.maxLength(this.longitud)(control)]],
      telefonoFijo: [data ? data.telefonoFijo : ''],
      telefonoCelular: [data ? data.telefonoCelular : ''],
      telefonoOficina: [data ? data.telefonoOficina : ''],
      correoElectronico: [data ? data.correoElectronico : ''],
      nombresCoacreditado: [data ? data.nombresCoacreditado : ''],
      primerApellidoCoacreditado: [data ? data.primerApellidoCoacreditado : ''],
      segundoApellidoCoacreditado: [data ? data.segundoApellidoCoacreditado : ''],
      generoCoacreditado: [data ? data.generoCoacreditado : ''],
      edadCoacreditado: [data ? data.edadCoacreditado : ''],
      rfcCoacreditado: [data ? data.rfcCoacreditado : ''],
      fechaNacimientoCoacreditado: [data ? data.fechaNacimientoCoacreditado : ''],
      curpCoacreditado: [data ? data.curpCoacreditado : '',[Validators.maxLength(18), Validators.minLength(18)]],
      documentoOficialCoacreditado: [data ? data.documentoOficialCoacreditado : ''],
      claveElectorCoacreditado: [data ? data.claveElectorCoacreditado : '', [Validators.required, (control: AbstractControl) => Validators.minLength(this.longitud)(control), (control: AbstractControl) => Validators.maxLength(this.longitud)(control)]],
      telefonoFijoCoacreditado: [data ? data.telefonoFijoCoacreditado : ''],
      telefonoCelularCoacreditado: [data ? data.telefonoCelularCoacreditado : ''],
      telefonoOficinaCoacreditado: [data ? data.telefonoOficinaCoacreditado : ''],
      correoElectronicoCoacreditado: [data ? data.correoElectronicoCoacreditado : ''],
      direccion: [data ? data.direccion : ''],
      anio: [data ? data.anio : ''],
      marca: [data ? data.marca?.id : ''],
      modelo: [data ? data.modelo?.id : ''],
      versionAuto: [data ? data.versionAuto : ''],
      color: [data ? data.color : ''],
      placas: [data ? data.placas : ''],
      numeroDeMotor: [data ? data.numeroDeMotor : ''],
      numeroDeFactura: [data ? data.numeroDeFactura : ''],
      fechaDeFactura: [data ? data.fechaDeFactura : ''],
      emisoraDeFactura: [data ? data.emisoraDeFactura : ''],
      valorDeVenta: [data ? data.valorDeVenta : ''],
      valorDeCompra: [data ? data.valorDeCompra : ''],
      montoMaximoAutorizado: [data ? data.montoMaximoAutorizado : ''],
      numeroVin: [data ? data.numeroVin : ''],
      gps1: [data ? data.gps1 : ''],
      gps2: [data ? data.gps2 : ''],
      proveedor2: [data ? data.proveedor2 : ''],
      gps3: [data ? data.gps3 : ''],
      proveedor3: [data ? data.proveedor3 : ''],
      montoRequerido: [data ? data.montoRequerido : '', [Validators.required, Validators.min(20000), (control: AbstractControl) => Validators.max(Number((this.montoMaximoAutorizado).toFixed(2)))(control)]],
      costoMensualInteres: [data ? data.costoMensualInteres : this.costoMensualInteres],
      costoMensualMonitoreo: [data ? data.costoMensualMonitoreo : this.costoMensualMonitoreo],
      costoMensualGPS: [data ? data.costoMensualGPS : this.costoMensualGPS],
      totalAutoPresta: [data ? data.totalAutoPresta : this.totalAutoPresta],
      iva: [data ? data.iva : this.iva],
      costoMensualTotal: [data ? data.costoMensualTotal : this.costoMensualTotal],
      tipoContrato: [data ? data.tipoContrato : ''],
      referencia: [data ? data.referencia : ''],
      clabe: [data ? data.clabe : ''],
      coacreditado: [true],
      calificacionCliente: [data ? data.calificacionCliente : ''],
      contratoPrueba: [data ? data.contratoPrueba : ''],
      montoTransferencia: [data ? data.montoTransferencia : ''],
      detalleDescuentos: [data ? data.detalleDescuentos : ''],
      fechaSolicitud: [data ? data.fechaSolicitud : ''],
      fechaContrato: [data ? data.fechaContrato : ''],
      montoLiquidar: [data ? data.montoLiquidar : ''],
      fechaCompromiso: [data ? data.fechaCompromiso : ''],
      descuentosRetenciones: [data ? data.descuentosRetenciones : ''],
    });
  }

  save() {
    if (this.direcciones.length == 0) {
      this.addNewBeneficiario()
    }
    this.formulario.patchValue({direccion: this.direcciones})

    const dialogRef = this.dialog.open(GeneracionContratoComponent, {
      data: {title: 'nombre', disableClose: true, data: 'result', action: 'Editar'}, height: 'auto', width: '40%'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (!result) {
        return;
      }
      this.formulario.patchValue({
        calificacionCliente: result.calificacionCliente,
        contratoPrueba: result.contratoPrueba,
        montoTransferencia: result.montoTransferencia,
        detalleDescuentos: result.detalleDescuentos,
        fechaSolicitud: result.fechaSolicitud,
        fechaContrato: result.fechaContrato,
        montoLiquidar: result.montoLiquidar,
        fechaCompromiso: result.fechaCompromiso,
        referencia: result.referenciaBancariaBBVA,
        descuentosRetenciones: result.descuentosRetenciones,
      })
      this.genericRestService.save<Contrato>(this.formulario.value, {}, this._datos._dominio).subscribe(() => {
        this.ngOnInit();
        this.snack.open(this._datos._title + ' capturado!', 'OK', {duration: 4000});
      }, error => {
        this.snack.open(error.error.mensaje, 'OK', {duration: 4000});
      });
    });
  }

  calcular(monto: number) {
    this.costoMensualInteres = (monto * this.tasaInteres) / 100
    this.costoMensualMonitoreo = ((monto * this.tasaMonitoreo) / 100) < 800 ? 800 : ((monto * this.tasaMonitoreo) / 100)
    this.costoMensualGPS = ((monto * this.tasaGPS) / 100) < 600 ? 600 : ((monto * this.tasaGPS) / 100)
    this.totalAutoPresta = this.costoMensualInteres + this.costoMensualMonitoreo + this.costoMensualGPS
    this.iva = this.totalAutoPresta * 0.16
    this.costoMensualTotal = this.totalAutoPresta + this.iva

    this.formulario.patchValue({
      costoMensualInteres: (this.costoMensualInteres).toFixed(2), costoMensualMonitoreo: (this.costoMensualMonitoreo).toFixed(2),
      costoMensualGPS: (this.costoMensualGPS).toFixed(2), totalAutoPresta: (this.totalAutoPresta).toFixed(2), iva: (this.iva).toFixed(2),
      costoMensualTotal: (this.costoMensualTotal).toFixed(2),
    })
  }

  calcularMaximoAutorizado(monto: number) {
    this.montoMaximoAutorizado = monto * 0.7;
    this.formulario.patchValue({
      montoMaximoAutorizado: (this.montoMaximoAutorizado).toFixed(2)
    })
  }

  cargarModelos(value) {
    this.genericRestService.combo<Combo[]>({id: value}, 'comboModelos').subscribe(res => this.modelosCombo = res);
  }

  showNotification(colorName, text, placementFrom, placementAlign) {
    this.snack.open(text, '', {
      duration: 2000,
      verticalPosition: placementFrom,
      horizontalPosition: placementAlign,
      panelClass: colorName
    });
  }

  addNewBeneficiario() {
    this.direcciones.push({
      dirTrabajo: this.direccion.get('dirTrabajo').value,
      dirAdicional: this.direccion.get('dirAdicional').value,
      direccionPrincipal: this.direccion.get('direccionPrincipal').value,
      exterior: this.direccion.get('exterior').value,
      interior: this.direccion.get('interior').value,
      cp: this.direccion.get('cp').value,
      colonia: this.direccion.get('colonia').value,
      municipio: this.direccion.get('municipio').value,
      entidad: this.direccion.get('entidad').value
    });
    this.direccionFormulario();
  }


  eliminarBeneficiario(c: direccion) {
    const indice = this.direcciones.indexOf(c);
    this.direcciones.splice(indice, 1);
    this.direccionFormulario()
  }

  datos(d: string, coacreditado: boolean) {
    const data = d.split('');
    if (data.length == 18) {
      const a = Number(data[4])
      let complemento
      if (a >= 0 && a <= 4) {
        complemento = '20'
      } else {
        complemento = '19'
      }
      let anio = complemento + data[4] + data[5]
      const mes = data[6] + data[7]
      const dia = data[8] + data[9]

      let dateString = anio + '-' + mes + '-' + dia + 'T00:00:00'

      let newDate = new Date(dateString);
      let timeDiff = Math.abs(Date.now() - newDate.getTime());
      let age = Math.floor((timeDiff / (1000 * 3600 * 24)) / 365.25);
      if (coacreditado) {
        this.formulario.patchValue({
          generoCoacreditado: data[10] == 'H' ? 'M' : 'F',
          edadCoacreditado: age,
          fechaNacimientoCoacreditado: newDate
        })
      } else {
        this.formulario.patchValue({genero: data[10] == 'H' ? 'M' : 'F', edad: age, fechaNacimiento: newDate})
      }
    } else {
      if (coacreditado) {
        this.formulario.patchValue({
          generoCoacreditado: '',
          edadCoacreditado: '',
          fechaNacimientoCoacreditado: ''
        })
      } else {
        this.formulario.patchValue({genero: '', edad: '', fechaNacimiento: ''})
      }
    }
  }

  cargarDatos(value: string) {
      if (value.length == 5) {
        this.genericRestService.index<_comboCp>('Cp', {cp: value}, 'cargarDatos').subscribe(r => {
            this.direccion.patchValue({
              entidad: r[0].estado,
              municipio: r[0].municipio,
            })

            this.genericRestService.combo<Combo[]>({id: value}, 'comboColonias').subscribe(res => {
              this.coloniasCombo = res
              if (this.coloniasCombo.length == 1) {
                this.direccion.patchValue({
                  colonia: this.coloniasCombo[0].id
                })
              } else {
                this.showNotification('snackbar-success', 'Favor de seleccionar su colonia', 'bottom', 'center')
              }
            });
          }
        )
      } else {
        this.direccion.patchValue({
          entidad: '',
          municipio: '',
          colonia: ''
        })
      }

  }

  documentoOficialValidator(event, esCoacreditado) {
    for (let doc of this.documentoOficialCombo){
      if (doc.id == event) {
        if (esCoacreditado) {
          this.longitudCoacreditado = doc.longitud
        } else {
          this.longitud = doc.longitud
        }
      }
    }
  }

  fechaFactura(fecha: string){
    if (fecha.length == 10) {
      let campos = fecha.split('/')
      let anio = campos[2]
      const mes = campos[1]
      const dia = campos[0]

      let dateString = anio + '-' + mes + '-' + dia + 'T00:00:00'

      let newDate = new Date(dateString);
      this.formulario.patchValue({fechaDeFactura: newDate})
    }
  }
}
