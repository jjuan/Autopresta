import {Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {conciliacionContratosTable, conciliacionStatus} from "../../../../../core/models/data.interface";
import {BehaviorSubject, fromEvent, merge, Observable, Subscription} from "rxjs";
import {RestService} from "../../../../../core/service/rest.service";
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../../../../core/service/global.service";
import {MatDialog} from "@angular/material/dialog";
import {DatePipe} from "@angular/common";
import {MatSnackBar} from "@angular/material/snack-bar";
import {FormBuilder} from "@angular/forms";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatMenuTrigger} from "@angular/material/menu";
import {map} from "rxjs/operators";

@Component({
  selector: 'app-conciliacion-contratos',
  templateUrl: './conciliacion-contratos.component.html',
  styleUrls: ['./conciliacion-contratos.component.sass']
})
export class ConciliacionContratosComponent implements OnInit {
  public _dominio = 'Conciliaciones'
  displayedColumns = [
    'contrato',
    'parcialidad',
    'fecha',
    'monto'
  ];

  @Input() fechaInicio: Date
  @Input() fechaFin: Date
  @Input() cargoAbono: Boolean;
  @Input() titulo: string;
  @Input() subtitulo: string;

  public conciliacionStatus: conciliacionStatus = {
    conciliadas: 7.025,
    pendientes: 3,
    total: 10,
  };


  selection = new SelectionModel<conciliacionContratosTable>(true, []);
  advanceTable: conciliacionContratosTable | null;

  id: number;
  public getRowsSub: Subscription;
  db: RestService;
  dataSource: BancosDataSource | null;

  constructor(
    public httpClient: HttpClient, private globalService: GlobalService, public dialog: MatDialog, private datePipe: DatePipe,
    public advanceTableService: RestService, private snackBar: MatSnackBar, private fBuilder: FormBuilder
  ) {
  }

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild('filter', {static: true}) filter: ElementRef;
  @ViewChild(MatMenuTrigger)
  contextMenu: MatMenuTrigger;
  contextMenuPosition = {x: '0px', y: '0px'};

  ngOnInit() {
    this.advanceTableService.initService(this._dominio);
    this.loadData();
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.renderedData.length;
    return numSelected === numRows;
  }


  public loadData() {
    this.statusConciliacion()
    this.db = new RestService(this.httpClient, this.globalService, this.fBuilder);
    this.dataSource = new BancosDataSource(
      this.db, this.paginator, this.sort, this._dominio,
      this.datePipe.transform(this.fechaInicio, 'yyyy-MM-dd'),
      this.datePipe.transform(this.fechaFin, 'yyyy-MM-dd'),
      true);
    fromEvent(this.filter.nativeElement, 'keyup').subscribe(() => {
      if (!this.dataSource) {
        return;
      }
      this.dataSource.filter = this.filter.nativeElement.value;
    });
    this.paginator._changePageSize(this.paginator.pageSize)
  }

  showNotification(colorName, text, placementFrom, placementAlign) {
    this.snackBar.open(text, '', {
      duration: 2000,
      verticalPosition: placementFrom,
      horizontalPosition: placementAlign,
      panelClass: colorName
    });
  }

  statusConciliacion() {
    this.advanceTableService.index<conciliacionStatus>(this._dominio, {
      fechaInicio: this.datePipe.transform(this.fechaInicio, 'yyyy-MM-dd'),
      fechaFin: this.datePipe.transform(this.fechaFin, 'yyyy-MM-dd'),
    }, 'statusConciliaciones').subscribe(r => {
      this.conciliacionStatus = r
    })
  }

  onContextMenu(event: MouseEvent, item: conciliacionContratosTable) {
    event.preventDefault();
    this.contextMenuPosition.x = event.clientX + 'px';
    this.contextMenuPosition.y = event.clientY + 'px';
    this.contextMenu.menuData = {item: item};
    this.contextMenu.menu.focusFirstItem('mouse');
    this.contextMenu.openMenu();
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

  constructor(public _dataSource: RestService, public _paginator: MatPaginator, public _sort: MatSort, private _dominio: string, private fechaInicio, private fechaFin, private cargoAbono: Boolean) {
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
            advanceTable.folio+
          advanceTable.contrato+
          advanceTable.parcialidad+
          advanceTable.fecha+
          advanceTable.monto+
          advanceTable.estatus
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
      let propertyA: number | string = '';
      let propertyB: number | string = '';
      switch (this._sort.active) {
        case 'folio':
          [propertyA, propertyB] =[a.folio, b.folio]
          break
        case 'contrato':
          [propertyA, propertyB] =[a.contrato, b.contrato]
          break
        case 'parcialidad':
          [propertyA, propertyB] =[a.parcialidad, b.parcialidad]
          break
        case 'monto':
          [propertyA, propertyB] =[a.monto, b.monto]
          break
        case 'estatus':
          [propertyA, propertyB] =[a.estatus, b.estatus]
          break
      }
      const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
      const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
      return (
        (valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1)
      );
    });
  }
}
