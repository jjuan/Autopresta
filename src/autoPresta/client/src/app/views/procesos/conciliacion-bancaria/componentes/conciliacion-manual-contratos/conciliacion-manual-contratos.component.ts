import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {RestService} from "../../../../../core/service/rest.service";
import {Combo, conciliacionMovimientosTable} from "../../../../../core/models/data.interface";
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../../../../core/service/global.service";
import {DatePipe} from "@angular/common";
import {MatSnackBar} from "@angular/material/snack-bar";
import {BehaviorSubject, fromEvent, merge, Observable} from "rxjs";
import {map} from "rxjs/operators";

@Component({
  selector: 'app-conciliacion-manual-contratos',
  templateUrl: './conciliacion-manual-contratos.component.html',
  styleUrls: ['./conciliacion-manual-contratos.component.sass']
})
export class ConciliacionManualContratosComponent implements OnInit {
  public _dominio = 'Conciliaciones'
  displayedColumns = ['select', 'cuenta', 'referencia', 'fecha', 'monto'];

  action: string;
  dialogTitle: string;
  titulo

  formulario: FormGroup;
  montoMovimientos: number;
  etiqueta;
  saldo;

  db: RestService;
  dataSource: BancosDataSource | null;
  advanceTable: conciliacionMovimientosTable | null;
  selection = new SelectionModel<conciliacionMovimientosTable>(true, []);

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild('filter', {static: true}) filter: ElementRef;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  formaConciliacionCombo: Combo[] = [
    {id: 'regla1', descripcion: '1.\tNombre del cliente'},
    {id: 'regla2', descripcion: '2.\tReferencia bancaria'},
    {id: 'regla3', descripcion: '3.\tNumero de contrato'},
    {id: 'regla4', descripcion: '4.\tNúmero de cuenta'},
    {id: 'regla5', descripcion: '5.\tRFC del cliente'},
    {id: 'regla6', descripcion: '6.\tPlacas del vehículo del cliente'},
    {id: 'regla7', descripcion: '7.\tComprobantes de pago que comparte equipo de cobranza'},
    {id: 'regla8', descripcion: '8.\tCajero '}
  ]

  constructor(
    public dialogRef: MatDialogRef<ConciliacionManualContratosComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
    public httpClient: HttpClient, private globalService: GlobalService, public dialog: MatDialog, private datePipe: DatePipe,
    public advanceTableService: RestService, private snackBar: MatSnackBar, private fBuilder: FormBuilder, public restService: RestService,
  ) {
  }

  ngOnInit(): void {
    this.advanceTableService.initService(this._dominio);
    this.loadData();
    this.action = this.data.action;
    this.saldo = this.data.info.monto
    this.etiqueta = 'Saldo excedente'
    this.dialogTitle = this.data.cabecera
    this.titulo = this.data.title
    this.formulario = this.restService.buildForm({
      formaConciliacion: [''],
      campo: [''],
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  public confirmAdd(): void {
    let conciliacionDetalles = [];
    let movimientos = []
    this.selection.selected.forEach((item) => {
      conciliacionDetalles.push([
        {
          movimiento: item.folio,
          folioOperacion: this.data.info.folio,
          tipoOperacion: this.data.info.clase
        }
      ])
      movimientos.push({item})
    });
    this.dialogRef.close({
      detalles: conciliacionDetalles,
      movimientos: movimientos,
      parcialidad: this.data.info,
      saldo: this.saldo.toFixed(2),
      etiqueta: this.etiqueta,
      porMovimientos: false,
      formaConciliacion: this.formulario.get('formaConciliacion').value,
      campo: this.formulario.get('campo').value,
      montoParcialidades: this.data.info.monto.toFixed(2),
      montoMovimientos: this.montoMovimientos.toFixed(2)
    });
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.renderedData.length;
    return numSelected === numRows;
  }

  masterToggle() {
    this.isAllSelected() ? this.selection.clear() : this.dataSource.renderedData.forEach((row) => this.selection.select(row))
  }

  public loadData() {
    this.db = new RestService(this.httpClient, this.globalService, this.fBuilder);
    this.dataSource = new BancosDataSource(this.db, this.paginator, this.sort, this._dominio,
      this.datePipe.transform(this.data.fechaInicio, 'yyyy-MM-dd'),
      this.datePipe.transform(this.data.fechaFin, 'yyyy-MM-dd'),
      false);
    fromEvent(this.filter.nativeElement, 'keyup').subscribe(() => {
      if (!this.dataSource) {
        return;
      }
      this.dataSource.filter = this.filter.nativeElement.value;
    });
    this.paginator._changePageSize(this.paginator.pageSize)
  }

  actualizarMonto() {
    this.montoMovimientos = 0;
    this.selection.selected.forEach((item) => {
      this.montoMovimientos = this.montoMovimientos + item.monto
    });
    if (this.data.info.monto == this.montoMovimientos) {
      this.etiqueta = 'Liquidado'
      this.saldo = 0
    } else if (this.data.info.monto > this.montoMovimientos && this.montoMovimientos > 0) {
      this.etiqueta = 'Saldo excedente'
      this.saldo = this.data.info.monto - this.montoMovimientos
    } else if (this.data.info.monto < this.montoMovimientos) {
      this.etiqueta = 'Saldo remanente'
      this.saldo = this.montoMovimientos - this.data.info.monto
    } else if (this.montoMovimientos == 0) {
      this.saldo = this.data.info.monto
      this.etiqueta = 'Saldo excedente'
    }
  }
}

export class BancosDataSource extends DataSource<conciliacionMovimientosTable> {
  _filterChange = new BehaviorSubject('');

  get filter(): string {
    return this._filterChange.value;
  }

  set filter(filter: string) {
    this._filterChange.next(filter);
  }

  filteredData: conciliacionMovimientosTable[] = [];
  renderedData: conciliacionMovimientosTable[] = [];

  constructor(public _dataSource: RestService, public _paginator: MatPaginator, public _sort: MatSort,
              private _dominio: string, private fechaInicio, private fechaFin, private cargoAbono) {
    super();
    this._filterChange.subscribe(() => (this._paginator.pageIndex = 0));
  }

  connect(): Observable<conciliacionMovimientosTable[]> {
    const displayDataChanges = [this._dataSource.dataChange, this._sort.sortChange, this._filterChange, this._paginator.page];
    this._dataSource.getAdvancedTable<any>(this._dominio, {
      fechaInicio: this.fechaInicio,
      fechaFin: this.fechaFin,
      cargoAbono: this.cargoAbono,
      conciliados: false
    }, 'cargarMovimientos');
    return merge(...displayDataChanges).pipe(map(() => {
        this.filteredData = this._dataSource.data.slice().filter((advanceTable: conciliacionMovimientosTable) => {
          const searchStr = (
            advanceTable.folio +
            advanceTable.cuenta +
            advanceTable.fecha +
            advanceTable.referencia +
            advanceTable.monto +
            advanceTable.estatus +
            advanceTable.clase
          ).toLowerCase();
          return searchStr.indexOf(this.filter.toLowerCase()) !== -1;
        });
        const sortedData = this.sortData(this.filteredData.slice());
        const startIndex = this._paginator.pageIndex * this._paginator.pageSize;
        this.renderedData = sortedData.splice(startIndex, this._paginator.pageSize);
        return this.renderedData;
      })
    );
  }

  disconnect() {
  }

  sortData(data: conciliacionMovimientosTable[]): conciliacionMovimientosTable[] {
    if (!this._sort.active || this._sort.direction === '') {
      return data;
    }
    return data.sort((a, b) => {
      let propertyA: any;
      let propertyB: any;
      const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
      const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
      return (
        (valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1)
      );
    });
  }
}
