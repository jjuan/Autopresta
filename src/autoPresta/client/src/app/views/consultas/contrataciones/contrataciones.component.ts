import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {_statusContratos, Contrataciones, Contrato, monto} from "../../../core/models/data.interface";
import {MatDialog} from "@angular/material/dialog";
import {RestService} from "../../../core/service/rest.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {GlobalService} from "../../../core/service/global.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {DialogService} from "../../../core/service/dialog.service";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {BehaviorSubject, fromEvent, merge, Observable} from "rxjs";
import Swal from "sweetalert2";
import {map} from "rxjs/operators";
import {Router} from "@angular/router";
import {DatePipe} from "@angular/common";
import {DateAdapter} from "@angular/material/core";

@Component({
  selector: 'app-contrataciones',
  templateUrl: './contrataciones.component.html',
  styleUrls: ['./contrataciones.component.sass']
})
export class ContratacionesComponent implements OnInit {
  datos = {
    modulo: 'Consultas',
    componente: 'Contrataciones',
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
    'actions'
  ];
  selection = new SelectionModel<Contrataciones>(true, []);
  dataSource: registros | null;
  formulario: FormGroup;

  constructor(public dialog: MatDialog, public restService: RestService, private snackBar: MatSnackBar, private router: Router, private dateAdapter: DateAdapter<Date>,
              private globalService: GlobalService, private formBuilder: FormBuilder, private dialogService: DialogService,  private datePipe: DatePipe) {
    this.dateAdapter.setLocale('en-GB'); //dd/MM/yyyy
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
    const date = new Date(), y = date.getFullYear(), m = date.getMonth();
    const firstDay = new Date(y, m, 1);
    const lastDay = new Date(y, m + 1, 0);
    this.formulario = this.restService.buildForm({
      fechaInicio: [firstDay, Validators.required],
      fechaFin: [lastDay, Validators.required],
    })
    this.loadData();
  }

  loadData() {
    const fechaInicio = this.datePipe.transform(this.formulario.get('fechaInicio').value, 'yyyy-MM-dd');
    const fechaFin = this.datePipe.transform(this.formulario.get('fechaFin').value, 'yyyy-MM-dd');
    // this.estatusContratos()

    this.restService.index<any>(this.datos.controlador, {fechaInicio: fechaInicio,fechaFin: fechaFin, 'max': 100}).subscribe(respuesta =>{

      this.conciliacionOperaciones = respuesta.data
      this.dataSource = new registros(this.restService, this.paginator, this.sort, respuesta.detalle);
      fromEvent(this.filter.nativeElement, 'keyup').subscribe(() => {
        if (!this.dataSource) {
          return;
        }
        this.dataSource.filter = this.filter.nativeElement.value;
      });

    })
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
    } else if (estatus == 'R') {
      label = 'Registrado'
    } else if (estatus == 'I') {
      label = 'Impreso'
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

  imprimir(row: Contrato, accion) {
    this.restService.index<any>(this.datos.controlador, {id: row.id}, accion).subscribe(r => {
      this.loadData()
      this.download(row.id, row.numeroContrato)
      this.dialogService.snack(accion == 'librarFolio' ? 'danger' : 'success', r.message)
    })
  }

  download(id: number, numeroContrato) {
    const _dominio = 'Reporte'
    const _observable = this.restService.getReport('contratoAutoPresta', _dominio, {id: id});
    return this.restService.printReport(_observable, 'Contrato AP #' + numeroContrato);
  }

  estatusContratos() {
    const fechaInicio = this.datePipe.transform(this.formulario.get('fechaInicio').value, 'yyyy-MM-dd');
    const fechaFin = this.datePipe.transform(this.formulario.get('fechaFin').value, 'yyyy-MM-dd');
    this.restService.index<_statusContratos>(this.datos.controlador, {fechaInicio: fechaInicio,
      fechaFin: fechaFin}, 'estatusContratos').subscribe(r => {
      this.conciliacionOperaciones = r
      console.log(this.conciliacionOperaciones)
    })
  }

  editar(id) {
    this.router.navigate(['/Contrataciones/Edicion-Contrato/'+id])
    // this.restService.edit<any>(id, this.datos.controlador, {}, 'edicionContrato').subscribe(r => {
    //   console.log(r)
    // })
  }
}


export class registros extends DataSource<Contrataciones> {
  filterChange = new BehaviorSubject('');
  data

  get filter(): string {
    return this.filterChange.value;
  }

  set filter(filter: string) {
    this.filterChange.next(filter);
  }

  filteredData: Contrataciones[] = [];
  renderedData: Contrataciones[] = [];

  constructor(private ds: RestService, private paginator: MatPaginator, private _sort: MatSort, private detalles) {
    super();
    this.filterChange.subscribe(() => (this.paginator.pageIndex = 0));
  }

  disconnect() {
  }

  connect(): Observable<Contrataciones[]> {

    const displayDataChanges = [
      this.ds.dataChange, this._sort.sortChange, this.filterChange, this.paginator.page
    ];
    this.ds.changeData(this.detalles)
    // console.log(this.ds)
    return merge(...displayDataChanges).pipe(map(() => {
        this.filteredData = this.ds.data.slice().filter((campo: Contrataciones) => {
          const searchStr = (
            campo.id +
            campo.folio +
            // campo.regimenFiscal +
            campo.fechaEmision +
            campo.montoPrestamo +
            campo.total +
            campo.titular +
            campo.representante +
            campo.estatus +
            campo.estatusLabel +
            campo.numeroContrato
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
      }
      const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
      const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
      return ((valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1));
    });
  }
}

