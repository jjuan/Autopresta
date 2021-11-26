import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {CalificacionCliente} from "../../../core/models/data.interface";
import {MatDialog} from "@angular/material/dialog";
import {RestService} from "../../../core/service/rest.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {GlobalService} from "../../../core/service/global.service";
import {FormBuilder} from "@angular/forms";
import {DialogService} from "../../../core/service/dialog.service";
import {MatPaginator} from "@angular/material/paginator";
import {MatSort} from "@angular/material/sort";
import {BehaviorSubject, fromEvent, merge, Observable} from "rxjs";
import Swal from "sweetalert2";
import {map} from "rxjs/operators";
import {CalificacionClienteFormComponent} from "./calificacion-cliente-form/calificacion-cliente-form.component";

@Component({
  selector: 'app-calificacion-cliente',
  templateUrl: './calificacion-cliente.component.html',
  styleUrls: ['./calificacion-cliente.component.sass']
})
export class CalificacionClienteComponent implements OnInit {
  datos = {modulo: 'Catalogos', componente: 'Calificación Cliente', icono: 'fas fa-folder-open', titulo: 'CalificacionCliente', controlador: 'CalificacionCliente'}
  displayedColumns = [
    'select',
    'nombre',
    'descripcion',
    'actions'
  ];
  selection = new SelectionModel<CalificacionCliente>(true, []);
  dataSource: registros | null;
  constructor(public dialog: MatDialog, public restService: RestService, private snackBar: MatSnackBar,
              private globalService: GlobalService, private formBuilder: FormBuilder, private dialogService: DialogService) { }
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild('filter', { static: true }) filter: ElementRef;

  ngOnInit(): void {
    this.loadData();
  }
  loadData() {
    this.dataSource = new registros(this.restService, this.paginator, this.sort, this.datos.controlador);
    fromEvent(this.filter.nativeElement, 'keyup').subscribe(() => {
      if (!this.dataSource) { return; }
      this.dataSource.filter = this.filter.nativeElement.value;
    });
  }
  private refreshTable() { this.paginator._changePageSize(this.paginator.pageSize); }

  save(row?: CalificacionCliente) {
    if (row){
      this.restService.edit<CalificacionCliente>(row.id, this.datos.controlador).subscribe(result => {
        const dialogRef = this.dialog.open(CalificacionClienteFormComponent, {
          data: { title: row.nombre , disableClose: true, data: result, action: 'Editar' }, height: 'auto', width: '40%'
        });
        dialogRef.afterClosed().subscribe(result => {
          if (!result) { return; }
          this.restService.update<string>(row.id, result, this.datos.controlador).subscribe(() => {
            this.dialogService.snack( 'success', '¡¡ ' + this.datos.titulo + ' Modificado!!');
            this.loadData();
          }, error => {
            if (error._embedded !== undefined) {
              this.dialogService.snack( 'danger', 'Error al modificar');
            }});
          this.refreshTable();
        });
      });
    } else {
      this.restService.create<CalificacionCliente>( this.datos.controlador).subscribe(result => {
        const dialogRef = this.dialog.open(CalificacionClienteFormComponent, {
          data: { title: this.datos.titulo, disableClose: true, data: result, action: 'Agregar' }, height: 'auto', width: '40%'
        });
        dialogRef.afterClosed().subscribe((result) => {
          if (!result) { return; }
          this.restService.save<string>(result, {}, this.datos.controlador).subscribe(() => {
            this.dialogService.snack( 'success', '¡' + this.datos.titulo + 'Agregado!');
            this.loadData();
          }, error => {
            if (error._embedded !== undefined) {
              this.dialogService.snack( 'danger', '¡Error al guardar!');
            }
          });
          this.refreshTable();
        });
      });
    }
  }
  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.renderedData.length;
    return numSelected === numRows;
  }

  masterToggle() {
    this.isAllSelected() ? this.selection.clear() : this.dataSource.renderedData.forEach((row) => this.selection.select(row) );
  }

  removeSelectedRows() {
    const totalSelect = this.selection.selected.length;
    this.selection.selected.forEach((item) => {
      const index: number = this.dataSource.renderedData.findIndex( (d) => d === item );
      this.restService.dataChange.value.splice(index, 1);
      this.refreshTable();
      this.selection = new SelectionModel<CalificacionCliente>(true, []);
    });
    this.dialogService.snack( 'danger', totalSelect + ' registros eliminados' );
  }

  delete(row: CalificacionCliente) {
    const html ='<div class="align-left mt-3">'+
      '<h5>Detalles del ' + this.datos.titulo.toLowerCase() + ': ' + row.id + '</h5>'+
      '<p><span class="font-weight-bold">Nombre: </span>' + row.nombre + '</p>' +
      '<p><span class="font-weight-bold">Slug: </span>' + row.descripcion + '</p>' +
      '</div>';
    Swal.fire({
      titleText: this.datos.titulo,
      html: '¿Esta seguro de eliminar el registro: ' + row.id + '?<br>' + html,
      icon: 'warning',
      showConfirmButton: true,
      confirmButtonText: 'Confirmar',
      showDenyButton: true,
      denyButtonText: 'Cancelar',
      allowOutsideClick: false
    }).then(r => {
      if (r.value) {
        this.restService.delete<string>(row.id, {}, this.datos.controlador).subscribe(() => {
          this.dialogService.snack( 'success', '¡Registro eliminado!');
          this.loadData();
        }, () => this.dialogService.snack( 'danger', '¡Error al eliminar!, Este registro esta siendo utilizado' ));
      }
    });
  }
}


export class registros extends DataSource<CalificacionCliente> {
  filterChange = new BehaviorSubject('');
  get filter(): string { return this.filterChange.value; }
  set filter(filter: string) { this.filterChange.next(filter); }
  filteredData: CalificacionCliente[] = [];
  renderedData: CalificacionCliente[] = [];
  constructor( private ds: RestService, private paginator: MatPaginator, private _sort: MatSort, private controller: string) {
    super();
    this.filterChange.subscribe(() => (this.paginator.pageIndex = 0));
  }

  disconnect() {
  }

  connect(): Observable<CalificacionCliente[]> {

    const displayDataChanges = [
      this.ds.dataChange, this._sort.sortChange, this.filterChange, this.paginator.page
    ];

    this.ds.getAdvancedTable<any>(this.controller,{'max': 100});
    return merge(...displayDataChanges).pipe( map(() => {
        this.filteredData = this.ds.data.slice().filter((campo: CalificacionCliente) => {
          const searchStr = (
            campo.id +
            campo.nombre+
            campo.descripcion
          ).toLowerCase();
          return searchStr.indexOf(this.filter.toLowerCase()) !== -1;
        });

        const sortedData = this.sortData(this.filteredData.slice());
        const startIndex = this.paginator.pageIndex * this.paginator.pageSize;
        this.renderedData = sortedData.splice( startIndex, this.paginator.pageSize );
        return this.renderedData;
      })
    );
  }

  sortData(data: CalificacionCliente[]): CalificacionCliente[] {
    if (!this._sort.active || this._sort.direction === '') { return data; }
    return data.sort((a, b) => {
      let propertyA: number| Date | string = '';
      let propertyB: number| Date | string = '';
      switch (this._sort.active) {
        case 'id':
          [propertyA, propertyB] = [a.id, b.id]
          break;
        case 'nombre':
          [propertyA, propertyB] = [a.nombre, b.nombre]
          break;
        case 'descripcion':
          [propertyA, propertyB] = [a.descripcion, b.descripcion]
          break;
      }
      const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
      const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
      return ( (valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1) );
    });
  }
}
