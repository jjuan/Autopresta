import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {_bancos} from "../../../../../core/models/data.interface";
import {BehaviorSubject, fromEvent, merge, Observable, Subscription} from "rxjs";
import {RestService} from "../../../../../core/service/rest.service";
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../../../../core/service/global.service";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {FormBuilder} from "@angular/forms";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {MatMenuTrigger} from "@angular/material/menu";
import {BancosFormComponent} from "../../../../catalogos/bancos/bancos-form/bancos-form.component";
import {BancosDeleteComponent} from "../../../../catalogos/bancos/bancos-delete/bancos-delete.component";
import {map} from "rxjs/operators";

@Component({
  selector: 'app-conciliacion-movimientos',
  templateUrl: './conciliacion-movimientos.component.html',
  styleUrls: ['./conciliacion-movimientos.component.sass']
})
export class ConciliacionMovimientosComponent implements OnInit {
  public _datos = { _title: 'Banco', _modulo: 'Catalogos', _icono: 'fas fa-folder-open', _dominio: 'Banco', _componente: 'Bancos'}
  displayedColumns = [ 'select',
    'descripcionCorta',
    'descripcionLarga',
    'pais',
    'actions' ];
  selection = new SelectionModel<Movimientos>(true, []);
  advanceTable: _bancos | null;

  id: number;
  public getRowsSub: Subscription;
  db: RestService;
  dataSource: BancosDataSource | null;

  constructor(
    public httpClient: HttpClient, private globalService: GlobalService, public dialog: MatDialog,
    public advanceTableService: RestService, private snackBar: MatSnackBar, private fBuilder: FormBuilder
  ) {}

  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild('filter', { static: true }) filter: ElementRef;

  ngOnInit() {
    this.advanceTableService.initService(this._datos._dominio);
    this.loadData();
  }

  refresh() { this.loadData(); }


  // private refreshTable() { this.paginator._changePageSize(this.paginator.pageSize) }



  public loadData() {
    this.db = new RestService(this.httpClient, this.globalService, this.fBuilder);
    this.dataSource = new BancosDataSource( this.db, this.paginator, this.sort, this._datos._dominio );
    fromEvent(this.filter.nativeElement, 'keyup').subscribe(() => {
      if (!this.dataSource) { return; }
      this.dataSource.filter = this.filter.nativeElement.value;
    });
  }


}
export class BancosDataSource extends DataSource<_bancos> {
  _filterChange = new BehaviorSubject('');
  get filter(): string { return this._filterChange.value; }
  set filter(filter: string) { this._filterChange.next(filter); }
  filteredData: _bancos[] = [];
  renderedData: _bancos[] = [];

  constructor(public _dataSource: RestService, public _paginator: MatPaginator, public _sort: MatSort, private _dominio: string ) {
    super();
    this._filterChange.subscribe(() => (this._paginator.pageIndex = 0));
  }

  connect(): Observable<_bancos[]> {
    const displayDataChanges = [ this._dataSource.dataChange, this._sort.sortChange, this._filterChange, this._paginator.page ];
    this._dataSource.getAdvancedTable<any>(this._dominio,{'max': 100});
    return merge(...displayDataChanges).pipe( map(() => {
        this.filteredData = this._dataSource.data.slice().filter((advanceTable: _bancos) => {
          const searchStr = ( advanceTable.id + advanceTable.descripcionCorta + advanceTable.descripcionLarga + advanceTable.pais ).toLowerCase();
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

  sortData(data: _bancos[]): _bancos[] {
    if (!this._sort.active || this._sort.direction === '') { return data; }
    return data.sort((a, b) => {
      let propertyA: number | string = '';
      let propertyB: number | string = '';
      switch (this._sort.active) {
        case 'id':
          [propertyA, propertyB] = [a.id, b.id];
          break;
        case 'descripcionCorta':
          [propertyA, propertyB] = [a.descripcionCorta, b.descripcionCorta];
          break;
        case 'descripcionLarga':
          [propertyA, propertyB] = [a.descripcionLarga, b.descripcionLarga];
          break;
        case 'pais':
          [propertyA, propertyB] = [a.pais, b.pais];
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
