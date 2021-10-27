import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Clientes} from "../../../core/models/data.interface";
import {RestService} from "../../../core/service/rest.service";
import {FormBuilder} from "@angular/forms";
import {BehaviorSubject, fromEvent, merge, Observable} from "rxjs";
import {DialogService} from "../../../core/service/dialog.service";
import Swal from "sweetalert2";
import {GlobalService} from "../../../core/service/global.service";
import {MatPaginator} from "@angular/material/paginator";
import {DataSource, SelectionModel} from "@angular/cdk/collections";
import {MatDialog} from "@angular/material/dialog";
import {MatSort} from "@angular/material/sort";
import {MatSnackBar} from "@angular/material/snack-bar";
import {map} from "rxjs/operators";
import {FormClientesComponent} from "./form-clientes/form-clientes.component";

@Component({
  selector: 'app-clientes',
  templateUrl: './clientes.component.html',
  styleUrls: ['./clientes.component.sass']
})
export class ClientesComponent implements OnInit {
  datos = {
    modulo: 'Catalogos',
    componente: 'Clientes',
    icono: 'fas fa-folder-open',
    titulo: 'Clientes',
    controlador: 'Clientes'
  }
  displayedColumns = [
    'select',
    'nombres',
    'primerApellido',
    'segundoApellido',
    'genero',
    'rfc',
    // 'fechaDeNacimiento',
    'curp',
    // 'claveDeElector',
    // 'telefonoFijo',
    // 'telefonoCelular',
    // 'telefonoOficina',
    // 'correoElectronico',
    // 'dirTrabajo',
    // 'dirAdicional',
    // 'direccionPrincipal',
    // 'exterior',
    // 'interior',
    // 'cp',
    // 'colonia',
    // 'alcaldiaMunicipio',
    // 'entidad',
    'actions'
  ];
  selection = new SelectionModel<Clientes>(true, []);
  dataSource: registros | null;

  constructor(public dialog: MatDialog, public restService: RestService, private snackBar: MatSnackBar,
              private globalService: GlobalService, private formBuilder: FormBuilder, private dialogService: DialogService) {
  }

  @ViewChild(MatPaginator, {static: true}) paginator: MatPaginator;
  @ViewChild(MatSort, {static: true}) sort: MatSort;
  @ViewChild('filter', {static: true}) filter: ElementRef;

  ngOnInit(): void {
    this.loadData();
  }

  loadData() {
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

  save(row?: Clientes) {
    if (row) {
      this.restService.edit<Clientes>(row.id, this.datos.controlador).subscribe(result => {
        const dialogRef = this.dialog.open(FormClientesComponent, {
          data: {title: row.nombres, disableClose: true, data: result, action: 'Editar'}, height: 'auto', width: '40%'
        });
        dialogRef.afterClosed().subscribe(result => {
          if (!result) {
            return;
          }
          this.restService.update<string>(row.id, result, this.datos.controlador).subscribe(() => {
            this.dialogService.snack('success', '¡¡ ' + this.datos.titulo + ' Modificado!!');
            this.loadData();
          }, error => {
            if (error._embedded !== undefined) {
              this.dialogService.snack('danger', 'Error al modificar');
            }
          });
          this.refreshTable();
        });
      });
    } else {
      this.restService.create<Clientes>(this.datos.controlador).subscribe(result => {
        const dialogRef = this.dialog.open(FormClientesComponent, {
          data: {title: this.datos.titulo, disableClose: true, data: result, action: 'Agregar'},
          height: 'auto',
          width: '40%'
        });
        dialogRef.afterClosed().subscribe((result) => {
          if (!result) {
            return;
          }
          this.restService.save<string>(result, {}, this.datos.controlador).subscribe(() => {
            this.dialogService.snack('success', '¡' + this.datos.titulo + 'Agregado!');
            this.loadData();
          }, error => {
            if (error._embedded !== undefined) {
              this.dialogService.snack('danger', '¡Error al guardar!');
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
    this.isAllSelected() ? this.selection.clear() : this.dataSource.renderedData.forEach((row) => this.selection.select(row));
  }

  removeSelectedRows() {
    const totalSelect = this.selection.selected.length;
    this.selection.selected.forEach((item) => {
      const index: number = this.dataSource.renderedData.findIndex((d) => d === item);
      this.restService.dataChange.value.splice(index, 1);
      this.refreshTable();
      this.selection = new SelectionModel<Clientes>(true, []);
    });
    this.dialogService.snack('danger', totalSelect + ' registros eliminados');
  }

  delete(row: Clientes) {
    const html = '<div class="align-left mt-3">' +
      '<h5>Detalles del ' + this.datos.titulo.toLowerCase() + ': ' + row.id + '</h5>' +
      '<p><span class="font-weight-bold">Nombre: </span>' + row.nombres + '</p>' +
      '<p><span class="font-weight-bold">Primer Apellido: </span>' + row.primerApellido + '</p>' +
      '<p><span class="font-weight-bold">Segundo Apellido: </span>' + row.segundoApellido + '</p>' +
      '<p><span class="font-weight-bold">Genero: </span>' + row.genero + '</p>' +
      '<p><span class="font-weight-bold">RFC: </span>' + row.rfc + '</p>' +
      '<p><span class="font-weight-bold">Fecha de Nacimiento: </span>' + row.fechaDeNacimiento + '</p>' +
      '<p><span class="font-weight-bold">Curp: </span>' + row.curp + '</p>' +
      '<p><span class="font-weight-bold">Clave de Elector: </span>' + row.claveDeElector + '</p>' +
      '<p><span class="font-weight-bold">Telefono Fijo: </span>' + row.telefonoFijo + '</p>' +
      '<p><span class="font-weight-bold">Telefono Celular: </span>' + row.telefonoCelular + '</p>' +
      '<p><span class="font-weight-bold">Telefono Oficina: </span>' + row.telefonoOficina + '</p>' +
      '<p><span class="font-weight-bold">Correo Electronico: </span>' + row.correoElectronico + '</p>' +
      '<p><span class="font-weight-bold">Dir. Trabajo: </span>' + row.dirTrabajo + '</p>' +
      '<p><span class="font-weight-bold">Dir. Adicional: </span>' + row.dirAdicional + '</p>' +
      '<p><span class="font-weight-bold">Direccion Principal: </span>' + row.direccionPrincipal + '</p>' +
      '<p><span class="font-weight-bold">Exterior: </span>' + row.exterior + '</p>' +
      '<p><span class="font-weight-bold">Interior: </span>' + row.interior + '</p>' +
      '<p><span class="font-weight-bold">CP: </span>' + row.cp + '</p>' +
      '<p><span class="font-weight-bold">Colonia: </span>' + row.colonia + '</p>' +
      '<p><span class="font-weight-bold">Alcaldia/Municipio: </span>' + row.alcaldiaMunicipio + '</p>' +
      '<p><span class="font-weight-bold">Entidad: </span>' + row.entidad + '</p>' +
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
          this.dialogService.snack('success', '¡Registro eliminado!');
          this.loadData();
        }, () => this.dialogService.snack('danger', '¡Error al eliminar!, Este registro esta siendo utilizado'));
      }
    });
  }
}


export class registros extends DataSource<Clientes> {
  filterChange = new BehaviorSubject('');

  get filter(): string {
    return this.filterChange.value;
  }

  set filter(filter: string) {
    this.filterChange.next(filter);
  }

  filteredData: Clientes[] = [];
  renderedData: Clientes[] = [];

  constructor(private ds: RestService, private paginator: MatPaginator, private _sort: MatSort, private controller: string) {
    super();
    this.filterChange.subscribe(() => (this.paginator.pageIndex = 0));
  }

  disconnect() {
  }

  connect(): Observable<Clientes[]> {

    const displayDataChanges = [
      this.ds.dataChange, this._sort.sortChange, this.filterChange, this.paginator.page
    ];

    this.ds.getAdvancedTable<any>(this.controller, {'max': 100});
    return merge(...displayDataChanges).pipe(map(() => {
        this.filteredData = this.ds.data.slice().filter((campo: Clientes) => {
          const searchStr = (
            // campo.id +
            campo.nombres +
            campo.primerApellido +
            campo.segundoApellido +
            campo.genero +
            campo.rfc +
            campo.fechaDeNacimiento +
            campo.curp +
            campo.claveDeElector +
            campo.telefonoFijo +
            campo.telefonoCelular +
            campo.telefonoOficina +
            campo.correoElectronico +
            campo.dirTrabajo +
            campo.dirAdicional +
            campo.direccionPrincipal +
            campo.exterior +
            campo.interior +
            campo.cp +
            campo.colonia +
            campo.alcaldiaMunicipio +
            campo.entidad
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

  sortData(data: Clientes[]): Clientes[] {
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
        case 'nombres':
          [propertyA, propertyB] = [a.nombres, b.nombres]
          break;
        case 'primerApellido':
          [propertyA, propertyB] = [a.primerApellido, b.primerApellido]
          break;
        case 'segundoApellido':
          [propertyA, propertyB] = [a.segundoApellido, b.segundoApellido]
          break;
        case 'genero':
          [propertyA, propertyB] = [a.genero, b.genero]
          break;
        case 'rfc':
          [propertyA, propertyB] = [a.rfc, b.rfc]
          break;
        case 'fechaDeNacimiento':
          [propertyA, propertyB] = [a.fechaDeNacimiento, b.fechaDeNacimiento]
          break;
        case 'curp':
          [propertyA, propertyB] = [a.curp, b.curp]
          break;
        case 'claveDeElector':
          [propertyA, propertyB] = [a.claveDeElector, b.claveDeElector]
          break;
        case 'telefonoFijo':
          [propertyA, propertyB] = [a.telefonoFijo, b.telefonoFijo]
          break;
        case 'telefonoCelular':
          [propertyA, propertyB] = [a.telefonoCelular, b.telefonoCelular]
          break;
        case 'telefonoOficina':
          [propertyA, propertyB] = [a.telefonoOficina, b.telefonoOficina]
          break;
        case 'correoElectronico':
          [propertyA, propertyB] = [a.correoElectronico, b.correoElectronico]
          break;
        case 'direccionPrincipal':
          [propertyA, propertyB] = [a.direccionPrincipal, b.direccionPrincipal]
          break;
        case 'exterior':
          [propertyA, propertyB] = [a.exterior, b.exterior]
          break;
        case 'interior':
          [propertyA, propertyB] = [a.interior, b.interior]
          break;
        case 'cp':
          [propertyA, propertyB] = [a.cp, b.cp]
          break;
        case 'colonia':
          [propertyA, propertyB] = [a.colonia, b.colonia]
          break;
        case 'alcaldiaMunicipio':
          [propertyA, propertyB] = [a.alcaldiaMunicipio, b.alcaldiaMunicipio]
          break;
        case 'entidad':
          [propertyA, propertyB] = [a.entidad, b.entidad]
          break;
      }
      const valueA = isNaN(+propertyA) ? propertyA : +propertyA;
      const valueB = isNaN(+propertyB) ? propertyB : +propertyB;
      return ((valueA < valueB ? -1 : 1) * (this._sort.direction === 'asc' ? 1 : -1));
    });
  }
}
