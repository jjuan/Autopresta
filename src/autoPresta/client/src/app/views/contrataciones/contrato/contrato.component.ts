import {Component, OnInit} from '@angular/core';
import {
  _comboCp,
  Combo,
  Contrato,
  direccion,
  Gps,
  IdentificacionesOficiales,
  Marcas,
  Proveedores
} from "../../../core/models/data.interface";
import {AbstractControl, FormGroup, Validators} from "@angular/forms";
import {GlobalService} from "../../../core/service/global.service";
import {MatDialog} from "@angular/material/dialog";
import {ActivatedRoute, Router} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {RestService} from "../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";
import {
  GeneracionContratoComponent
} from "../contrataciones-componentes/generacion-contrato/generacion-contrato.component";
import {number} from "ngx-custom-validators/src/app/number/validator";

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
  id;
  contratoEdicion;

  constructor(
    private globalService: GlobalService, private genericRestService: RestService, private activatedroute: ActivatedRoute, private router: Router,
    private snack: MatSnackBar, public dialog: MatDialog, private dateAdapter: DateAdapter<Date>, private route: ActivatedRoute
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

    if (this.route.snapshot.params.id != undefined) {
      this.id = this.route.snapshot.params.id
      this.genericRestService.edit<any>(this.id, this._datos._dominio, {}, 'edicionContrato').subscribe(datos => {
        this.contratoEdicion = datos
        this.documentoOficialValidator(datos.contrato.documentoOficial, false)
        this.calcularMaximoAutorizado(datos.contrato.valorDeCompra)
        if (datos.direcciones.length == 1) {
          this.cargarDatos((datos.direcciones[0].cp.toString()).length == 4 ? '0' + datos.direcciones[0].cp : datos.direcciones[0].cp, false)
          this.direccion.patchValue({
            id: datos.direcciones[0].id,
            dirTrabajo: datos.direcciones[0].dirTrabajo,
            dirAdicional: datos.direcciones[0].dirAdicional,
            direccionPrincipal: datos.direcciones[0].direccionPrincipal,
            exterior: datos.direcciones[0].exterior,
            interior: datos.direcciones[0].interior,
            cp: (datos.direcciones[0].cp.toString()).length == 4 ? '0' + datos.direcciones[0].cp : datos.direcciones[0].cp,
            colonia: datos.direcciones[0].colonia,
            municipio: datos.direcciones[0].municipio,
            principal: datos.direcciones[0].principal,
            entidad: datos.direcciones[0].entidad
          })
        } else {
          for (let dir of datos.direcciones) {
            this.direcciones.push({
              id: dir.id,
              dirTrabajo: dir.dirTrabajo,
              dirAdicional: dir.dirAdicional,
              direccionPrincipal: dir.direccionPrincipal,
              exterior: dir.exterior,
              interior: dir.interior,
              cp: (dir.cp.toString()).length == 4 ? '0' + dir.cp : dir.cp,
              colonia: dir.colonia,
              municipio: dir.municipio,
              entidad: dir.entidad,
              principal: dir.principal
            });
          }
        }
        this.cargarModelos(datos.contrato.marca.id)
        this.onEdit(datos.contrato)
      })
    }
  }

  direccionFormulario() {
    this.direccion = this.genericRestService.buildForm({
      id: [''],
      dirTrabajo: [false, Validators.required],
      dirAdicional: [false, Validators.required],
      direccionPrincipal: ['', Validators.required],
      exterior: ['', Validators.required],
      interior: [''],
      cp: ['', Validators.required],
      colonia: ['', Validators.required],
      municipio: ['', Validators.required],
      principal: [''],
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
      fechaF: [''],
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
      documentoOficial: [data ? data.documentoOficial : '', Validators.required],
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
      clabe: [data ? data.clabe : ''],
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
      this.agregarDireccion()
    }
    this.formulario.patchValue({direccion: this.direcciones})
    const dialogRef = this.dialog.open(GeneracionContratoComponent, {
      data: {
        title: 'nombre', disableClose: true, data: {
          calificacionCliente: this.contratoEdicion.contrato.calificacionCliente.id,
          fechaContrato: this.contratoEdicion.contrato.fechaContrato,
          contratoPrueba: this.contratoEdicion.contrato.contratoPrueba,
          contratoMonterrey: this.contratoEdicion.contrato.contratoMonterrey,
          numeroContrato: this.contratoEdicion.contrato.numeroContrato,
          referenciaBancariaBBVA: this.contratoEdicion.contrato.referenciaBancariaBBVA,
          montoTransferencia: this.contratoEdicion.contrato.montoTransferencia,
          descuentosRetenciones: this.contratoEdicion.contrato.descuentosRetenciones,
          fechaSolicitud: this.contratoEdicion.contrato.fechaSolicitud,
          montoLiquidar: this.contratoEdicion.contrato.montoLiquidar,
          fechaCompromiso: this.contratoEdicion.contrato.fechaCompromiso,
          tipoContrato: this.contratoEdicion.contrato.tipoFolio,
          detalleDescuentos: this.contratoEdicion.contrato.detalleDescuentos,
        }, action: 'Editar'
      }, height: 'auto', width: '40%'
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
      this.genericRestService.update<Contrato>(this.contratoEdicion.contrato.id, this.formulario.value, this._datos._dominio).subscribe(data => {
        // this.ngOnInit();
        this.snack.open(data['message'], 'OK', {duration: 4000});
        this.router.navigate(['Consultas/Contrataciones'])
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
      costoMensualInteres: this.costoMensualInteres, costoMensualMonitoreo: this.costoMensualMonitoreo,
      costoMensualGPS: this.costoMensualGPS, totalAutoPresta: this.totalAutoPresta, iva: this.iva,
      costoMensualTotal: this.costoMensualTotal,
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


  agregarDireccion() {
    this.direcciones.push({
      id: this.direccion.get('id').value,
      dirTrabajo: this.direccion.get('dirTrabajo').value,
      dirAdicional: this.direccion.get('dirAdicional').value,
      direccionPrincipal: this.direccion.get('direccionPrincipal').value,
      exterior: this.direccion.get('exterior').value,
      interior: this.direccion.get('interior').value,
      cp: this.direccion.get('cp').value,
      colonia: this.direccion.get('colonia').value,
      municipio: this.direccion.get('municipio').value,
      entidad: this.direccion.get('entidad').value,
      principal: this.direccion.get('principal').value
    });
    this.direccionFormulario();
  }

  eliminarDireccion(c: direccion) {
    this.direccionFormulario()
    const indice = this.direcciones.indexOf(c);
    this.direcciones.splice(indice, 1);
    this.direccionFormulario()
  }

  editarDireccion(c: direccion) {
    const indice = this.direcciones.indexOf(c);
    this.cargarDatos(c.cp.toString(), false)
    this.direccion.patchValue({
      id: c.id,
      dirTrabajo: c.dirTrabajo,
      dirAdicional: c.dirAdicional,
      direccionPrincipal: c.direccionPrincipal,
      exterior: c.exterior,
      interior: c.interior,
      cp: c.cp,
      colonia: c.colonia,
      municipio: c.municipio,
      principal: c.principal,
      entidad: c.entidad
    });
    this.direcciones.splice(indice, 1);
  }

  datos(d, coacreditado: boolean) {
    const data = (d.toString()).split('');
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

  cargarDatos(value, moral: boolean) {
    if (moral) {
      if ((value.toString()).length == 5) {
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
      if ((value.toString()).length == 5) {
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

  documentoOficialValidator(event, esCoacreditado) {
    for (let doc of this.documentoOficialCombo) {
      if (doc.id == event) {
        if (esCoacreditado) {
          this.longitudCoacreditado = doc.longitud
        } else {
          this.longitud = doc.longitud
        }
      }
    }
  }

  onEdit(datos) {
    this.formulario.patchValue({
      regimenFiscal: datos.regimenFiscal.clave,
      razonSocialMoral: datos.razonSocialMoral,
      rfcMoral: datos.rfcMoral,
      telefonoFijoMoral: datos.telefonoFijoMoral,
      telefonoCelularMoral: datos.telefonoCelularMoral,
      telefonoOficinaMoral: datos.telefonoOficinaMoral,
      calleDireccionFiscalMoral: datos.calleDireccionFiscalMoral,
      numeroExteriorMoral: datos.numeroExteriorMoral,
      numeroInteriorMoral: datos.numeroInteriorMoral,
      codigoPostalMoral: datos.codigoPostalMoral,
      fechaF: datos.fechaF,
      coloniaMoral: datos.coloniaMoral,
      municipioMoral: datos.municipioMoral,
      entidadMoral: datos.entidadMoral,
      nombres: datos.nombres,
      primerApellido: datos.primerApellido,
      segundoApellido: datos.segundoApellido,
      genero: datos.genero,
      edad: datos.edad,
      rfc: datos.rfc,
      fechaNacimiento: datos.fechaNacimiento,
      curp: datos.curp,
      documentoOficial: Number(datos.documentoOficial),
      claveElector: datos.claveElector,
      telefonoFijo: datos.telefonoFijo,
      telefonoCelular: datos.telefonoCelular,
      telefonoOficina: datos.telefonoOficina,
      correoElectronico: datos.correoElectronico,
      nombresCoacreditado: datos.nombresCoacreditado,
      primerApellidoCoacreditado: datos.primerApellidoCoacreditado,
      segundoApellidoCoacreditado: datos.segundoApellidoCoacreditado,
      generoCoacreditado: datos.generoCoacreditado,
      edadCoacreditado: datos.edadCoacreditado,
      rfcCoacreditado: datos.rfcCoacreditado,
      fechaNacimientoCoacreditado: datos.fechaNacimientoCoacreditado,
      curpCoacreditado: datos.curpCoacreditado,
      claveElectorCoacreditado: datos.claveElectorCoacreditado,
      telefonoFijoCoacreditado: datos.telefonoFijoCoacreditado,
      telefonoCelularCoacreditado: datos.telefonoCelularCoacreditado,
      telefonoOficinaCoacreditado: datos.telefonoOficinaCoacreditado,
      correoElectronicoCoacreditado: datos.correoElectronicoCoacreditado,
      direccion: datos.direccion,
      anio: datos.anio,
      marca: datos.marca.id,
      modelo: datos.modelo.id,
      versionAuto: datos.numeroVin,
      color: datos.color,
      placas: datos.placas,
      numeroDeMotor: datos.numeroDeMotor,
      numeroDeFactura: datos.numeroDeFactura,
      fechaDeFactura: datos.fechaDeFactura,
      emisoraDeFactura: datos.emisoraDeFactura,
      valorDeVenta: datos.valorDeVenta,
      valorDeCompra: datos.valorDeCompra,
      montoMaximoAutorizado: datos.montoMaximoAutorizado,
      numeroVin: datos.numeroVin,
      gps1: datos.gps1 ? datos.gps1.id : '',
      gps2: datos.gps2 ? datos.gps2.id : '',
      proveedor2: datos.proveedor2,
      gps3: datos.gps3 ? datos.gps3.id : '',
      proveedor3: datos.proveedor3,
      montoRequerido: datos.montoRequerido,
      costoMensualInteres: datos.costoMensualInteres,
      costoMensualMonitoreo: datos.costoMensualMonitoreo,
      costoMensualGPS: datos.costoMensualGPS,
      totalAutoPresta: datos.totalAutoPresta,
      iva: datos.iva,
      costoMensualTotal: datos.costoMensualTotal,
      tipoContrato: datos.tipoContrato.id,
      referencia: datos.referencia,
      clabe: datos.clabe,
      coacreditado: datos.coacreditado,
      calificacionCliente: datos.calificacionCliente,
      contratoPrueba: datos.contratoPrueba,
      montoTransferencia: datos.montoTransferencia,
      detalleDescuentos: datos.detalleDescuentos,
      fechaSolicitud: datos.fechaSolicitud,
      fechaContrato: datos.fechaContrato,
      montoLiquidar: datos.montoLiquidar,
      fechaCompromiso: datos.fechaCompromiso,
      descuentosRetenciones: datos.descuentosRetenciones,
    })
  }
}
