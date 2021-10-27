import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import Swal from "sweetalert2";
import {Portafolios} from "../../../core/models/data.interface";
import {FormBuilder} from "@angular/forms";
import {BehaviorSubject, fromEvent, merge, Observable} from "rxjs";
import {DialogService} from "../../../core/service/dialog.service";
import {GlobalService} from "../../../core/service/global.service";
import {MatPaginator} from "@angular/material/paginator";
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {MatDialog} from "@angular/material/dialog";
import {MatSort} from "@angular/material/sort";
import {FormPortafoliosComponent} from "./form-portafolios/form-portafolios.component";
import {RestService} from "../../../core/service/rest.service";
import {MatSnackBar} from "@angular/material/snack-bar";
import {map} from "rxjs/operators";

@Component({
  selector: 'app-portafolios',
  templateUrl: './portafolios.component.html',
  styleUrls: ['./portafolios.component.sass']
})
export class PortafoliosComponent implements OnInit {

  datos = {modulo: 'Catalogos', componente: 'Portafolios', icono: 'fas fa-folder-open', titulo: 'Portafolios', controlador: 'Portafolios'}
  displayedColumns = [
    'select',
    'cvePortafolio',
    'descripcion',
    'fecha',
    'mercados',
    // 'actions'
  ];
  selection = new SelectionModel<Portafolios>(true, []);
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

  save(row?: Portafolios) {
    if (row){
      this.restService.edit<Portafolios>(row.cvePortafolio, this.datos.controlador).subscribe(result => {
        const dialogRef = this.dialog.open(FormPortafoliosComponent, {
          data: { title: row.descripcion , disableClose: true, data: result, action: 'Editar' }, height: 'auto', width: '40%'
        });
        dialogRef.afterClosed().subscribe(result => {
          if (!result) { return; }
          this.restService.update<string>(row.cvePortafolio, result, this.datos.controlador).subscribe(() => {
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
      this.restService.create<Portafolios>( this.datos.controlador).subscribe(result => {
        const dialogRef = this.dialog.open(FormPortafoliosComponent, {
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
      this.selection = new SelectionModel<Portafolios>(true, []);
    });
    this.dialogService.snack( 'danger', totalSelect + ' registros eliminados' );
  }

  delete(row: Portafolios) {
    const html ='<div class="align-left mt-3">'+
      '<h5>Detalles del ' + this.datos.titulo.toLowerCase() + ': ' + row.cvePortafolio + '</h5>'+
      '<p><span class="font-weight-bold">cvePortafolio: </span>' + row.cvePortafolio + '</p>' +
      '<p><span class="font-weight-bold">descripcion: </span>' + row.descripcion + '</p>' +
      '<p><span class="font-weight-bold">fecha: </span>' + row.fecha + '</p>' +
      '<p><span class="font-weight-bold">mercados: </span>' + row.mercados + '</p>' +
      '</div>';
    Swal.fire({
      titleText: this.datos.titulo,
      html: '¿Esta seguro de eliminar el registro: ' + row.cvePortafolio + '?<br>' + html,
      icon: 'warning',
      showConfirmButton: true,
      confirmButtonText: 'Confirmar',
      showDenyButton: true,
      denyButtonText: 'Cancelar',
      allowOutsideClick: false
    }).then(r => {
      if (r.value) {
        this.restService.delete<string>(row.cvePortafolio, {}, this.datos.controlador).subscribe(() => {
          this.dialogService.snack( 'success', '¡Registro eliminado!');
          this.loadData();
        }, () => this.dialogService.snack( 'danger', '¡Error al eliminar!, Este registro esta siendo utilizado' ));
      }
    });
  }
}


export class registros extends DataSource<Portafolios> {
  filterChange = new BehaviorSubject('');
  get filter(): string { return this.filterChange.value; }
  set filter(filter: string) { this.filterChange.next(filter); }
  filteredData: Portafolios[] = [];
  renderedData: Portafolios[] = [];
  constructor( private ds: RestService, private paginator: MatPaginator, private _sort: MatSort, private controller: string) {
    super();
    this.filterChange.subscribe(() => (this.paginator.pageIndex = 0));
  }

  disconnect() {
  }

  connect(): Observable<Portafolios[]> {

    const displayDataChanges = [
      this.ds.dataChange, this._sort.sortChange, this.filterChange, this.paginator.page
    ];

    this.ds.getAdvancedTable<any>(this.controller,{'max': 100});
    return merge(...displayDataChanges).pipe( map(() => {
        this.filteredData = this.ds.data.slice().filter((campo: Portafolios) => {
          const searchStr = (
            campo.cvePortafolio +
            campo.descripcion +
            campo.fecha +
            campo.mercados
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

  sortData(data: Portafolios[]): Portafolios[] {
    if (!this._sort.active || this._sort.direction === '') { return data; }
    return data.sort((a, b) => {
      let propertyA: number| Date | string = '';
      let propertyB: number| Date | string = '';
      switch (this._sort.active) {
        case 'cvePortafolio':
          [propertyA, propertyB] = [a.cvePortafolio, b.cvePortafolio]
          break;
        case 'descripcion':
          [propertyA, propertyB] = [a.descripcion, b.descripcion]
          break;
        case 'fecha':
          [propertyA, propertyB] = [a.fecha, b.fecha]
          break;
      }
      const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
      const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
      return ( (valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1) );
    });
  }

  myFilter = (d: Date): boolean => {
    const day = d.getDay();
    return day !== 0 && day !== 6;
  }
}
