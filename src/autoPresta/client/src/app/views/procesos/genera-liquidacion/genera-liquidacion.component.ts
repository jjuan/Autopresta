import { Component, OnInit } from '@angular/core';
import {FormGroup, Validators} from '@angular/forms';
import {MatProgressButtonOptions} from 'mat-progress-buttons';
import {ActivatedRoute} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HttpClient} from '@angular/common/http';
import {MatDialog} from '@angular/material/dialog';
import {GeneraLiquidacionFormComponent} from './genera-liquidacion-form/genera-liquidacion-form.component';
import {Subscription} from 'rxjs';
import {GeneraLiquidacionDeleteComponent} from './genera-liquidacion-delete/genera-liquidacion-delete.component';
import {TablaReglasComponent} from './tabla-reglas/tabla-reglas.component';
import {_genLiq_ext_det, _genLiq_ext_head, _operacion, _subconceptos, Combo} from "../../../core/models/data.interface";
import {GlobalService} from "../../../core/service/global.service";
import {RestService} from "../../../core/service/rest.service";

@Component({
  selector: 'app-genera-liquidacion',
  templateUrl: './genera-liquidacion.component.html',
  styleUrls: ['./genera-liquidacion.component.sass']
})
export class GeneraLiquidacionComponent implements OnInit {

  public _datos = { _title: 'Genera Liquidacion', _modulo: 'Procesos', _icono: 'fas fa-desktop', _dominio: 'Genera Liquidacion',
    _componente: 'Genera Liquidacion'};
  consultaButton: MatProgressButtonOptions = {
    active: false, text: 'Consultar', buttonColor: 'primary', barColor: 'primary', raised: true, stroked: false, mode: 'indeterminate',
    value: 0, disabled: false, fullWidth: false, buttonIcon: { fontIcon: 'search' }
  };

  generaLiquidacionButton: MatProgressButtonOptions = {
    active: false, text: 'Consultar', buttonColor: 'primary', barColor: 'primary', raised: true, stroked: false, mode: 'indeterminate',
    value: 0, disabled: false, fullWidth: false, buttonIcon: { fontIcon: 'search' }
  };
  public _subconceptoForm: FormGroup;
  public _data: _subconceptos;
  public razonSocialCombo: Combo[];
  public subconceptoAbonoCombo: Combo[];
  public subconceptoCargoCombo: Combo[];

  public getRowsSub: Subscription;
  public rows: any[];
  public id: any;
  public _subconceptoabono: FormGroup;
  public _subconceptocargo: FormGroup;
  public subconceptos: any[];
  constructor(
    private _globalService: GlobalService,
    private _genericRestService: RestService,
    private _activatedroute: ActivatedRoute,
    private snack: MatSnackBar,
    private http: HttpClient, private dialog: MatDialog,
    public  globalService: GlobalService
  ) { }

  ngOnInit(): void {
    this._genericRestService.initService('GenLiq_Ext_Head');
    this._genericRestService.combo<Combo[]>({id: 'RazonSocial'}, 'comboGLController').subscribe(result =>
      this.razonSocialCombo = result);
    this._genericRestService.combo<Combo[]>({id: 'Ingresos'}, 'comboSCController').subscribe(result =>
      this.subconceptoAbonoCombo = result);
    this._genericRestService.combo<Combo[]>({id: 'Egresos'}, 'comboSCController').subscribe(result =>
      this.subconceptoCargoCombo = result);

    this._subconceptoForm = this._genericRestService.buildForm({
      razonSocial: ['', Validators.required],
      subconceptoCargo: ['', Validators.required],
      subconceptoAbono: ['', Validators.required],
    });
    this.index();
  }

  index() {
    this._genericRestService.initService('GenLiq_Ext_Head');
    this.getRowsSub = this._genericRestService.index<any>('Parametros', {'max': 100})
      .subscribe(data => {
        this.rows = data;
      });
    this._genericRestService.initService('Parametros');
    this.getRowsSub = this._genericRestService.index<any>('Parametros', {'max': 100})
      .subscribe(data => {
        this.subconceptos = data;
      });
    this._genericRestService.initService('GenLiq_Ext_Head');
  }
  submit() {}

  consultar() {
    this.consultaButton.active = true;
    setTimeout(() => { this.consultaButton.active = false; }, 3500);
  }
  generarLiquidacion() {
    this.generaLiquidacionButton.active = true;
    setTimeout(() => { this.generaLiquidacionButton.active = false; }, 3500);
  }

  save() {
    const parametroAbono = this._subconceptoForm.get('subconceptoAbono').value;
    const portafolio = this._subconceptoForm.get('razonSocial').value;
    const parametroCobro = this._subconceptoForm.get('subconceptoCargo').value;
    this._subconceptoabono = this._genericRestService.buildForm({ parametro: [parametroAbono], portafolio: [portafolio], valor: 0 });
    this._subconceptocargo = this._genericRestService.buildForm({ parametro: [parametroCobro], portafolio: [portafolio], valor: 0 });
    this._genericRestService.initService('Parametros');
        // tslint:disable-next-line:no-shadowed-variable
    this._genericRestService.save<string>(this._subconceptoabono.value).subscribe();
    this._genericRestService.save<string>(this._subconceptocargo.value).subscribe();
    this._genericRestService.initService('GenLiq_Ext_Head');
    this.index();
  }
  addNew() {
    let data: any;
    this._genericRestService.initService('GenLiq_Ext_Head');
    this._genericRestService.create<_genLiq_ext_head>().subscribe(result => {
      data = result;
      const dialogRef = this.dialog.open(GeneraLiquidacionFormComponent, {
        data: { title: this._datos._title, disableClose: true, data: data, action: 'Agregar' }
      });
      // tslint:disable-next-line:no-shadowed-variable
      dialogRef.afterClosed().subscribe((result) => {
        if (!result) { return; }

        // tslint:disable-next-line:no-shadowed-variable
        this._genericRestService.save<string>(result).subscribe(data => {
          this.showNotification( 'snackbar-success', this._datos._title + 'Agregada!!', 'bottom', 'center' );
          this.index();

        }, error => {
          if (error._embedded !== undefined) {
            this.showNotification( 'snackbar-danger', '¡¡Error al guardar!!', 'bottom', 'center' );
            Object.entries(error._embedded.errors).forEach(([key, value]) => { });
          }
        });
      });
    });
  }
  addNewRule(row) {
    let data: any;
    this._genericRestService.create<_genLiq_ext_det>().subscribe(result => {
      data = result;
      const dialogRef = this.dialog.open(GeneraLiquidacionFormComponent, {
        data: { title: this._datos._title, disableClose: true, data: data, action: 'Agregar', orden: row.orden, head: row.id }
      });
      // tslint:disable-next-line:no-shadowed-variable
      dialogRef.afterClosed().subscribe((result) => {
        if (!result) { return; }
        // tslint:disable-next-line:no-shadowed-variable
        this._genericRestService.initService('GenLiq_Ext_Det');
        this._genericRestService.save<string>(result).subscribe(data1 => {
          this.showNotification( 'snackbar-success', this._datos._title + 'Agregada!!', 'bottom', 'center' );

        }, error => {
          if (error._embedded !== undefined) {
            this.showNotification( 'snackbar-danger', '¡¡Error al guardar!!', 'bottom', 'center' );
            Object.entries(error._embedded.errors).forEach(([key, value]) => { });
          }
        });
      });
    });
    this.index();
    this._genericRestService.initService('GenLiq_Ext_Head');
  }


  deleteItem(row) {
    this.id = row.id;
    const dialogRef = this.dialog.open(GeneraLiquidacionDeleteComponent, { data: row });
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this._genericRestService.delete<string>(row.id)
          .subscribe(data => {
            this.showNotification( 'snackbar-danger', '¡¡ ' + this._datos._title + ' Eliminada!!', 'bottom', 'center' );
          }, error => {
            this.showNotification( 'snackbar-danger', '¡Error al eliminar! Este registro esta siendo utilizado', 'bottom', 'center' );
            Object.entries(error._embedded.errors).forEach(([key, value]) => { });
          });
      }
    });
    this.index();
  }
  deleteSC(row) {
    this._genericRestService.initService('Parametros');
    this._genericRestService.delete<string>(row.idAbono).subscribe();
    this._genericRestService.initService('Parametros');
    this._genericRestService.delete<string>(row.idCargo).subscribe();
    this.index();
    this._genericRestService.initService('GenLiq_Ext_Head');
  }

  editCall(row) {
    this.id = row.id;
    let data: any;
    this._genericRestService.edit<_genLiq_ext_head>(this.id).subscribe(result => {
      data = result;
      const dialogRef = this.dialog.open(GeneraLiquidacionFormComponent, {
        data: { title: row.nombre , disableClose: true, data: data, action: 'Editar' }
      });
      dialogRef.afterClosed().subscribe((result1) => {
        if (!result1) { return; }
        this._genericRestService.update<string>(this.id, result1)
          .subscribe(data1 => {
            this.showNotification( 'snackbar-success', '¡¡ ' + this._datos._title + ' Editada!!', 'bottom', 'center' );
          }, error => {
            if (error._embedded !== undefined) {
              this.showNotification( 'snackbar-danger', 'Error al guardar', 'bottom', 'center' );
              Object.entries(error._embedded.errors).forEach(([key, value]) => {});
            }});
        this.index();
      });
    });
  }
  showNotification(colorName, text, placementFrom, placementAlign) {
    // tslint:disable-next-line:max-line-length
    this.snack.open(text, '', { duration: 2000, verticalPosition: placementFrom, horizontalPosition: placementAlign, panelClass: colorName });
  }

  cargarLista(row: any) {
    const opts = this.globalService.getHttpOptions();
    opts['params'] = {id: row.id};
    this.http.get<_operacion>(this.globalService.BASE_API_URL + 'GenLiq_Ext_Det' + '/' + 'reglas', opts).subscribe(result => {
      const dialogRef = this.dialog.open(TablaReglasComponent, { data: {data: result, titulo: row.nombre}});
      dialogRef.afterClosed().subscribe();
    });

  }
}
