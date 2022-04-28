import {Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {
  _statusContratos, ConciliacionAutomatica,
  conciliacionMovimientosTable,
  conciliacionStatus, configConcilicacionComponent, objetoConciliacion
} from "../../../../../core/models/data.interface";
import {BehaviorSubject, fromEvent, merge, Observable, Subscription} from "rxjs";
import {RestService} from "../../../../../core/service/rest.service";
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../../../../core/service/global.service";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {map} from "rxjs/operators";
import {DatePipe} from "@angular/common";
import {MatMenuTrigger} from "@angular/material/menu";
import Swal from "sweetalert2";
import {
  ConciliacionManualMovimientosComponent
} from "../conciliacion-manual-movimientos/conciliacion-manual-movimientos.component";
import {ConciliacionDetallesComponent} from "../conciliacion-detalles/conciliacion-detalles.component";
import {ConciliacionPreviewComponent} from "../conciliacion-preview/conciliacion-preview.component";
import {DateAdapter} from "@angular/material/core";
import {
  ConciliacionManualContratosComponent
} from "../conciliacion-manual-contratos/conciliacion-manual-contratos.component";

@Component({
  selector: 'app-conciliacion-movimientos',
  templateUrl: './conciliacion-movimientos.component.html',
  styleUrls: ['./conciliacion-movimientos.component.sass']
})
export class ConciliacionMovimientosComponent implements OnInit {
  public _dominio = 'Conciliaciones'
  displayedColumns = [
    'actions',
    'cuenta',
    'referencia',
    'fecha',
    'monto'
  ];

  public _datos = {
    _title: 'Conciliacion de Movimiento',
    _modulo: 'Procesos',
    _icono: 'fas fa-desktop',
    _componente: 'Conciliacion de Movimientos'
  };
  @Input() fechaInicio: Date
  formulario: FormGroup;
  @Input() fechaFin: Date
  @Input() cargoAbono: Boolean;
  @Input() titulo: string;
  @Input() subtitulo: string;

  public conciliacionStatus: conciliacionStatus = {
    conciliadas: 0,
    pendientes: 0,
    total: 0,
  };


  selection = new SelectionModel<conciliacionMovimientosTable>(true, []);
  advanceTable: conciliacionMovimientosTable | null;

  id: number;
  public getRowsSub: Subscription;
  db: RestService;
  dataSource: BancosDataSource | null;

  constructor(
    private httpClient: HttpClient,
    private dateAdapter: DateAdapter<Date>, private globalService: GlobalService, private dialog: MatDialog, private datePipe: DatePipe,
    private advanceTableService: RestService, private snackBar: MatSnackBar, private fBuilder: FormBuilder
  ) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
  }

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild('filter', {static: true}) filter: ElementRef;
  @ViewChild(MatMenuTrigger)
  contextMenu: MatMenuTrigger;
  contextMenuPosition = {x: '0px', y: '0px'};
  red = 'blue';

  ngOnInit() {
    this.advanceTableService.initService(this._dominio);
    this.advanceTableService.initService(this._dominio);
    this.formulario = this.advanceTableService.buildForm({
      fechaInicio: [new Date(), Validators.required],
      fechaFin: [new Date(), Validators.required]
    })
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
      this.datePipe.transform(this.formulario.get('fechaInicio').value, 'yyyy-MM-dd'),
      this.datePipe.transform(this.formulario.get('fechaFin').value, 'yyyy-MM-dd'),
      this.cargoAbono);
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
      fechaInicio: this.datePipe.transform(this.formulario.get('fechaInicio').value, 'yyyy-MM-dd'),
      fechaFin: this.datePipe.transform(this.formulario.get('fechaFin').value, 'yyyy-MM-dd'),
      cargoAbono: this.cargoAbono
    }, 'statusConciliacionesMovimientos').subscribe(r => {
      this.conciliacionStatus = r
    })
  }

  onContextMenu(event: MouseEvent, item: conciliacionMovimientosTable) {
    event.preventDefault();
    this.contextMenuPosition.x = event.clientX + 'px';
    this.contextMenuPosition.y = event.clientY + 'px';
    this.contextMenu.menuData = {item: item};
    this.contextMenu.menu.focusFirstItem('mouse');
    this.contextMenu.openMenu();
  }

  conciliar(row: conciliacionMovimientosTable) {
    const opts = this.globalService.getHttpOptions()
    opts['params'] = {
      fechaInicio: this.datePipe.transform(this.formulario.get('fechaInicio').value, 'yyyy-MM-dd'),
      fechaFin: this.datePipe.transform(this.formulario.get('fechaFin').value, 'yyyy-MM-dd'),
      cargoAbono: this.cargoAbono,
      id: row.folio
    };

    let data: any;
    const dialogRef = this.dialog.open(ConciliacionManualMovimientosComponent, {
      width: '80%', disableClose: true,
      data: {
        title: 'Parcialidad', disableClose: true, fechaInicio: this.fechaInicio, fechaFin: this.fechaFin,
        info: row, action: 'Agregar', cabecera: 'Concilliaciòn manual de movimientos', esDetalle: false
      }
    });
    dialogRef.afterClosed().subscribe((result) => {
        if (result != undefined) {
          this.httpClient.post(this.globalService.BASE_API_URL + this._dominio + "/conciliacionMovimientos", result, opts)
            .subscribe(data => {
              this.showNotification('snackbar-success', 'Conciliacion creada!!', 'bottom', 'center');
              this.loadData();
            }, error => {
              if (error._embedded !== undefined) {
                this.showNotification('snackbar-danger', '¡¡Error al guardar!!', 'bottom', 'center');
              }
            })
        }
      }
    );
  }

  detalles(row) {
    this.advanceTableService.index<any>(this._dominio, {
      folio: row.folio,
      clase: row.clase
    }, 'verConciliacion').subscribe(r => {
      const dialogRef = this.dialog.open(ConciliacionDetallesComponent, {
        width: '80%', disableClose: true, height: '95%',
        data: {
          esMovimiento: r[0].porMovimiento, disableClose: true, fechaInicio: this.fechaInicio, fechaFin: this.fechaFin,
          info: r[0], action: 'Agregar', cabecera: 'Resumen de la conciliacion', esDetalle: true, row: row
        }
      });

      dialogRef.afterClosed().subscribe((respuesta) => {
        if (respuesta) {
          this.loadData()
        }
      })
    })
  }

  conciliacionAutomatica() {
    const opts = this.globalService.getHttpOptions()
    opts['params'] = {
      fechaInicio: this.datePipe.transform(this.formulario.get('fechaInicio').value, 'yyyy-MM-dd'),
      fechaFin: this.datePipe.transform(this.formulario.get('fechaFin').value, 'yyyy-MM-dd'),
    };
    this.httpClient.post<ConciliacionAutomatica[]>(this.globalService.BASE_API_URL + this._dominio + "/conciliacionGeneralMovimentos", {
      fechaInicio: this.fechaInicio,
      fechaFin: this.fechaFin,
    }, opts).subscribe(r => {
      if (r.length > 0) {
        const dialogRef = this.dialog.open(ConciliacionPreviewComponent, {
          width: '80%',
          disableClose: true, height: '95%',
          data: {
            fechaInicio: this.fechaInicio, fechaFin: this.fechaFin,
            datos: r, cabecera: 'Concilliaciòn Automatica de Movimientos',
          }
        });
        this.loadData()
      } else if (r.length == 0) {
        this.showNotification('snackbar-success', '0 Conciliaciones obtenidas', 'bottom', 'center');
      }
    })
  }

  conciliacionParcial(row) {
    //////////////////////////////////////////////////////////////////////////////
    let conciliacion
    this.advanceTableService.index<any>(this._dominio, {
      folio: row.raiz,
      clase: row.claseRaiz
    }, 'verConciliacion').subscribe(r => {
      conciliacion = r[0].id
      let dialogRef
      if (row.claseRaiz == 'LiquidacionBanco') {
        dialogRef = this.dialog.open(ConciliacionManualMovimientosComponent, {
          width: '80%', height: '95%', disableClose: true,
          data: {
            esMovimiento: r[0].porMovimiento,
            disableClose: true,
            fechaInicio: this.fechaInicio,
            fechaFin: this.fechaFin,
            detalle: r[0],
            esDetalle: false,
            title: 'Movimiento',
            info: {
              folio: r[0].detalles[0].movimiento[0].folio,
              cuenta: r[0].detalles[0].movimiento[0].cuenta,
              fecha: r[0].detalles[0].movimiento[0].fecha,
              titular: r[0].detalles[0].movimiento[0].titular,
              referencia: r[0].detalles[0].movimiento[0].referencia,
              monto: r[0].detalles[0].movimiento[0].monto,
              estatus: r[0].detalles[0].movimiento[0].estatus,
              clase: r[0].detalles[0].movimiento[0].clase,
            },
            action: 'Agregar',
            cabecera: 'Concilliaciòn manual de Movimientos',
          }
        });
      } else {
        dialogRef = this.dialog.open(ConciliacionManualContratosComponent, {
          width: '80%', height: '95%', disableClose: true,
          data: {
            esMovimiento: r[0].porMovimiento,
            disableClose: true,
            fechaInicio: this.fechaInicio,
            fechaFin: this.fechaFin,
            detalle: r[0],
            esDetalle: true,
            title: 'Movimiento',
            info: {
              folio: r[0].detalles[0].operacion[0].folio,
              contrato: r[0].detalles[0].operacion[0].contrato,
              parcialidad: r[0].detalles[0].operacion[0].parcialidad,
              fecha: r[0].detalles[0].operacion[0].fecha,
              titular: r[0].detalles[0].operacion[0].titular,
              monto: r[0].detalles[0].operacion[0].monto,
              estatus: r[0].detalles[0].operacion[0].estatus,
              clase: r[0].detalles[0].operacion[0].clase,
            },
            action: 'Agregar',
            cabecera: 'Concilliaciòn manual de contratos',
          }
        });
      }


      const opts = this.globalService.getHttpOptions()
      opts['params'] = {
        fechaInicio: this.datePipe.transform(this.formulario.get('fechaInicio').value, 'yyyy-MM-dd'),
        fechaFin: this.datePipe.transform(this.formulario.get('fechaFin').value, 'yyyy-MM-dd'),
        id: conciliacion
      };
      dialogRef.afterClosed().subscribe((result) => {
        if (result != undefined) {
          this.httpClient.post(this.globalService.BASE_API_URL + this._dominio + "/conciliacionMovimientosParcial", result, opts)
            .subscribe(data => {
              this.showNotification('snackbar-success', 'Conciliacion creada!!', 'bottom', 'center');
              this.loadData();
            }, error => {
              if (error._embedded !== undefined) {
                this.showNotification('snackbar-danger', '¡¡Error al guardar!!', 'bottom', 'center');
              }
            })
        }
      })
    })


    //////////////////////////////////////////////////////////////////////////////
  }

  getColor(estatus: string): objetoConciliacion {
    let objeto: objetoConciliacion = {
      color: '',
      label: '',
      icon: '',
    }
    switch (estatus) {
      case 'Conciliado':
        objeto.color = 'green'
        objeto.label = 'green'
        objeto.icon = 'visibility'
        break
      case 'Pendiente':
        objeto.color = '#E45826'
        objeto.label = 'verified_user'
        objeto.icon = 'verified_user'
        break
      case 'Conciliacion Parcial':
        objeto.color = '#333C83'
        objeto.label = '#333C83'
        objeto.icon = 'add_to_queue'
        break
    }
    return objeto
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

  constructor(public _dataSource: RestService, public _paginator: MatPaginator, public _sort: MatSort, private _dominio: string, private fechaInicio, private fechaFin, private cargoAbono: Boolean) {
    super();
    this._filterChange.subscribe(() => (this._paginator.pageIndex = 0));
  }

  connect(): Observable<conciliacionMovimientosTable[]> {
    const displayDataChanges = [this._dataSource.dataChange, this._sort.sortChange, this._filterChange, this._paginator.page];
    if (this.fechaInicio != null && this.fechaFin != null) {
      this._dataSource.getAdvancedTable<any>(this._dominio, {
        fechaInicio: this.fechaInicio,
        fechaFin: this.fechaFin,
        cargoAbono: this.cargoAbono
      }, 'cargarMovimientos');
    }
    return merge(...displayDataChanges).pipe(map(() => {
        this.filteredData = this._dataSource.data.slice().filter((advanceTable: conciliacionMovimientosTable) => {
          const searchStr = (
            advanceTable.folio +
            advanceTable.cuenta +
            advanceTable.fecha +
            advanceTable.referencia +
            advanceTable.monto +
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

  sortData(data: conciliacionMovimientosTable[]): conciliacionMovimientosTable[] {
    if (!this._sort.active || this._sort.direction === '') {
      return data;
    }
    return data.sort((a, b) => {
      let propertyA: number | string = '';
      let propertyB: number | string = '';
      switch (this._sort.active) {
        case 'folio':
          [propertyA, propertyB] = [a.folio, b.folio]
          break
        case 'cuenta':
          [propertyA, propertyB] = [a.cuenta, b.cuenta]
          break
        case 'referencia':
          [propertyA, propertyB] = [a.referencia, b.referencia]
          break
        case 'monto':
          [propertyA, propertyB] = [a.monto, b.monto]
          break
        case 'estatus':
          [propertyA, propertyB] = [a.estatus, b.estatus]
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
