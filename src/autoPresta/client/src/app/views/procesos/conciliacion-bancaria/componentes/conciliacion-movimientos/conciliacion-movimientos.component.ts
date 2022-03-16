import {Component, ElementRef, Input, OnChanges, OnInit, SimpleChanges, ViewChild} from '@angular/core';
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {
  _statusContratos, ConciliacionAutomatica,
  conciliacionMovimientosTable,
  conciliacionStatus, configConcilicacionComponent
} from "../../../../../core/models/data.interface";
import {BehaviorSubject, fromEvent, merge, Observable, Subscription} from "rxjs";
import {RestService} from "../../../../../core/service/rest.service";
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../../../../core/service/global.service";
import {MatDialog} from "@angular/material/dialog";
import {MatSnackBar} from "@angular/material/snack-bar";
import {FormBuilder} from "@angular/forms";
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

  @Input() fechaInicio: Date
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
    private httpClient: HttpClient, private globalService: GlobalService, private dialog: MatDialog, private datePipe: DatePipe,
    private advanceTableService: RestService, private snackBar: MatSnackBar, private fBuilder: FormBuilder
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
      fechaInicio: this.datePipe.transform(this.fechaInicio, 'yyyy-MM-dd'),
      fechaFin: this.datePipe.transform(this.fechaFin, 'yyyy-MM-dd'),
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
      fechaInicio: this.datePipe.transform(this.fechaInicio, 'yyyy-MM-dd'),
      fechaFin: this.datePipe.transform(this.fechaFin, 'yyyy-MM-dd'),
      cargoAbono: this.cargoAbono,
      id: row.folio
    };
    // this.httpClient.post<ConciliacionAutomatica>(this.globalService.BASE_API_URL + this._dominio + "/conciliacionAutomaticaMovimientos", {
    //   fechaInicio: this.fechaInicio,
    //   fechaFin: this.fechaFin,
    //   cargoAbono: this.cargoAbono,
    //   id: row.folio
    // }, opts).subscribe(r => {
      // if (r == true) {
      //   const dialogRef = this.dialog.open(ConciliacionDetallesComponent, {
      //     width: '50%', disableClose: true,
      //     data: {
      //       esMovimiento: true, disableClose: true, fechaInicio: this.fechaInicio, fechaFin: this.fechaFin,
      //       info: row, action: 'Agregar', cabecera: 'Resumen de la conciliacion', esDetalle: false
      //     }
      //   });
      //   dialogRef.afterClosed().subscribe((result) => {
      //     if (!result) {
      //       return
      //     }
      //   })
      // if (r.concilio == true) {
      //   const dialogRef = this.dialog.open(ConciliacionDetallesComponent, {
      //     width: '50%', disableClose: true,
      //     data: {
      //       esMovimiento: true, disableClose: true, fechaInicio: this.fechaInicio, fechaFin: this.fechaFin,
      //       info: row, action: 'Agregar', cabecera: 'Resumen de la conciliacion', esDetalle: false
      //     }
      //   });
      //   dialogRef.afterClosed().subscribe((result) => {
      //     if (result == true) {
      //       return
      //     }
      //   })
      // } else {

        let data: any;
        const dialogRef = this.dialog.open(ConciliacionManualMovimientosComponent, {
          width: '80%', disableClose: true,
          data: {
            title: 'Parcialidad', disableClose: true, fechaInicio: this.fechaInicio, fechaFin: this.fechaFin,
            info: row, action: 'Agregar', cabecera: 'Concilliaciòn manual de movimientos', esDetalle: false
          }
        });
        dialogRef.afterClosed().subscribe((result) => {
            if (result) {
              // const detalle = this.dialog.open(ConciliacionDetallesComponent, {
              //   width: '50%', disableClose: true,
              //   data: {
              //     esMovimiento: true, disableClose: true, fechaInicio: this.fechaInicio, fechaFin: this.fechaFin,
              //     info: row, action: 'Agregar', cabecera: 'Resumen de la conciliacion', esDetalle: false, datos: result
              //   }
              // });

              // detalle.afterClosed().subscribe((respuesta) => {
              //   if (respuesta) {
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
            // }
            // if (result.diferencia != 0) {
            //   Swal.fire({
            //     title: 'Advertencia',
            //     text: "Los montos de las parcialidades seleccionadas y el movimiento no coinciden, ¿Desea continuar?",
            //     icon: 'warning',
            //     showCancelButton: true,
            //     confirmButtonColor: '#3085d6',
            //     cancelButtonColor: '#d33',
            //     confirmButtonText: 'Confirmar',
            //     cancelButtonText: 'Cancelar'
            //   }).then((res) => {
            //     if (res.value) {
            this.httpClient.post(this.globalService.BASE_API_URL + this._dominio + "/conciliacionMovimientos", result, opts)
              .subscribe(data => {
                this.showNotification('snackbar-success', 'Conciliacion creada!!', 'bottom', 'center');
                this.loadData();
              }, error => {
                if (error._embedded !== undefined) {
                  this.showNotification('snackbar-danger', '¡¡Error al guardar!!', 'bottom', 'center');
                }
              })
            // }
            // });
            // }
          }
        );
    //   }
    // })
    // }
    // })
  }

  detalles(row) {
    this.advanceTableService.index<any>(this._dominio, {
      folio: row.folio,
      clase: row.clase
    }, 'verConciliacion').subscribe(r => {
      const dialogRef = this.dialog.open(ConciliacionDetallesComponent, {
        width: '80%', disableClose: true,
        data: {
          esMovimiento: r[0].porMovimiento, disableClose: true, fechaInicio: this.fechaInicio, fechaFin: this.fechaFin,
          info: r[0], action: 'Agregar', cabecera: 'Resumen de la conciliacion', esDetalle: true
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
      fechaInicio: this.datePipe.transform(this.fechaInicio, 'yyyy-MM-dd'),
      fechaFin: this.datePipe.transform(this.fechaFin, 'yyyy-MM-dd'),
    };
    this.httpClient.post<ConciliacionAutomatica[]>(this.globalService.BASE_API_URL + this._dominio + "/conciliacionGeneralMovimentos", {
      fechaInicio: this.fechaInicio,
      fechaFin: this.fechaFin,
    }, opts).subscribe(r => {
      if (r.length > 0) {
        const dialogRef = this.dialog.open(ConciliacionPreviewComponent, {
          width: '80%',
          disableClose: true,
          // height: '80%',
          data: {
            fechaInicio: this.fechaInicio, fechaFin: this.fechaFin,
            datos: r, cabecera: 'Concilliaciòn Automatica de Movimientos',
          }
        });
        this.loadData()
      }else if (r.length == 0){
        this.showNotification('snackbar-success', '0 Conciliaciones obtenidas', 'bottom', 'center');
      }
    })
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
    this._dataSource.getAdvancedTable<any>(this._dominio, {
      fechaInicio: this.fechaInicio,
      fechaFin: this.fechaFin,
      cargoAbono: this.cargoAbono
    }, 'cargarMovimientos');

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
