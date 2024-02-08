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
  public cveDir = 1
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
  public direccion1: FormGroup;
  public direccion2: FormGroup;
  public direccion3: FormGroup;
  public direccion4: FormGroup;

  costoMensualInteres = 0
  costoMensualMonitoreo = 0
  costoMensualGPS = 0
  totalAutoPresta = 0
  iva = 0
  costoMensualTotal = 0

  tasaInteres = 5;
  tasaMonitoreo = 1;
  tasaGPS = 0.75;


  porcentajeMax = 70
  public longitud = 1
  public longitudCoacreditado = 1

  public respuesta: Contrato;
  public estadosCombo: Combo[];
  public keyword = 'descripcion';
  public coloniasCombo1: Combo[];
  public coloniasCombo2: Combo[];
  public coloniasCombo3: Combo[];
  public coloniasCombo4: Combo[];
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
    {id: '2022', descripcion: '2022'},
    {id: '2023', descripcion: '2023'},
    {id: '2024', descripcion: '2024'},
    {id: '2025', descripcion: '2025'},
    {id: '2026', descripcion: '2026'},
    {id: '2027', descripcion: '2027'},
    {id: '2028', descripcion: '2028'},
    {id: '2029', descripcion: '2029'},
    {id: '2030', descripcion: '2030'},

  ];
  public documentoOficialCombo: IdentificacionesOficiales[];
  public comboDir: Combo[];
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

    this.genericRestService.combo<any>({}, 'maxAutorizado').subscribe(result => {
      this.porcentajeMax = result.max
    });
    if (this.route.snapshot.params.id != undefined) {
      this.id = this.route.snapshot.params.id
      this.genericRestService.edit<any>(this.id, this._datos._dominio, {}, 'edicionContrato').subscribe(datos => {
        this.contratoEdicion = datos
        console.log(datos)
        this.documentoOficialValidator(datos.contrato.documentoOficial, false)
        this.calcularMaximoAutorizado(datos.contrato.valorDeCompra)
        if (datos.direcciones.length == 1) {
          this.cargarDatos(datos.direcciones[0].cp.toString(), 1)
          this.direccion1.patchValue({
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
            entidad: datos.direcciones[0].entidad,
            tipo: 'Domicilio casa cliente',
          })
        } else {
          console.log(datos.direcciones.length)
          if (datos.direcciones.length >= 1) {
            this.cargarDatos(datos.direcciones[0].cp.toString(), 1)
            this.direccion1.patchValue({
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
              entidad: datos.direcciones[0].entidad,
              tipo: 'Domicilio casa cliente',
            })
          }
          if (datos.direcciones.length >= 2) {
            this.cargarDatos(datos.direcciones[1].cp.toString(), 2)
            this.direccion2.patchValue({
              id: datos.direcciones[1].id,
              dirTrabajo: datos.direcciones[1].dirTrabajo,
              dirAdicional: datos.direcciones[1].dirAdicional,
              direccionPrincipal: datos.direcciones[1].direccionPrincipal,
              exterior: datos.direcciones[1].exterior,
              interior: datos.direcciones[1].interior,
              cp: (datos.direcciones[1].cp.toString()).length == 4 ? '0' + datos.direcciones[1].cp : datos.direcciones[1].cp,
              colonia: datos.direcciones[1].colonia,
              municipio: datos.direcciones[1].municipio,
              principal: datos.direcciones[1].principal,
              entidad: datos.direcciones[1].entidad,
              tipo: 'Domicilio donde se sitúa el auto',
            })
          }
          if (datos.direcciones.length >= 3) {
            this.cargarDatos(datos.direcciones[2].cp.toString(), 3)
            this.direccion3.patchValue({
              id: datos.direcciones[2].id,
              dirTrabajo: datos.direcciones[2].dirTrabajo,
              dirAdicional: datos.direcciones[2].dirAdicional,
              direccionPrincipal: datos.direcciones[2].direccionPrincipal,
              exterior: datos.direcciones[2].exterior,
              interior: datos.direcciones[2].interior,
              cp: (datos.direcciones[2].cp.toString()).length == 4 ? '0' + datos.direcciones[2].cp : datos.direcciones[2].cp,
              colonia: datos.direcciones[2].colonia,
              municipio: datos.direcciones[2].municipio,
              principal: datos.direcciones[2].principal,
              entidad: datos.direcciones[2].entidad,
              tipo: 'Domicilio laboral',
            })
          }
          if (datos.direcciones.length == 4) {
            this.cargarDatos(datos.direcciones[3].cp.toString(), 4)
            this.direccion4.patchValue({
              id: datos.direcciones[3].id,
              dirTrabajo: datos.direcciones[3].dirTrabajo,
              dirAdicional: datos.direcciones[3].dirAdicional,
              direccionPrincipal: datos.direcciones[3].direccionPrincipal,
              exterior: datos.direcciones[3].exterior,
              interior: datos.direcciones[3].interior,
              cp: (datos.direcciones[3].cp.toString()).length == 4 ? '0' + datos.direcciones[3].cp : datos.direcciones[3].cp,
              colonia: datos.direcciones[3].colonia,
              municipio: datos.direcciones[3].municipio,
              principal: datos.direcciones[3].principal,
              entidad: datos.direcciones[3].entidad,
              tipo: '2do Domicilio donde radica',
            })
          }
        }
        this.cargarModelos(datos.contrato.marca.id)
        if (datos.contrato.regimenFiscal.clave == "PM") {
          this.onEditPM(datos.contrato, datos.razonSocial)
        } else {

          this.onEdit(datos.contrato)
        }
      })
    }
  }

  direccionFormulario() {
    this.genericRestService.combo<Combo[]>({cve: this.cveDir}, 'comboDir').subscribe(res => this.comboDir = res);
    this.direccion1 = this.genericRestService.buildForm({
      dirTrabajo: [false],
      dirAdicional: [false],
      direccionPrincipal: ['', Validators.required],
      exterior: ['', Validators.required],
      interior: [''],
      cp: ['', Validators.required],
      colonia: ['', Validators.required],
      municipio: ['', Validators.required],
      entidad: ['', Validators.required],
      tipo: ['Domicilio casa cliente', Validators.required]
    });
    this.direccion2 = this.genericRestService.buildForm({
      dirTrabajo: [false],
      dirAdicional: [false],
      direccionPrincipal: ['', Validators.required],
      exterior: ['', Validators.required],
      interior: [''],
      cp: ['', Validators.required],
      colonia: ['', Validators.required],
      municipio: ['', Validators.required],
      entidad: ['', Validators.required],
      tipo: ['Domicilio donde se sitúa el auto', Validators.required]
    });
    this.direccion3 = this.genericRestService.buildForm({
      dirTrabajo: [false],
      dirAdicional: [false],
      direccionPrincipal: ['', Validators.required],
      exterior: ['', Validators.required],
      interior: [''],
      cp: ['', Validators.required],
      colonia: ['', Validators.required],
      municipio: ['', Validators.required],
      entidad: ['', Validators.required],
      tipo: ['Domicilio laboral', Validators.required]
    });
    this.direccion4 = this.genericRestService.buildForm({
      dirTrabajo: [false],
      dirAdicional: [false],
      direccionPrincipal: ['', Validators.required],
      exterior: ['', Validators.required],
      interior: [''],
      cp: ['', Validators.required],
      colonia: ['', Validators.required],
      municipio: ['', Validators.required],
      entidad: ['', Validators.required],
      tipo: ['2do Domicilio donde radica', Validators.required]
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
      tipoFolio: [data ? data.tipoContrato : '', Validators.required],
      referencia: [data ? data.referencia : ''],
      clabe: [data ? data.clabe : '', [Validators.required, Validators.minLength(18), Validators.maxLength(18)]],
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

    if (this.direcciones.length==0){
      this.formulario.patchValue({direccion: this.contratoEdicion.direcciones})
    }

    console.log(this.direcciones)
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
        numeroContrato: result.numeroContrato,
        tipoFolio: result.tipoContrato,
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
    this.montoMaximoAutorizado = monto * (this.porcentajeMax / 100);
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
    if (this.direccion1.valid==true) {
      this.direcciones.push({
        dirTrabajo: this.direccion1.get('dirTrabajo').value,
        dirAdicional: this.direccion1.get('dirAdicional').value,
        direccionPrincipal: this.direccion1.get('direccionPrincipal').value,
        exterior: this.direccion1.get('exterior').value,
        interior: this.direccion1.get('interior').value,
        cp: this.direccion1.get('cp').value,
        colonia: this.direccion1.get('colonia').value,
        municipio: this.direccion1.get('municipio').value,
        entidad: this.direccion1.get('entidad').value,
        tipo: this.direccion1.get('tipo').value
      });
    }
    if (this.direccion2.valid==true) {
      this.direcciones.push({
        dirTrabajo: this.direccion2.get('dirTrabajo').value,
        dirAdicional: this.direccion2.get('dirAdicional').value,
        direccionPrincipal: this.direccion2.get('direccionPrincipal').value,
        exterior: this.direccion2.get('exterior').value,
        interior: this.direccion2.get('interior').value,
        cp: this.direccion2.get('cp').value,
        colonia: this.direccion2.get('colonia').value,
        municipio: this.direccion2.get('municipio').value,
        entidad: this.direccion2.get('entidad').value,
        tipo: this.direccion2.get('tipo').value
      });
    }
    if (this.direccion3.valid==true) {
      this.direcciones.push({
        dirTrabajo: this.direccion3.get('dirTrabajo').value,
        dirAdicional: this.direccion3.get('dirAdicional').value,
        direccionPrincipal: this.direccion3.get('direccionPrincipal').value,
        exterior: this.direccion3.get('exterior').value,
        interior: this.direccion3.get('interior').value,
        cp: this.direccion3.get('cp').value,
        colonia: this.direccion3.get('colonia').value,
        municipio: this.direccion3.get('municipio').value,
        entidad: this.direccion3.get('entidad').value,
        tipo: this.direccion3.get('tipo').value
      });
    }
    if (this.direccion4.valid==true) {
      this.direcciones.push({
        dirTrabajo: this.direccion4.get('dirTrabajo').value,
        dirAdicional: this.direccion4.get('dirAdicional').value,
        direccionPrincipal: this.direccion4.get('direccionPrincipal').value,
        exterior: this.direccion4.get('exterior').value,
        interior: this.direccion4.get('interior').value,
        cp: this.direccion4.get('cp').value,
        colonia: this.direccion4.get('colonia').value,
        municipio: this.direccion4.get('municipio').value,
        entidad: this.direccion4.get('entidad').value,
        tipo: this.direccion4.get('tipo').value
      });
    }
    this.direccionFormulario();
  }

  eliminarDireccion(c: direccion) {
    this.direccionFormulario()
    const indice = this.direcciones.indexOf(c);
    this.direcciones.splice(indice, 1);
    this.cveDir--;
    this.direccionFormulario()
  }

  editarDireccion(c: direccion) {
    const indice = this.direcciones.indexOf(c);
    this.cargarDatos(c.cp.toString(), indice+1)
    this.direccion1.patchValue({
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
      entidad: c.entidad,
      tipo: c.tipo
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
          generoCoacreditado: data[10] == 'H'|| data[10] == 'h' ? 'M' : 'F',
          edadCoacreditado: age,
          fechaNacimientoCoacreditado: newDate
        })
      } else {
        this.formulario.patchValue({genero: data[10] == 'H'|| data[10] == 'h' ? 'M' : 'F', edad: age, fechaNacimiento: newDate})
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

  cargarDatos(value: string, form: number) {
    console.log('Codigo ' + value + ' del formulario: ' + form)
    switch (form){
      case 0:
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
        break
      case 1:
        if (value.length == 5) {
          this.genericRestService.index<_comboCp>('Cp', {cp: value}, 'cargarDatos').subscribe(r => {
              this.direccion1.patchValue({ entidad: r[0].estado, municipio: r[0].municipio })
              this.genericRestService.combo<Combo[]>({id: value}, 'comboColonias').subscribe(res => {
                this.coloniasCombo1 = res
                if (this.coloniasCombo1.length == 1) {
                  this.direccion1.patchValue({ colonia: this.coloniasCombo1[0].id })
                } else {
                  this.showNotification('snackbar-success', 'Favor de seleccionar su colonia', 'bottom', 'center')
                }
              });
            }
          )
        } else {
          this.direccion1.patchValue({
            entidad: '',
            municipio: '',
            colonia: ''
          })
        }
        break
      case 2:
        if (value.length == 5) {
          this.genericRestService.index<_comboCp>('Cp', {cp: value}, 'cargarDatos').subscribe(r => {
              this.direccion2.patchValue({ entidad: r[0].estado, municipio: r[0].municipio })
              this.genericRestService.combo<Combo[]>({id: value}, 'comboColonias').subscribe(res => {
                this.coloniasCombo2 = res
                if (this.coloniasCombo2.length == 1) {
                  this.direccion2.patchValue({ colonia: this.coloniasCombo2[0].id })
                } else {
                  this.showNotification('snackbar-success', 'Favor de seleccionar su colonia', 'bottom', 'center')
                }
              });
            }
          )
        } else {
          this.direccion2.patchValue({
            entidad: '',
            municipio: '',
            colonia: ''
          })
        }
        break
      case 3:
        if (value.length == 5) {
          this.genericRestService.index<_comboCp>('Cp', {cp: value}, 'cargarDatos').subscribe(r => {
              this.direccion3.patchValue({ entidad: r[0].estado, municipio: r[0].municipio })
              this.genericRestService.combo<Combo[]>({id: value}, 'comboColonias').subscribe(res => {
                this.coloniasCombo3 = res
                if (this.coloniasCombo3.length == 1) {
                  this.direccion3.patchValue({ colonia: this.coloniasCombo3[0].id })
                } else {
                  this.showNotification('snackbar-success', 'Favor de seleccionar su colonia', 'bottom', 'center')
                }
              });
            }
          )
        } else {
          this.direccion3.patchValue({
            entidad: '',
            municipio: '',
            colonia: ''
          })
        }
        break
      case 4:
        if (value.length == 5) {
          this.genericRestService.index<_comboCp>('Cp', {cp: value}, 'cargarDatos').subscribe(r => {
              this.direccion4.patchValue({ entidad: r[0].estado, municipio: r[0].municipio })
              this.genericRestService.combo<Combo[]>({id: value}, 'comboColonias').subscribe(res => {
                this.coloniasCombo4 = res
                if (this.coloniasCombo4.length == 1) {
                  this.direccion4.patchValue({ colonia: this.coloniasCombo4[0].id })
                } else {
                  this.showNotification('snackbar-success', 'Favor de seleccionar su colonia', 'bottom', 'center')
                }
              });
            }
          )
        } else {
          this.direccion4.patchValue({
            entidad: '',
            municipio: '',
            colonia: ''
          })
        }
        break
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
  onEditPM(datos, rs) {
    this.formulario.patchValue({
      regimenFiscal: datos.regimenFiscal.clave,
      razonSocialMoral: rs.razonSocial,
      rfcMoral: rs.rfc,
      telefonoFijoMoral: rs.telefonoFijo,
      telefonoCelularMoral: rs.telefonoCelular,
      telefonoOficinaMoral: rs.telefonoOficina,
      calleDireccionFiscalMoral: rs.calleDireccionFiscal,
      numeroExteriorMoral: rs.numeroExterior,
      numeroInteriorMoral: rs.numeroInterior,
      codigoPostalMoral: rs.codigoPostal,
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

  back() {
    this,this.router.navigate(['Consultas/Contrataciones'])
  }
}
