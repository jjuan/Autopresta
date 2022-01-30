import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {conciliacionContratosTable} from "../../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../../../../core/service/global.service";
import {RestService} from "../../../../../core/service/rest.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {FormBuilder, FormGroup} from "@angular/forms";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {BehaviorSubject, fromEvent, merge, Observable, Subscription} from "rxjs";
import {map} from 'rxjs/operators';
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {MatMenuTrigger} from "@angular/material/menu";
import {DatePipe} from "@angular/common";

@Component({
  selector: 'app-conciliacion-manual-movimientos',
  templateUrl: './conciliacion-manual-movimientos.component.html',
  styleUrls: ['./conciliacion-manual-movimientos.component.sass']
})
export class ConciliacionManualMovimientosComponent implements OnInit {
  public _dominio = 'Conciliaciones'
  displayedColumns = ['select', 'contrato', 'parcialidad', 'fecha', 'monto'];

  action: string;
  dialogTitle: string;
  titulo

  formulario: FormGroup;
  montoParcialidades: number;
  etiqueta;
  saldo;

  db: RestService;
  dataSource: BancosDataSource | null;
  advanceTable: conciliacionContratosTable | null;
  selection = new SelectionModel<conciliacionContratosTable>(true, []);

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild('filter', {static: true}) filter: ElementRef;
  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(
    public dialogRef: MatDialogRef<ConciliacionManualMovimientosComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
    public httpClient: HttpClient, private globalService: GlobalService, public dialog: MatDialog, private datePipe: DatePipe,
    public advanceTableService: RestService, private snackBar: MatSnackBar, private fBuilder: FormBuilder
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
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  public confirmAdd(): void {
    let conciliacionDetalles = [];
    this.selection.selected.forEach((item) => {
      conciliacionDetalles.push([
        {
          movimiento: this.data.info.folio,
          folioOperacion: item.folio,
          tipoOperacion: item.clase
        }
      ])
    });
    this.dialogRef.close({
      detalles: conciliacionDetalles,
      saldo: this.saldo.toFixed(2),
      etiqueta: this.etiqueta,
      montoMovimientos: this.data.info.monto.toFixed(2),
      montoParcialidades: this.montoParcialidades.toFixed(2)
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
    this.montoParcialidades = 0;
    this.selection.selected.forEach((item) => {
      this.montoParcialidades = this.montoParcialidades + item.monto
    });
    if (this.data.info.monto == this.montoParcialidades) {
      this.etiqueta = 'Liquidado'
      this.saldo = 0
    } else if (this.data.info.monto > this.montoParcialidades && this.montoParcialidades > 0) {
      this.etiqueta = 'Saldo excedente'
      this.saldo = this.data.info.monto - this.montoParcialidades
    } else if (this.data.info.monto < this.montoParcialidades) {
      this.etiqueta = 'Saldo remanente'
      this.saldo = this.montoParcialidades - this.data.info.monto
    } else if (this.montoParcialidades == 0) {
      this.saldo = this.data.info.monto
      this.etiqueta = 'Saldo excedente'
    }
  }
}

export class BancosDataSource extends DataSource<conciliacionContratosTable> {
  _filterChange = new BehaviorSubject('');

  get filter(): string {
    return this._filterChange.value;
  }

  set filter(filter: string) {
    this._filterChange.next(filter);
  }

  filteredData: conciliacionContratosTable[] = [];
  renderedData: conciliacionContratosTable[] = [];

  constructor(public _dataSource: RestService, public _paginator: MatPaginator, public _sort: MatSort,
              private _dominio: string, private fechaInicio, private fechaFin, private cargoAbono) {
    super();
    this._filterChange.subscribe(() => (this._paginator.pageIndex = 0));
  }

  connect(): Observable<conciliacionContratosTable[]> {
    const displayDataChanges = [this._dataSource.dataChange, this._sort.sortChange, this._filterChange, this._paginator.page];
    this._dataSource.getAdvancedTable<any>(this._dominio, {
      fechaInicio: this.fechaInicio,
      fechaFin: this.fechaFin,
      cargoAbono: this.cargoAbono
    }, 'cargarParcialidades');
    return merge(...displayDataChanges).pipe(map(() => {
        this.filteredData = this._dataSource.data.slice().filter((advanceTable: conciliacionContratosTable) => {
          const searchStr = (
            advanceTable.folio +
            advanceTable.contrato +
            advanceTable.parcialidad +
            advanceTable.fecha +
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

  sortData(data: conciliacionContratosTable[]): conciliacionContratosTable[] {
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
