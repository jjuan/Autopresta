import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {_statusContratos, Agencias, Contrataciones, ContratoDetalle} from "../../../core/models/data.interface";
import {MatDialog} from "@angular/material/dialog";
import {RestService} from "../../../core/service/rest.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {GlobalService} from "../../../core/service/global.service";
import {FormBuilder} from "@angular/forms";
import {DialogService} from "../../../core/service/dialog.service";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {BehaviorSubject, fromEvent, merge, Observable} from "rxjs";
import {map} from "rxjs/operators";
import {DetallePagosComponent} from "./detalle-pagos/detalle-pagos.component";
import {FormAgenciasComponent} from "../../catalogos/agencias/form-agencias/form-agencias.component";
import {CamboEstadoComponent} from "./cambo-estado/cambo-estado.component";
import {Router, RouterModule} from "@angular/router";
import {CambioEstatusComponent} from "./cambio-estatus/cambio-estatus.component";

@Component({
  selector: 'app-contratos-firmados',
  templateUrl: './contratos-firmados.component.html',
  styleUrls: ['./contratos-firmados.component.sass']
})
export class ContratosFirmadosComponent implements OnInit {
  datos = {
    modulo: 'Consultas',
    componente: 'Contratos Firmados',
    icono: 'fas fa-folder-open',
    titulo: 'Contrato',
    controlador: 'Contrato'
  }
  displayedColumns = [
    'folio',
    'titular',
    'apoderado',
    'fechaEmision',
    'montoPrestamo',
    'total',
    'estatus',
    'estatusCliente',
    'actions'
  ];
  selection = new SelectionModel<Contrataciones>(true, []);
  dataSource: registros | null;

  constructor(public dialog: MatDialog, public restService: RestService, private snackBar: MatSnackBar, private router: Router,
              private globalService: GlobalService, private formBuilder: FormBuilder, private dialogService: DialogService) {
  }

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild('filter', {static: true}) filter: ElementRef;

  conciliacionOperaciones: _statusContratos = {
    registrado: 0,
    impreso: 0,
    firmado: 0,
    cancelado: 0,
    total: 0,
  };

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
    this.estatusContratos()
    this.dataSource = new registros(this.restService, this.paginator, this.sort, this.datos.controlador);
    fromEvent(this.filter.nativeElement, 'keyup').subscribe(() => {
      if (!this.dataSource) {
        return;
      }
      this.dataSource.filter = this.filter.nativeElement.value;
    });
  }

  private refreshTable() {
    this.paginator._changePageSize(this.paginator.pageSize);
  }

  getEstatus(estatus: any) {
    let label = ''
    if (estatus == 'C') {
      label = 'Cancelado'
    } else if (estatus == 'F') {
      label = 'Firmado'
    } else if (estatus == 'P') {
      label = 'Pendiente'
    }
    return label
  }

  getTotal(montos) {
    let total = 0
    for (let m of montos) {
      total = total + m.monto
    }
    return total
  }

  cambiarEstatus(id, accion) {
    this.restService.index<any>(this.datos.controlador, {id: id}, accion).subscribe(r => {
      this.loadData()
      this.dialogService.snack(accion == 'librarFolio' ? 'danger' : 'success', r.message)
    })
  }

  download(id: number, numeroContrato) {
    const _dominio = 'Reporte'
    const _observable = this.restService.getReport('contratoAutoPresta', _dominio, {id: id});
    return this.restService.printReport(_observable, 'Contrato AP #' + numeroContrato);
  }

  cambiarEstado(id, accion: string) {
    this.restService.edit<Agencias>(id, this.datos.controlador).subscribe(result => {
      const dialogRef = this.dialog.open(CamboEstadoComponent, {
        data: {title: 'Cambiar estado del contrato', disableClose: true, data: result, action: 'Editar'},
        height: 'auto',
        width: '40%'
      });
      dialogRef.afterClosed().subscribe(result => {
        if (!result) {
          return;
        }
        this.restService.update<string>(id, result, this.datos.controlador).subscribe(() => {
          this.dialogService.snack('success', '¡¡ ' + this.datos.titulo + ' actualizado!!');
          this.loadData();
        }, error => {
          if (error._embedded !== undefined) {
            this.dialogService.snack('danger', 'Error al actualizar');
          }
        });
        this.refreshTable();
      });
    });

  }

  verDetalles(id, accion: string) {
    this.restService.index<ContratoDetalle[]>('ContratoDetalle', {id: id}, accion).subscribe(pagos => {
      this.dialog.open(DetallePagosComponent, {
        data: {datos: pagos, titulo: 'Parcialidades del contrato ' + id}
      })
    })
  }

  estatusContratos() {
    this.restService.index<_statusContratos>(this.datos.controlador, {}, 'estatusContratos').subscribe(r => {
      this.conciliacionOperaciones = r
    })
  }

  extenderContrato(id) {
    this.router.navigate(['Consultas/Extension-Contrato/' + id])
  }

  generaExtension(row) {
    this.router.navigate(['/Contrataciones/Extension-Contrato/'+row.id])
  }

  cambiarEstatusCliente(id) {
    this.restService.edit<Agencias>(id, this.datos.controlador).subscribe(result => {
      const dialogRef = this.dialog.open(CambioEstatusComponent, {
        data: {title: 'Cambiar estado del cliente', disableClose: true, data: result, action: 'Editar'},
        height: 'auto',
        width: '40%'
      });
      dialogRef.afterClosed().subscribe(result => {
        if (!result) {
          return;
        }
        this.restService.update<string>(id, result, this.datos.controlador, {}, 'estatusCliente').subscribe(() => {
          this.dialogService.snack('success', '¡¡ Estatus actualizado!!');
          this.loadData();
        }, error => {
          if (error._embedded !== undefined) {
            this.dialogService.snack('danger', 'Error al actualizar');
          }
        });
        this.refreshTable();
      });
    });

  }
}


export class registros extends DataSource<Contrataciones> {
  filterChange = new BehaviorSubject('');

  get filter(): string {
    return this.filterChange.value;
  }

  set filter(filter: string) {
    this.filterChange.next(filter);
  }

  filteredData: Contrataciones[] = [];
  renderedData: Contrataciones[] = [];

  constructor(private ds: RestService, private paginator: MatPaginator, private _sort: MatSort, private controller: string) {
    super();
    this.filterChange.subscribe(() => (this.paginator.pageIndex = 0));
  }

  disconnect() {
  }

  connect(): Observable<Contrataciones[]> {

    const displayDataChanges = [
      this.ds.dataChange, this._sort.sortChange, this.filterChange, this.paginator.page
    ];

    this.ds.getAdvancedTable<any>(this.controller, {'max': 100}, 'contratosFirmados');
    return merge(...displayDataChanges).pipe(map(() => {
        this.filteredData = this.ds.data.slice().filter((campo: Contrataciones) => {
          const searchStr = (
            campo.id +
            campo.folio +
            campo.regimenFiscal +
            campo.fechaEmision +
            campo.montoPrestamo +
            campo.total +
            campo.estatusContrato +
            campo.numeroContrato +
            campo.estatusCliente +
            campo.estatus
          ).toLowerCase();
          return searchStr.indexOf(this.filter.toLowerCase()) !== -1;
        });

        const sortedData = this.sortData(this.filteredData.slice());
        const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
        this.renderedData = sortedData.splice(startIndex, this.paginator.pageSize);
        return this.renderedData;
      })
    );
  }

  sortData(data: Contrataciones[]): Contrataciones[] {
    if (!this._sort.active || this._sort.direction === '') {
      return data;
    }
    return data.sort((a, b) => {
      let propertyA: number | Date | string = '';
      let propertyB: number | Date | string = '';
      switch (this._sort.active) {
        case 'id':
          [propertyA, propertyB] = [a.id, b.id]
          break;
        case 'folio':
          [propertyA, propertyB] = [a.folio, b.folio]
          break;
        case 'regimenFiscal':
          [propertyA, propertyB] = [a.regimenFiscal, b.regimenFiscal]
          break;
        case 'fechaEmision':
          [propertyA, propertyB] = [a.fechaEmision, b.fechaEmision]
          break;
        case 'montoPrestamo':
          [propertyA, propertyB] = [a.montoPrestamo, b.montoPrestamo]
          break;
        case 'estatus':
          [propertyA, propertyB] = [a.estatus, b.estatus]
          break;
        case 'estatusCliente':
          [propertyA, propertyB] = [a.estatusCliente, b.estatusCliente]
          break;
      }
      const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
      const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
      return ((valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1));
    });
  }
}
