import {Component, ElementRef, Inject, OnInit, ViewChild} from '@angular/core';
import {Combo, conciliacionContratosTable} from "../../../../../core/models/data.interface";
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from "@angular/material/dialog";
import {HttpClient} from "@angular/common/http";
import {GlobalService} from "../../../../../core/service/global.service";
import {RestService} from "../../../../../core/service/rest.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
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
  displayedColumns = ['select', 'contrato', 'titular', 'parcialidad', 'fecha', 'monto'];

  action: string;
  dialogTitle: string;
  titulo

  formulario: FormGroup;
  // formularioBusqueda: FormGroup;
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
  public formaConciliacionCombo: Combo[] = [
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
    public dialogRef: MatDialogRef<ConciliacionManualMovimientosComponent>, @Inject(MAT_DIALOG_DATA) public data: any, private http: HttpClient,
    public httpClient: HttpClient, private globalService: GlobalService, public dialog: MatDialog, private datePipe: DatePipe,
    public advanceTableService: RestService, private snackBar: MatSnackBar, private fBuilder: FormBuilder,
    public restService: RestService,
  ) {
  }

  ngOnInit(): void {
    let firstDay = (new Date().getMonth() + 1) + "/01/" + new Date().getFullYear();
    let lastDate = new Date(new Date().getFullYear(), new Date().getMonth() + 1, 0);
    let lastDay = (lastDate.getMonth() + 1) + "/" + lastDate.getDate() + "/" + lastDate.getFullYear();

    this.advanceTableService.initService(this._dominio);
    this.action = this.data.action;
    this.saldo = this.data.detalle!=undefined?this.data.detalle.diferencia:this.data.info.monto
    this.etiqueta = this.data.detalle!=undefined?this.data.detalle.descripcionDiferencia:'Saldo a favor'
    this.dialogTitle = this.data.cabecera
    this.titulo = this.data.title
    this.formulario = this.restService.buildForm({
      formaConciliacion: [''],
      campo: [''],
      conciliacionParcial: [true],
      // });
      // this.formularioBusqueda = this.restService.buildForm({
      fechaInicio: [new Date(firstDay +" 00:00:00"), Validators.required],
      fechaFin: [new Date(lastDay +" 00:00:00"), Validators.required]
    })
    this.loadData();
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  showNotification(colorName, text, placementFrom, placementAlign) {
    this.snackBar.open(text, '', {
      duration: 2000,
      verticalPosition: placementFrom,
      horizontalPosition: placementAlign,
      panelClass: colorName
    });
  }

  public confirmAdd(): void {
    let conciliacionDetalles = [];
    let parcialidades = []
    this.selection.selected.forEach((item) => {
      conciliacionDetalles.push([
        {
          movimiento: this.data.info.folio,
          folioOperacion: item.folio,
          tipoOperacion: item.clase
        }
      ])
      parcialidades.push({item})
    });
    this.dialogRef.close({
      detalles: conciliacionDetalles,
      parcialidades: parcialidades,
      movimiento: this.data.info,
      saldo: this.saldo.toFixed(2),
      etiqueta: this.etiqueta,
      porMovimientos: false,
      formaConciliacion: this.formulario.get('formaConciliacion').value,
      conciliacionParcial: this.formulario.get('conciliacionParcial').value,
      campo: this.formulario.get('campo').value,
      montoMovimientos: this.data.info.monto.toFixed(2),
      montoParcialidades: this.montoParcialidades.toFixed(2)
    });
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.renderedData.length;
    return numSelected === numRows;
  }
  desconciliarMovimiento(p: any, texto: string) {
    const formData = new FormData();
    formData.append('id', p.id);
    this.http.post(this.globalService.BASE_API_URL + 'Conciliaciones/eliminarConciliacionDetalle', formData, {
      headers: {
        'Authorization': 'Bearer=' + this.globalService.getAuthToken()
      }
    }).subscribe(r => {
      // this.showNotification('snackbar-danger', texto, 'bottom', 'center');
      // this.dialogRef.close()
      console.log(this.data)
      this.advanceTableService.index<any>(this._dominio, {
        folio: this.data.info.folio,
        clase: this.data.info.clase
      }, 'verConciliacion').subscribe(r => {
        this.data.detalle = r[0]

        this.saldo = this.data.detalle.diferencia
        this.etiqueta = this.data.detalle.descripcionDiferencia
        this.actualizarMonto()
      })
    });
  }

  masterToggle() {
    this.isAllSelected() ? this.selection.clear() : this.dataSource.renderedData.forEach((row) => this.selection.select(row))
  }

  public loadData() {
    this.db = new RestService(this.httpClient, this.globalService, this.fBuilder);
    this.dataSource = new BancosDataSource(this.db, this.paginator, this.sort, this._dominio,
      this.datePipe.transform(this.formulario.get('fechaInicio').value, 'yyyy-MM-dd'),
      this.datePipe.transform(this.formulario.get('fechaFin').value, 'yyyy-MM-dd'),
      // new Date(this.formulario.get('fechaInicio').value).toJSON(),
      // new Date(this.formulario.get('fechaFin').value).toJSON(),
      false, this.data.esDetalle);
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
    let monto = this.data.detalle!=undefined?this.data.detalle.diferencia:this.data.info.monto
    if (monto == this.montoParcialidades) {
      this.etiqueta = 'Liquidado'
      this.saldo = 0
    } else if (monto > this.montoParcialidades && this.montoParcialidades > 0) {
      this.etiqueta = 'Saldo a favor'
      this.saldo = monto - this.montoParcialidades
    } else if (monto < this.montoParcialidades) {
      this.etiqueta = 'Saldo en contra'
      this.saldo = this.montoParcialidades - monto
    } else if (this.montoParcialidades == 0) {
      this.saldo = monto
      this.etiqueta = 'Saldo a favor'
    }
  }

  Cerrar() {
    this.http.post(this.globalService.BASE_API_URL + 'Conciliaciones/cerrarConciliacion', {id: this.data.info.folio}, {
      headers: {
        'Authorization': 'Bearer=' + this.globalService.getAuthToken()
      }
    }).subscribe(() => this.dialogRef.close())
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
              private _dominio: string, private fechaInicio, private fechaFin, private cargoAbono, private esDetalle) {
    super();
    this._filterChange.subscribe(() => (this._paginator.pageIndex = 0));
  }

  connect(): Observable<conciliacionContratosTable[]> {
    const displayDataChanges = [this._dataSource.dataChange, this._sort.sortChange, this._filterChange, this._paginator.page];
    if (this.fechaInicio != null && this.fechaFin != null) {
      this._dataSource.getAdvancedTable<any>(this._dominio, this.esDetalle ?
          {} :
          {fechaInicio: this.fechaInicio, fechaFin: this.fechaFin, cargoAbono: this.cargoAbono, conciliados: false},
        'cargarParcialidadesManualNueva');
    }

    return merge(...displayDataChanges).pipe(map(() => {
        this.filteredData = this._dataSource.data.slice().filter((advanceTable: conciliacionContratosTable) => {
          const searchStr = (
            advanceTable.folio +
            advanceTable.contrato +
            advanceTable.parcialidad +
            advanceTable.titular +
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
