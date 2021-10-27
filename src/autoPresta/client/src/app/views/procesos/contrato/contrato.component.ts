import {Component, OnInit} from '@angular/core';
import {
  _comboCp,
  Combo,
  Contrato,
  direccion,
  Gps,
  Marcas,
  Modelos,
  Proveedores
} from "../../../core/models/data.interface";
import {AbstractControl, FormGroup, Validators} from "@angular/forms";
import {GlobalService} from "../../../core/service/global.service";
import {MatDialog} from "@angular/material/dialog";
import {ActivatedRoute} from "@angular/router";
import {MatSnackBar} from "@angular/material/snack-bar";
import {RestService} from "../../../core/service/rest.service";
import {DateAdapter} from "@angular/material/core";

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
  public codigoPostalCombo: _comboCp[] = [];
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
    this.genericRestService.combo<Combo[]>({id: 'Modelos'}, 'comboAutoPresta').subscribe(res => this.modelosCombo = res);
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
    this.genericRestService.create<Contrato>(this._datos._dominio).subscribe(data => {
      this.formulario = this.genericRestService.buildForm({
        regimenFiscal: [data.regimenFiscal ? data.regimenFiscal : '', Validators.required],
        nombres: [data.nombres ? data.nombres : ''],
        primerApellido: [data.primerApellido ? data.primerApellido : ''],
        segundoApellido: [data.segundoApellido ? data.segundoApellido : ''],
        genero: [data.genero ? data.genero : ''],
        edad: [data.edad ? data.edad : ''],
        rfc: [data.rfc ? data.rfc : ''],
        fechaNacimiento: [data.fechaNacimiento ? data.fechaNacimiento : ''],
        curp: [data.curp ? data.curp : ''],
        claveElector: [data.claveElector ? data.claveElector : ''],
        telefonoFijo: [data.telefonoFijo ? data.telefonoFijo : ''],
        telefonoCelular: [data.telefonoCelular ? data.telefonoCelular : ''],
        telefonoOficina: [data.telefonoOficina ? data.telefonoOficina : ''],
        correoElectronico: [data.correoElectronico ? data.correoElectronico : ''],
        direccion: [data.direccion ? data.direccion: ''],
        anio: [data.anio ? data.anio : ''],
        marca: [data.marca ? data.marca.id : ''],
        modelo: [data.modelo ? data.modelo.id : ''],
        versionAuto: [data.versionAuto ? data.versionAuto : ''],
        color: [data.color ? data.color : '', Validators.required],
        numeroDeSerie: [data.numeroDeSerie ? data.numeroDeSerie : '', Validators.required],
        placas: [data.placas ? data.placas : '', Validators.required],
        numeroDeMotor: [data.numeroDeMotor ? data.numeroDeMotor : '', Validators.required],
        numeroDeFactura: [data.numeroDeFactura ? data.numeroDeFactura : '', Validators.required],
        fechaDeFactura: [data.fechaDeFactura ? data.fechaDeFactura : '', Validators.required],
        emisoraDeFactura: [data.emisoraDeFactura ? data.emisoraDeFactura : '', Validators.required],
        valorDeVenta: [data.valorDeVenta ? data.valorDeVenta : '', Validators.required],
        valorDeCompra: [data.valorDeCompra ? data.valorDeCompra : '', Validators.required],
        montoMaximoAutorizado: [data.montoMaximoAutorizado ? data.montoMaximoAutorizado : '', Validators.required],
        numeroVin: [data.numeroVin ? data.numeroVin : '', Validators.required],
        gps1: [data.gps1 ? data.gps1 : '', Validators.required],
        proveedor1: [data.proveedor1 ? data.proveedor1 : '', Validators.required],
        gps2: [data.gps2 ? data.gps2 : ''],
        proveedor2: [data.proveedor2 ? data.proveedor2 : ''],
        gps3: [data.gps3 ? data.gps3 : ''],
        proveedor3: [data.proveedor3 ? data.proveedor3 : ''],
        montoRequerido: [data.montoRequerido ? data.montoRequerido : '', [Validators.required, Validators.min(20000), (control: AbstractControl) => Validators.max(this.montoMaximoAutorizado)(control)]],
        costoMensualInteres: [data.costoMensualInteres ? data.costoMensualInteres : this.costoMensualInteres, Validators.required],
        costoMensualMonitoreo: [data.costoMensualMonitoreo ? data.costoMensualMonitoreo : this.costoMensualMonitoreo, Validators.required],
        costoMensualGPS: [data.costoMensualGPS ? data.costoMensualGPS : this.costoMensualGPS, Validators.required],
        totalAutoPresta: [data.totalAutoPresta ? data.totalAutoPresta : this.totalAutoPresta, Validators.required],
        iva: [data.iva ? data.iva : this.iva, Validators.required],
        costoMensualTotal: [data.costoMensualTotal ? data.costoMensualTotal : this.costoMensualTotal, Validators.required],
        tipoContrato: [data.tipoContrato ? data.tipoContrato : '', Validators.required],
        referencia: [data.referencia ? data.referencia : ''],
        clabe: [data.clabe ? data.clabe : '', Validators.required],
      });
    });
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

  form() {
    this.formulario = this.genericRestService.buildForm({
      regimenFiscal: [''],
      nombres: [''],
      primerApellido: [''],
      segundoApellido: [''],
      genero: [''],
      edad: [''],
      rfc: [''],
      fechaNacimiento: [''],
      curp: [''],
      claveElector: [''],
      telefonoFijo: [''],
      telefonoCelular: [''],
      telefonoOficina: [''],
      correoElectronico: [''],
      direccion: [],
      anio: [''],
      marca: [''],
      modelo: [''],
      versionAuto: [''],
      color: [''],
      numeroDeSerie: [''],
      placas: [''],
      numeroDeMotor: [''],
      numeroDeFactura: [''],
      fechaDeFactura: [''],
      emisoraDeFactura: [''],
      valorDeVenta: [''],
      valorDeCompra: [''],
      montoMaximoAutorizado: [''],
      numeroVin: [''],
      gps1: [''],
      proveedor1: [''],
      gps2: [''],
      proveedor2: [''],
      gps3: [''],
      proveedor3: [''],
      montoRequerido: [''],
      costoMensualInteres: [this.costoMensualInteres],
      costoMensualMonitoreo: [this.costoMensualMonitoreo],
      costoMensualGPS: [this.costoMensualGPS],
      totalAutoPresta: [this.totalAutoPresta],
      iva: [this.iva],
      costoMensualTotal: [this.costoMensualTotal],
      tipoContrato: [''],
      referencia: [''],
      clabe: [''],
    });
  }

  save() {
    this.formulario.patchValue({direccion: this.direcciones})
    this.genericRestService.save<Contrato>(this.formulario.value, {}, this._datos._dominio).subscribe(data => {
      this.download(data.id)
      this.snack.open(this._datos._title + ' capturado!', 'OK', {duration: 4000});
    }, error => {
      this.snack.open(error.error.mensaje, 'OK', {duration: 4000});
    });
    this.ngOnInit();
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

  cp(id){
    this.genericRestService.combo<_comboCp[]>({id: id}, 'comboCp').subscribe(result => {
      this.codigoPostalCombo = result
      this.showNotification('snackbar-success', 'Puede seleccionar su codigo postal', 'bottom', 'center')
    });
  }

  selectEventEmisor(value: _comboCp) {
    this.direccion.patchValue({
      cp: value.id,
      entidad: value.estado,
      municipio: value.municipio,
      colonia: value.asentamiento,
    });
  }

  showNotification(colorName, text, placementFrom, placementAlign) {
    this.snack.open(text, '', { duration: 2000, verticalPosition: placementFrom, horizontalPosition: placementAlign, panelClass: colorName });
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

  datos(d: string){
    const data = d.split('');
    if(data.length == 18) {
      const a = Number(data[4])
      let complemento
      if (a >= 0 && a <= 4){
        complemento = '20'
      } else {
        complemento = '19'
      }
      let anio= complemento + data[4] + data[5]
      const mes = data[6] + data[7]
      const dia = data[8] + data[9]

      this.formulario.patchValue({genero: data[10]=='H'?'M':'F'})

      let dateString = anio + '-' + mes + '-' + dia + 'T00:00:00'

      let newDate = new Date(dateString);
      let timeDiff = Math.abs(Date.now() - newDate.getTime());
      let age = Math.floor((timeDiff / (1000 * 3600 * 24))/365.25);

      this.formulario.patchValue({edad: age, fechaNacimiento: newDate})

    }
  }

}
