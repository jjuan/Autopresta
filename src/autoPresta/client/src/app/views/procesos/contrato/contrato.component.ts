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
  coloniasCombo: Combo[];

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

  form(data?) {
    this.formulario = this.genericRestService.buildForm({
      regimenFiscal: [data ? data.regimenFiscal : '', Validators.required],
      nombres: [data ? data.nombres : ''],
      primerApellido: [data ? data.primerApellido : ''],
      segundoApellido: [data ? data.segundoApellido : ''],
      genero: [data ? data.genero : ''],
      edad: [data ? data.edad : ''],
      rfc: [data ? data.rfc : ''],
      fechaNacimiento: [data ? data.fechaNacimiento : ''],
      curp: [data ? data.curp : '', [Validators.required, Validators.maxLength(18), Validators.minLength(18)]],
      claveElector: [data ? data.claveElector : ''],
      telefonoFijo: [data ? data.telefonoFijo : ''],
      telefonoCelular: [data ? data.telefonoCelular : ''],
      telefonoOficina: [data ? data.telefonoOficina : ''],
      correoElectronico: [data ? data.correoElectronico : ''],
      direccion: [data ? data.direccion : ''],
      anio: [data ? data.anio : ''],
      marca: [data ? data.marca?.id : ''],
      modelo: [data ? data.modelo?.id : ''],
      versionAuto: [data ? data.versionAuto : ''],
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
    });
  }

  save() {
    if (this.direcciones.length == 0) {
      this.addNewBeneficiario()
    }
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

  datos(d: string) {
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

      this.formulario.patchValue({genero: data[10] == 'H' ? 'M' : 'F'})

      let dateString = anio + '-' + mes + '-' + dia + 'T00:00:00'

      let newDate = new Date(dateString);
      let timeDiff = Math.abs(Date.now() - newDate.getTime());
      let age = Math.floor((timeDiff / (1000 * 3600 * 24)) / 365.25);

      this.formulario.patchValue({edad: age, fechaNacimiento: newDate})

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
}
