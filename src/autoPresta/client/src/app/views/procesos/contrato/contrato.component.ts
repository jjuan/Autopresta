import {Component, OnInit} from '@angular/core';
import {_comboCp, Combo, Contrato, direccion, Gps, Marcas, Proveedores} from "../../../core/models/data.interface";
import {AbstractControl, FormGroup, Validators} from "@angular/forms";
import {GlobalService} from "../../../core/service/global.service";
import {MatDialog} from "@angular/material/dialog";
import {ActivatedRoute} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {RestService} from "../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";
import {GeneracionContratoComponent} from "./generacion-contrato/generacion-contrato.component";

@Component({
  selector: 'app-contrato',
  templateUrl: './contrato.component.html',
  styleUrls: ['./contrato.component.sass']
})
export class ContratoComponent implements OnInit {
  public _datos = {
    _title: 'Contrato', _modulo: 'Procesos', _icono: 'fas fa-desktop', _dominio: 'Contrato', _componente: 'Contrato'
  };

  montoMaximoAutorizado = 0
  public direcciones = [];
  public formulario: FormGroup;
  public gps1Combo: Combo[];
  public provedores1Combo: Combo[];
  public gps2Combo: Combo[];
  public provedores2Combo: Combo[];
  public gps3Combo: Combo[];
  public provedores3Combo: Combo[];
  public tipoContratoCombo: Combo[];
  public regimenFiscalCombo: Combo[];
  public modelosCombo: Combo[];
  public marcasCombo: Combo[];
  direccion: FormGroup;

  costoMensualInteres = 0
  costoMensualMonitoreo = 0
  costoMensualGPS = 0
  totalAutoPresta = 0
  iva = 0
  costoMensualTotal = 0

  tasaInteres = 5;
  tasaMonitoreo = 1;
  tasaGPS = 0.75;

  respuesta: Contrato;
  public estadosCombo: Combo[];
  keyword = 'descripcion';
  coloniasCombo: Combo[];
  public coloniasMoralCombo: Combo[];
  coacreditado = false
  public aniosCombo: Combo[];

  constructor(
    private globalService: GlobalService, private genericRestService: RestService, private activatedroute: ActivatedRoute,
    private snack: MatSnackBar, public dialog: MatDialog, private dateAdapter: DateAdapter<Date>
  ) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
  }

  ngOnInit() {
    this.form()
    this.direcciones = []
    this.costoMensualInteres = 0
    this.costoMensualMonitoreo = 0
    this.costoMensualGPS = 0
    this.totalAutoPresta = 0
    this.iva = 0
    this.costoMensualTotal = 0
    this.genericRestService.combo<Combo[]>({id: 'Marcas'}, 'comboAutoPresta').subscribe(res => this.marcasCombo = res);
    this.genericRestService.combo<Combo[]>({id: 'Gps'}, 'comboAutoPresta').subscribe(result => this.gps1Combo = result);
    this.genericRestService.combo<Combo[]>({id: 'Proveedores'}, 'comboAutoPresta').subscribe(result => this.provedores1Combo = result);
    this.genericRestService.combo<Combo[]>({id: 'Gps'}, 'comboAutoPresta').subscribe(result => this.gps2Combo = result);
    this.genericRestService.combo<Combo[]>({id: 'Proveedores'}, 'comboAutoPresta').subscribe(result => this.provedores2Combo = result);
    this.genericRestService.combo<Combo[]>({id: 'Gps'}, 'comboAutoPresta').subscribe(result => this.gps3Combo = result);
    this.genericRestService.combo<Combo[]>({id: 'Proveedores'}, 'comboAutoPresta').subscribe(result => this.provedores3Combo = result);
    this.genericRestService.combo<Combo[]>({id: 'TipoContrato'}, 'comboAutoPresta').subscribe(result => this.tipoContratoCombo = result);
    this.genericRestService.combo<Combo[]>({id: 'C_RegimenFiscal'}, 'comboFactura').subscribe(result => this.regimenFiscalCombo = result);
    this.genericRestService.combo<Combo[]>({id: 'Estados'}, 'comboAutoPresta').subscribe(result => {
      this.estadosCombo = result
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

  formRazonSocial(value: any) {
    if (value == 'PM') {
      this.formulario.get('razonSocialMoral').setValidators(Validators.required)
      this.formulario.get('rfcMoral').setValidators(Validators.required)
      this.formulario.get('telefonoFijoMoral').setValidators(Validators.required)
      this.formulario.get('telefonoCelularMoral').setValidators(Validators.required)
      this.formulario.get('telefonoOficinaMoral').setValidators(Validators.required)
      this.formulario.get('calleDireccionFiscalMoral').setValidators(Validators.required)
      this.formulario.get('codigoPostalMoral').setValidators(Validators.required)
      this.formulario.get('coloniaMoral').setValidators(Validators.required)
      this.formulario.get('municipioMoral').setValidators(Validators.required)
      this.formulario.get('entidadMoral').setValidators(Validators.required)
    } else {
      this.formulario.get('razonSocialMoral').clearValidators();
      this.formulario.get('rfcMoral').clearValidators();
      this.formulario.get('telefonoFijoMoral').clearValidators();
      this.formulario.get('telefonoCelularMoral').clearValidators();
      this.formulario.get('telefonoOficinaMoral').clearValidators();
      this.formulario.get('calleDireccionFiscalMoral').clearValidators();
      this.formulario.get('codigoPostalMoral').clearValidators();
      this.formulario.get('coloniaMoral').clearValidators();
      this.formulario.get('municipioMoral').clearValidators();
      this.formulario.get('entidadMoral').clearValidators();
    }
  }


  form(data?) {
    this.formulario = this.genericRestService.buildForm({
      regimenFiscal: [data ? data.regimenFiscal : '', Validators.required],
      razonSocialMoral: [''],
      rfcMoral: [''],
      telefonoFijoMoral: [''],
      telefonoCelularMoral: [''],
      telefonoOficinaMoral: [''],
      calleDireccionFiscalMoral: [''],
      numeroExteriorMoral: [''],
      numeroInteriorMoral: [''],
      codigoPostalMoral: [''],
      coloniaMoral: [''],
      municipioMoral: [''],
      entidadMoral: [''],
      nombres: [data ? data.nombres : ''],
      primerApellido: [data ? data.primerApellido : ''],
      segundoApellido: [data ? data.segundoApellido : ''],
      genero: [data ? data.genero : ''],
      edad: [data ? data.edad : ''],
      rfc: [data ? data.rfc : '', Validators.required],
      fechaNacimiento: [data ? data.fechaNacimiento : ''],
      curp: [data ? data.curp : '', [Validators.required, Validators.maxLength(18), Validators.minLength(18)]],
      claveElector: [data ? data.claveElector : '', Validators.required],
      telefonoFijo: [data ? data.telefonoFijo : ''],
      telefonoCelular: [data ? data.telefonoCelular : ''],
      telefonoOficina: [data ? data.telefonoOficina : ''],
      correoElectronico: [data ? data.correoElectronico : '', Validators.required],
      nombresCoacreditado: [data ? data.nombresCoacreditado : ''],
      primerApellidoCoacreditado: [data ? data.primerApellidoCoacreditado : ''],
      segundoApellidoCoacreditado: [data ? data.segundoApellidoCoacreditado : ''],
      generoCoacreditado: [data ? data.generoCoacreditado : ''],
      edadCoacreditado: [data ? data.edadCoacreditado : ''],
      rfcCoacreditado: [data ? data.rfcCoacreditado : ''],
      fechaNacimientoCoacreditado: [data ? data.fechaNacimientoCoacreditado : ''],
      curpCoacreditado: [data ? data.curpCoacreditado : ''],
      claveElectorCoacreditado: [data ? data.claveElectorCoacreditado : ''],
      telefonoFijoCoacreditado: [data ? data.telefonoFijoCoacreditado : ''],
      telefonoCelularCoacreditado: [data ? data.telefonoCelularCoacreditado : ''],
      telefonoOficinaCoacreditado: [data ? data.telefonoOficinaCoacreditado : ''],
      correoElectronicoCoacreditado: [data ? data.correoElectronicoCoacreditado : ''],
      direccion: [data ? data.direccion : ''],
      anio: [data ? data.anio : '', Validators.required],
      marca: [data ? data.marca?.id : '', Validators.required],
      modelo: [data ? data.modelo?.id : '', Validators.required],
      versionAuto: [data ? data.versionAuto : '', Validators.required],
      color: [data ? data.color : '', Validators.required],
      placas: [data ? data.placas : '', Validators.required],
      numeroDeMotor: [data ? data.numeroDeMotor : '', Validators.required],
      numeroDeFactura: [data ? data.numeroDeFactura : '', Validators.required],
      fechaDeFactura: [data ? data.fechaDeFactura : '', Validators.required],
      emisoraDeFactura: [data ? data.emisoraDeFactura : '', Validators.required],
      valorDeVenta: [data ? data.valorDeVenta : '', Validators.required],
      valorDeCompra: [data ? data.valorDeCompra : '', Validators.required],
      montoMaximoAutorizado: [data ? data.montoMaximoAutorizado : '', Validators.required],
      numeroVin: [data ? data.numeroVin : '', Validators.required],
      gps1: [data ? data.gps1 : '', Validators.required],
      proveedor1: [data ? data.proveedor1 : '', Validators.required],
      gps2: [data ? data.gps2 : ''],
      proveedor2: [data ? data.proveedor2 : ''],
      gps3: [data ? data.gps3 : ''],
      proveedor3: [data ? data.proveedor3 : ''],
      montoRequerido: [data ? data.montoRequerido : '', [Validators.required, Validators.min(20000), (control: AbstractControl) => Validators.max(this.montoMaximoAutorizado)(control)]],
      costoMensualInteres: [data ? data.costoMensualInteres : this.costoMensualInteres, Validators.required],
      costoMensualMonitoreo: [data ? data.costoMensualMonitoreo : this.costoMensualMonitoreo, Validators.required],
      costoMensualGPS: [data ? data.costoMensualGPS : this.costoMensualGPS, Validators.required],
      totalAutoPresta: [data ? data.totalAutoPresta : this.totalAutoPresta, Validators.required],
      iva: [data ? data.iva : this.iva, Validators.required],
      costoMensualTotal: [data ? data.costoMensualTotal : this.costoMensualTotal, Validators.required],
      tipoContrato: [data ? data.tipoContrato : '', Validators.required],
      referencia: [data ? data.referencia : ''],
      clabe: [data ? data.clabe : '', Validators.required],
      coacreditado: [false],
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
      console.log(result)
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
      this.genericRestService.save<Contrato>(this.formulario.value, {}, this._datos._dominio).subscribe(data => {
        this.download(data.id)
        this.snack.open(this._datos._title + ' capturado!', 'OK', {duration: 4000});
      }, error => {
        this.snack.open(error.error.mensaje, 'OK', {duration: 4000});
      });
      this.ngOnInit();
    });
  }

  download(id: number) {
    const _dominio = 'Reporte'
    const _observable = this.genericRestService.getReport('contratoAutoPresta', _dominio, {id: id});
    return this.genericRestService.printReport(_observable, 'Contrato AP #1');
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

  calcularMaximoAutorizado(monto: number) {
    this.montoMaximoAutorizado = monto * 0.7;
    this.formulario.patchValue({
      montoMaximoAutorizado: this.montoMaximoAutorizado
    })
  }

  cargarModelos(value) {
    this.genericRestService.combo<Combo[]>({id: value}, 'comboModelos').subscribe(res => this.modelosCombo = res);
  }

  cargarAnios(value) {
    this.genericRestService.combo<Combo[]>({id: value}, 'comboAutos').subscribe(res => this.aniosCombo = res);
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
    console.log(d)
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
    }else {
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

  cargarDatos(value: string, moral: boolean) {
    if (moral) {
      if (value.length == 5) {
        this.genericRestService.index<_comboCp>('Cp', {cp: value}, 'cargarDatos').subscribe(r => {
            this.formulario.patchValue({
              entidadMoral: r[0].estado,
              municipioMoral: r[0].municipio,
            })

            this.genericRestService.combo<Combo[]>({id: value}, 'comboColonias').subscribe(res => {
              this.coloniasMoralCombo = res
              if (this.coloniasMoralCombo.length == 1) {
                this.formulario.patchValue({
                  coloniaMoral: this.coloniasMoralCombo[0].id
                })
              } else {
                this.showNotification('snackbar-success', 'Favor de seleccionar su colonia', 'bottom', 'center')
              }
            });
          }
        )
      } else {
        this.formulario.patchValue({
          entidadMoral: '',
          municipioMoral: '',
          coloniaMoral: ''
        })
      }

    } else {
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
  }

  coacreditadoform() {
    this.coacreditado = this.formulario.get('coacreditado').value
    if (this.coacreditado == false) {
      this.formulario.patchValue({
        nombresCoacreditado: '',
        primerApellidoCoacreditado: '',
        segundoApellidoCoacreditado: '',
        generoCoacreditado: '',
        edadCoacreditado: '',
        rfcCoacreditado: '',
        fechaNacimientoCoacreditado: '',
        curpCoacreditado: '',
        claveElectorCoacreditado: '',
        telefonoFijoCoacreditado: '',
        telefonoCelularCoacreditado: '',
        telefonoOficinaCoacreditado: '',
        correoElectronicoCoacreditado: '',
      })
    //   this.formulario.get('nombresCoacreditado').clearValidators();
    //   this.formulario.get('primerApellidoCoacreditado').clearValidators();
    //   // this.formulario.get('segundoApellidoCoacreditado').clearValidators();
    //   this.formulario.get('generoCoacreditado').clearValidators();
    //   this.formulario.get('edadCoacreditado').clearValidators();
    //   this.formulario.get('rfcCoacreditado').clearValidators();
    //   this.formulario.get('fechaNacimientoCoacreditado').clearValidators();
    //   this.formulario.get('curpCoacreditado').clearValidators();
    //   this.formulario.get('claveElectorCoacreditado').clearValidators();
    //   // this.formulario.get('telefonoFijoCoacreditado').clearValidators();
    //   // this.formulario.get('telefonoCelularCoacreditado').clearValidators();
    //   // this.formulario.get('telefonoOficinaCoacreditado').clearValidators();
    //   this.formulario.get('correoElectronicoCoacreditado').clearValidators();
    // } else {
    //
    //   this.formulario.get('nombresCoacreditado').setValidators(Validators.required);
    //   this.formulario.get('primerApellidoCoacreditado').setValidators(Validators.required);
    //   // this.formulario.get('segundoApellidoCoacreditado').setValidators(Validators.required);
    //   this.formulario.get('generoCoacreditado').setValidators(Validators.required);
    //   this.formulario.get('edadCoacreditado').setValidators(Validators.required);
    //   this.formulario.get('rfcCoacreditado').setValidators(Validators.required);
    //   this.formulario.get('fechaNacimientoCoacreditado').setValidators(Validators.required);
    //   this.formulario.get('curpCoacreditado').setValidators(Validators.required);
    //   this.formulario.get('claveElectorCoacreditado').setValidators(Validators.required);
    //   // this.formulario.get('telefonoFijoCoacreditado').setValidators(Validators.required);
    //   // this.formulario.get('telefonoCelularCoacreditado').setValidators(Validators.required);
    //   // this.formulario.get('telefonoOficinaCoacreditado').setValidators(Validators.required);
    //   this.formulario.get('correoElectronicoCoacreditado').setValidators(Validators.required);
    }
  }

  pruebas() {
    console.log(this.formulario.errors)
    console.log(this.formulario.value)
    console.log(this.formulario)
    console.log(this.direccion)
    console.log(this.direccion.value)
    console.log(this.direccion.errors)
    console.log(this.direcciones.length)
  }
}
