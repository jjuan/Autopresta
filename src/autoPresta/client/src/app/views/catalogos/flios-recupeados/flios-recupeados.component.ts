import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {FoliosRecuerados} from "../../../core/models/data.interface";
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

@Component({
  selector: 'app-flios-recupeados',
  templateUrl: './flios-recupeados.component.html',
  styleUrls: ['./flios-recupeados.component.sass']
})
export class FliosRecupeadosComponent implements OnInit {

  datos = {modulo: 'Catalogos', componente: 'Folios Recuperados', icono: 'fas fa-folder-open', titulo: 'Folio Recuperado', controlador: 'FoliosRecuperados'}
  displayedColumns = [
    'select',
    'cveTipo',
    'folio',
    'actions'
  ];
  selection = new SelectionModel<FoliosRecuerados>(true, []);
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

  save(row?: FoliosRecuerados) {
    if (row){
      // this.restService.edit<FoliosRecuerados>(row.cveTipo, this.datos.controlador).subscribe(result => {
      //   const dialogRef = this.dialog.open(FormFoliosRecueradossComponent, {
      //     data: { title: row.cveTipo , disableClose: true, data: result, action: 'Editar' }, height: 'auto', width: '40%'
      //   });
      //   dialogRef.afterClosed().subscribe(result => {
      //     if (!result) { return; }
      //     this.restService.update<string>(row.cveTipo, result, this.datos.controlador).subscribe(() => {
      //       this.dialogService.snack( 'success', '¡¡ ' + this.datos.titulo + ' Modificado!!');
      //       this.loadData();
      //     }, error => {
      //       if (error._embedded !== undefined) {
      //         this.dialogService.snack( 'danger', 'Error al modificar');
      //       }});
      //     this.refreshTable();
      //   });
      // });
    } else {
      // this.restService.create<FoliosRecuerados>( this.datos.controlador).subscribe(result => {
      //   const dialogRef = this.dialog.open(FormFoliosRecueradossComponent, {
      //     data: { title: this.datos.titulo, disableClose: true, data: result, action: 'Agregar' }, height: 'auto', width: '40%'
      //   });
      //   dialogRef.afterClosed().subscribe((result) => {
      //     if (!result) { return; }
      //     this.restService.save<string>(result, {}, this.datos.controlador).subscribe(() => {
      //       this.dialogService.snack( 'success', '¡' + this.datos.titulo + 'Agregado!');
      //       this.loadData();
      //     }, error => {
      //       if (error._embedded !== undefined) {
      //         this.dialogService.snack( 'danger', '¡Error al guardar!');
      //       }
      //     });
      //     this.refreshTable();
      //   });
      // });
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
      this.selection = new SelectionModel<FoliosRecuerados>(true, []);
    });
    this.dialogService.snack( 'danger', totalSelect + ' registros eliminados' );
  }

  delete(row: FoliosRecuerados) {
    const html ='<div class="align-left mt-3">'+
      '<h5>Detalles del ' + this.datos.titulo.toLowerCase() + ': ' + row.folio + '</h5>'+
      '<p><span class="font-weight-bold">Clave: </span>' + row.folio + '</p>' +
      '</div>';
    Swal.fire({
      titleText: this.datos.titulo,
      html: '¿Esta seguro de eliminar el registro: ' + row.folio + '?<br>' + html,
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


export class registros extends DataSource<FoliosRecuerados> {
  filterChange = new BehaviorSubject('');
  get filter(): string { return this.filterChange.value; }
  set filter(filter: string) { this.filterChange.next(filter); }
  filteredData: FoliosRecuerados[] = [];
  renderedData: FoliosRecuerados[] = [];
  constructor( private ds: RestService, private paginator: MatPaginator, private _sort: MatSort, private controller: string) {
    super();
    this.filterChange.subscribe(() => (this.paginator.pageIndex = 0));
  }

  disconnect() {
  }

  connect(): Observable<FoliosRecuerados[]> {

    const displayDataChanges = [
      this.ds.dataChange, this._sort.sortChange, this.filterChange, this.paginator.page
    ];

    this.ds.getAdvancedTable<any>(this.controller,{'max': 100});
    return merge(...displayDataChanges).pipe( map(() => {
        this.filteredData = this.ds.data.slice().filter((campo: FoliosRecuerados) => {
          const searchStr = (
            campo.cveTipo +
            campo.folio
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

  sortData(data: FoliosRecuerados[]): FoliosRecuerados[] {
    if (!this._sort.active || this._sort.direction === '') { return data; }
    return data.sort((a, b) => {
      let propertyA: number| Date | string = '';
      let propertyB: number| Date | string = '';
      switch (this._sort.active) {
        case 'cveTipo':
          [propertyA, propertyB] = [a.cveTipo, b.cveTipo]
          break;
        case 'folio':
          [propertyA, propertyB] = [a.folio, b.folio]
          break;
      }
      const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
      const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
      return ( (valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1) );
    });
  }
}
