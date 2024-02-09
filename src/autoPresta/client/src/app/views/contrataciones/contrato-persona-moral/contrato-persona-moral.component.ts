import { Component, OnInit } from '@angular/core';
import {_comboCp, Combo, Contrato, direccion, IdentificacionesOficiales} from 'src/app/core/models/data.interface';
import {AbstractControl, FormGroup, Validators} from '@angular/forms';
import {DateAdapter} from "@angular/material/core";
import {MatDialog} from "@angular/material/dialog";
import {ActivatedRoute} from "@angular/router";
import {GeneracionContratoComponent} from "../contrataciones-componentes/generacion-contrato/generacion-contrato.component";
import {GlobalService} from "../../../core/service/global.service";
import {RestService} from "../../../core/service/rest.service";
import {MatSnackBar} from "@angular/material/snack-bar";

@Component({
  selector: 'app-contrato-persona-moral',
  templateUrl: './contrato-persona-moral.component.html',
  styleUrls: ['./contrato-persona-moral.component.sass']
})
export class ContratoPersonaMoralComponent implements OnInit {
  public _datos = {
    _title: 'Contrato', _modulo: 'Procesos', _icono: 'fas fa-desktop', _dominio: 'Contrato', _componente: 'Contrato'
  };
  public cveDir = 1

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

  public respuesta: Contrato;
  public estadosCombo: Combo[];
  public keyword = 'descripcion';
  public coloniasCombo1: Combo[];
  public coloniasCombo2: Combo[];
  public coloniasCombo3: Combo[];
  public coloniasCombo4: Combo[];
  public coloniasMoralCombo: Combo[];
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
    this.genericRestService.combo<any>({}, 'maxAutorizado').subscribe(result => {
      this.porcentajeMax = result.max
    });

    this.genericRestService.create<Contrato>(this._datos._dominio).subscribe(data => this.form(data));
    this.direccionFormulario()
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

  form(data?) {
    this.formulario = this.genericRestService.buildForm({
      regimenFiscal: ['PM'],
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
      rfc: [data ? data.rfc : ''],
      fechaNacimiento: [data ? data.fechaNacimiento : ''],
      curp: [data ? data.curp : '', [Validators.maxLength(18), Validators.minLength(18)]],
      documentoOficial: [data ? data.documentoOficial : ''],
      claveElector: [data ? data.claveElector : '', [Validators.required, (control: AbstractControl) => Validators.minLength(this.longitud)(control), (control: AbstractControl) => Validators.maxLength(this.longitud)(control)]],
      telefonoFijo: [data ? data.telefonoFijo : ''],
      telefonoCelular: [data ? data.telefonoCelular : ''],
      telefonoOficina: [data ? data.telefonoOficina : ''],
      correoElectronico: [data ? data.correoElectronico : ''],
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
      gps3: [data ? data.gps3 : ''],
      montoRequerido: [data ? data.montoRequerido : '', [Validators.required, Validators.min(20000), (control: AbstractControl) => Validators.max(Number((this.montoMaximoAutorizado).toFixed(2)))(control)]],
      costoMensualInteres: [data ? data.costoMensualInteres : this.costoMensualInteres],
      costoMensualMonitoreo: [data ? data.costoMensualMonitoreo : this.costoMensualMonitoreo],
      costoMensualGPS: [data ? data.costoMensualGPS : this.costoMensualGPS],
      totalAutoPresta: [data ? data.totalAutoPresta : this.totalAutoPresta],
      iva: [data ? data.iva : this.iva],
      costoMensualTotal: [data ? data.costoMensualTotal : this.costoMensualTotal],
      tipoContrato: [data ? data.tipoContrato : ''],
      referencia: [data ? data.referencia : ''],
      clabe: [data ? data.clabe : '', [Validators.minLength(18), Validators.maxLength(18)]],
      coacreditado: [false],
      calificacionCliente: [data ? data.calificacionCliente : ''],
      contratoPrueba: [data ? data.contratoPrueba : ''],
      montoTransferencia: [data ? data.montoTransferencia : ''],
      detalleDescuentos: [data ? data.detalleDescuentos : ''],
      fechaSolicitud: [data ? data.fechaSolicitud : ''],
      fechaContrato: [data ? data.fechaContrato : ''],
      montoLiquidar: [data ? data.montoLiquidar : ''],
      contratoMonterrey: [data ? data.contratoMonterrey : ''],
      fechaCompromiso: [data ? data.fechaCompromiso : ''],
      descuentosRetenciones: [data ? data.descuentosRetenciones : ''],
      tipoFolio:['']
    });
  }

  save() {

    const dialogRef = this.dialog.open(GeneracionContratoComponent, {
      data: {title: 'nombre', disableClose: true, data: 'result', action: 'Generar'}, height: 'auto', width: '40%'
    });
    dialogRef.afterClosed().subscribe(result => {
      if (!result) {
        return;
      }
      if (this.direcciones.length == 0) {
        this.addNewBeneficiario()
      }
      this.formulario.patchValue({direccion: this.direcciones})
      this.formulario.patchValue({
        calificacionCliente: result.calificacionCliente,
        contratoPrueba: result.contratoPrueba,
        contratoMonterrey: result.contratoMonterrey,
        montoTransferencia: result.montoTransferencia,
        detalleDescuentos: result.detalleDescuentos,
        fechaSolicitud: result.fechaSolicitud,
        fechaContrato: result.fechaContrato,
        montoLiquidar: result.montoLiquidar,
        fechaCompromiso: result.fechaCompromiso,
        referencia: result.referenciaBancariaBBVA,
        descuentosRetenciones: result.descuentosRetenciones,
        tipoFolio: result.tipoContrato
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
    this.montoMaximoAutorizado = monto * (this.porcentajeMax /100);
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


  eliminarBeneficiario(c: direccion) {
    const indice = this.direcciones.indexOf(c);
    this.direcciones.splice(indice, 1);
    this.cveDir--;
    this.direccionFormulario()
  }

  datos(d: string) {
    const data = d.split('');
    if (data.length == 18) {
      const a = Number(data[4])
      let complemento
      if (a >= 0 && a <= 2) {
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

      this.formulario.patchValue({genero: data[10] == 'H' || data[10] == 'h' ? 'M' : 'F', edad: age, fechaNacimiento: newDate})

    } else {
        this.formulario.patchValue({genero: '', edad: '', fechaNacimiento: ''})

    }
  }

  cargarDatos(value: string, form: number) {
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

  documentoOficialValidator(event) {
    for (let doc of this.documentoOficialCombo){
      if (doc.id == event) {
          this.longitud = doc.longitud
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

