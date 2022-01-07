import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HttpClient} from '@angular/common/http';
import { ColumnMode } from '@swimlane/ngx-datatable';
import { Observable, BehaviorSubject, merge, fromEvent, Subscription } from 'rxjs';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { DataSource } from '@angular/cdk/table';
import { map } from 'rxjs/operators';
import { SelectionModel } from '@angular/cdk/collections';
import { MatMenuTrigger } from '@angular/material/menu';
import { MatDialog } from '@angular/material/dialog';
import {_importaciones, Combo} from "../../../core/models/data.interface";
import {RestService} from "../../../core/service/rest.service";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
import {GlobalService} from "../../../core/service/global.service";
import {CargaExtractosComponent} from "./carga-extractos/carga-extractos.component";

@Component({
  selector: 'app-importaciones',
  templateUrl: './importaciones.component.html',
  styleUrls: ['./importaciones.component.sass']
})
export class ImportacionesComponent implements OnInit {
  cuentaCombo: Combo[];
  comboArchivos: Combo[];

  constructor(
    public httpClient: HttpClient, private globalService: GlobalService, public dialog: MatDialog,
    public advanceTableService: RestService, private snackBar: MatSnackBar, private fBuilder: FormBuilder,
    private http: HttpClient
  ) {}
  public _datos = { _title: 'Carga de Extracto', _modulo: 'Procesos', _icono: 'fas fa-desktop', _dominio: 'CargaExtractosFisica', _componente: 'Carga de Extractos'
  };
  ColumnMode = ColumnMode;
  rows: [];
  displayedColumns = [
    'cuenta',
    'fecha',
    'montoAbono',
    'montoCargo',
    'tipoMovimiento',
    'referencia',
  ];
  selection = new SelectionModel<_importaciones>(true, []);
  advanceTable: _importaciones | null;

  public fecha: Date = new Date();
  _importaciones: FormGroup;
  id: number;
  public getRowsSub: Subscription;
  db: RestService;
  dataSource: ProveedoresDataSource | null;
  private tipoOperacion: string;
  razonSocialCombo: Combo[];
  public divisaCombo: Combo[];
  public razonSocialId: any;
  public aliasCombo: Combo[];

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild('filter', { static: true }) filter: ElementRef;
  @ViewChild(MatMenuTrigger)
  contextMenu: MatMenuTrigger;
  contextMenuPosition = { x: '0px', y: '0px' };

  submit() {}

  ngOnInit() {
    this.advanceTableService.initService(this._datos._dominio);
    // this.advanceTableService.combo<Combo[]>({id: 'RazonSocial'}, 'comboController').subscribe(result =>
    //   this.razonSocialCombo = result);
    this.formulario();
    this.loadData();
  }

  formulario() {
    this._importaciones = new FormGroup({
      fechaInicio: new FormControl('', Validators.required),
      fechaFin: new FormControl('', Validators.required),
      razonSocial: new FormControl(''),
      alias: new FormControl(''),
      archivo: new FormControl('')
    });
    }

  refresh() { this.loadData(); }
  download() {
    const _observable = this.advanceTableService.getReport(
      'saldos', 'Reporte',
      {
        fecha: new Date(this._importaciones.get('fecha').value).toJSON(),
        razonSocial: this._importaciones.get('razonSocial').value,
        alias: this._importaciones.get('alias').value
      });

    return this.advanceTableService.printReport(_observable, 'Reporte de Saldos');
  }
  addNew() {
    const _observable = this.advanceTableService.getReport(
      'inversiones', 'Reporte',
      {
        fecha: new Date(this._importaciones.get('fecha').value).toJSON(),
        razonSocial: this._importaciones.get('razonSocial').value,
        alias: this._importaciones.get('alias').value,
      });

    return this.advanceTableService.printReport(_observable, 'Reporte de Inversiones');
  }

  private refreshTable() { this.paginator._changePageSize(this.paginator.pageSize); }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.renderedData.length;
    return numSelected === numRows;
  }

  masterToggle() {
    this.isAllSelected() ? this.selection.clear() : this.dataSource.renderedData.forEach((row) => this.selection.select(row));
  }

  public loadData() {
    const fechaInicio = new Date(this._importaciones.get('fechaInicio').value).toJSON();
    const fechaFin = new Date(this._importaciones.get('fechaFin').value).toJSON();
    const razonSocial = this._importaciones.get('razonSocial').value;
    const alias = this._importaciones.get('alias').value;
    const archivo = this._importaciones.get('archivo').value;
    this.cargarArchivos();
    this.db = new RestService(this.httpClient, this.globalService, this.fBuilder);
    this.dataSource = new ProveedoresDataSource( this.db, this.paginator, this.sort, this._datos._dominio, fechaInicio, fechaFin, razonSocial, alias, archivo);
    fromEvent(this.filter.nativeElement, 'keyup').subscribe(() => {
      if (!this.dataSource) { return; }
      this.dataSource.filter = this.filter.nativeElement.value;
    });
  }

  cargarArchivos() {
    const fechaInicio = new Date(this._importaciones.get('fechaInicio').value).toJSON();
    const fechaFin = new Date(this._importaciones.get('fechaFin').value).toJSON();
    const razonSocial = this._importaciones.get('razonSocial').value;
    const alias = this._importaciones.get('alias').value;
    const archivo = this._importaciones.get('archivo').value;
    this.http.get<Combo[]>(this.globalService.BASE_API_URL + 'CargaExtractosFisica/listaArchivos/', { headers: {
        'Authorization': 'Bearer=' + this.globalService.getAuthToken()
      }, params: {
        fechaInicio: fechaInicio,
        fechaFin: fechaFin,
        razonSocial: razonSocial,
        alias: alias,
        archivo: archivo
      }
    }).subscribe(data => { this.comboArchivos = data; });
    }


  showNotification(colorName, text, placementFrom, placementAlign) {
    this.snackBar.open(text, '', { duration: 2000,
      verticalPosition: placementFrom, horizontalPosition: placementAlign, panelClass: colorName });
  }

  onContextMenu(event: MouseEvent, item: _importaciones) {
    event.preventDefault();
    this.contextMenuPosition.x = event.clientX + 'px';
    this.contextMenuPosition.y = event.clientY + 'px';
    this.contextMenu.menuData = { item: item };
    this.contextMenu.menu.focusFirstItem('mouse');
    this.contextMenu.openMenu();
  }

  cargarCuentas(value: any) {
    this.advanceTableService.combo<Combo[]>({id: value, banco: 0}, 'comboCuenta').subscribe(result =>
      this.cuentaCombo = result);
  }

  upload() {
    let fileName;
    const dialogRef = this.dialog.open(CargaExtractosComponent, {
      data: { title: 'Cargar Extracto', disableClose: true, action: 'Agregar' }
    });
    // tslint:disable-next-line:no-shadowed-variable
    dialogRef.afterClosed().subscribe((result) => {
      if (!result) { return; }
      const formData = new FormData();
      formData.append('banco', result.banco);
      formData.append('cuenta', result.cuenta);
      if (result.file) {
        fileName = result.file.name;
        formData.append('file', result.file, result.file.name);
      }

      this.http.post(this.globalService.BASE_API_URL + 'CargaExtractosFisica/edoctaFisica', formData, { headers: {
          'Authorization': 'Bearer=' + this.globalService.getAuthToken()
        }
      }).subscribe(data => {
        this.showNotification( 'snackbar-success', 'Procesando Archivo!!', 'bottom', 'center' );
        this.formulario();
        this._importaciones.patchValue({archivo: fileName})
        this.refresh();
      }, error => {
        if (error.error !== undefined) {
          if (error.error.mensaje !== undefined) {
            this.showNotification('snackbar-danger', error.error.mensaje, 'bottom', 'center');
          }
        }

        if (error._embedded !== undefined) {
          this.showNotification( 'snackbar-danger', '¡¡Error al guardar!!', 'bottom', 'center' );
          Object.entries(error._embedded.errors).forEach(([key, value]) => { });
        }
      });
      this.refreshTable();
    });
  }
  resumenCarga(fileName: string) {
    this.http.get<any>(this.globalService.BASE_API_URL + 'CargaExtractosFisica/resumen/', { headers: {
        'Authorization': 'Bearer=' + this.globalService.getAuthToken()
      }, params: { id: fileName }
    }).subscribe(data => { this.rows = data.data; });
  }
}
export class ProveedoresDataSource extends DataSource<_importaciones> {
  _filterChange = new BehaviorSubject('');
  get filter(): string { return this._filterChange.value; }
  set filter(filter: string) { this._filterChange.next(filter); }
  filteredData: _importaciones[] = [];
  renderedData: _importaciones[] = [];

  constructor(public _dataSource: RestService,
              public _paginator: MatPaginator,
              public _sort: MatSort,
              private _dominio: string,
              private fechaInicio,
              private fechaFin,
              private razonSocial,
              private alias,
              private archivo) {
    super();
    this._filterChange.subscribe(() => (this._paginator.pageIndex = 0));
  }

  connect(): Observable<_importaciones[]> {
    const displayDataChanges = [ this._dataSource.dataChange, this._sort.sortChange, this._filterChange, this._paginator.page ];
    if (this.fechaInicio != null && this.fechaInicio != null || this.archivo != null && this.archivo != '' && this.archivo != undefined) {
      this._dataSource.getAdvancedTable<any>(this._dominio, {
        fechaInicio: this.fechaInicio,
        fechaFin: this.fechaFin,
        razonSocial: this.razonSocial,
        alias: this.alias,
        archivo: this.archivo
      }, 'consultaExtractos');

    }
    return merge(...displayDataChanges).pipe( map(() => {
        this.filteredData = this._dataSource.data.slice().filter((advanceTable: _importaciones) => {
          const searchStr = (
            advanceTable.cuenta +
            advanceTable.fecha +
            advanceTable.montoAbono +
            advanceTable.montoCargo +
            advanceTable.tipoMovimiento +
            advanceTable.referencia
          ).toLowerCase();
          return searchStr.indexOf(this.filter.toLowerCase()) !== -1;
        });
        const sortedData = this.sortData(this.filteredData.slice());
        const startIndex = this._paginator.pageIndex * this._paginator.pageSize;
        this.renderedData = sortedData.splice( startIndex, this._paginator.pageSize );
        return this.renderedData;
      })
    );
  }

  disconnect() {}

  sortData(data: _importaciones[]): _importaciones[] {
    if (!this._sort.active || this._sort.direction === '') { return data; }
    return data.sort((a, b) => {
      let propertyA: number | string = '';
      let propertyB: number | string = '';
      switch (this._sort.active) {
        case 'cuenta':
          [propertyA, propertyB] = [a.cuenta, b.cuenta]
          break;
        case 'montoAbono':
          [propertyA, propertyB] = [a.montoAbono, b.montoAbono]
          break;
        case 'montoCargo':
          [propertyA, propertyB] = [a.montoCargo, b.montoCargo]
          break;
        case 'tipoMovimiento':
          [propertyA, propertyB] = [a.tipoMovimiento, b.tipoMovimiento]
          break;
        case 'referencia':
          [propertyA, propertyB] = [a.referencia, b.referencia]
          break;
      }
      const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
      const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
      return (
        (valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1)
      );
    });
  }
}
